import numpy as np
from PIL import Image
import imageio


def quadratic(a, b, c):
    t = b*b-4*a*c
    if t < 0:
        return np.array([])
    if t < 1E-3:
        return np.array([-b/(2*a)])
    t = np.sqrt(t)
    if b < 0:
        t = -0.5*(b-t)
    else:
        t = -0.5*(b+t)
    t0 = t/a
    t1 = c/t
    if t0 < t1:
        return np.array([t0, t1])
    return np.array([t1, t0])


def ray_sphere_intersection(ro, rd, radius):
    a = np.dot(rd, rd)
    b = 2*np.dot(ro, rd)
    c = np.dot(ro, ro) - radius*radius
    return quadratic(a, b, c)


class Atmosphere:
    def __init__(self, radius_earth, radius_atmosphere,
                        height_rayleigh, height_mie,
                        beta_rayleigh, beta_mie,
                        sun_direction, n_samples_sight, n_samples_light):
        self.radius_earth = radius_earth
        self.radius_atmosphere = radius_atmosphere
        self.height_rayleigh = height_rayleigh
        self.height_mie = height_mie
        self.beta_rayleigh = beta_rayleigh
        self.beta_mie = beta_mie
        self.sun_direction = sun_direction
        self.n_samples_sight = n_samples_sight
        self.n_samples_light = n_samples_light

    def get_color(self, ro, rd, tmin, tmax):

        # find the intersection of the ray with the outer shell of the atmosphere
        # here we use the full ray in case one is viewing from outside the atmosphere
        ts = ray_sphere_intersection(ro, rd, self.radius_atmosphere)
        if len(ts) == 0 or len(ts) == 1 or ts[1] < 0:
            return np.array([0.0, 0.0, 0.0])
        if ts[0] > tmin:
            tmin = ts[0]
        if ts[1] < tmax:
            tmax = ts[1]
        sls = (tmax-tmin)/self.n_samples_sight  # segment length sight

        # prepare variables for physics calculations
        odsr = 0.0  # optical depth sight rayleigh
        odsm = 0.0  # optical depth sight mie
        sum_rayleigh = np.array([0.0, 0.0, 0.0])
        sum_mie = np.array([0.0, 0.0, 0.0])
        for i in range(self.n_samples_sight):
            rts = ro + (i+0.5)*sls*rd
            hs = np.linalg.norm(rts) - self.radius_earth
            hr = np.exp(-hs/self.height_rayleigh)*sls
            hm = np.exp(-hs/self.height_mie)*sls
            odsr += hr
            odsm += hm

            tl = ray_sphere_intersection(rts, self.sun_direction, self.radius_atmosphere)
            sll = tl[1] / n_samples_light  # segment length light
            odlr = 0.0  # optical depth light rayleigh
            odlm = 0.0  # optical depth light mie
            valid = True
            for j in range(self.n_samples_light):
                rtl = rts + (j+0.5)*sll*self.sun_direction
                hl = np.linalg.norm(rtl) - self.radius_earth
                if hl < 0:
                    valid = False
                    break
                odlr += np.exp(-hl/height_rayleigh)*sll
                odlm += np.exp(-hl/height_mie)*sll

            if valid:
                tau = beta_rayleigh*(odsr + odlr) + beta_mie*1.1*(odsm + odlm)
                attenuation = np.exp(-tau)
                sum_rayleigh += hr*attenuation
                sum_mie += hm*attenuation

        mu = np.dot(rd, self.sun_direction)
        phase_r = 3.0/(16*np.pi)*(1.0+mu*mu)
        g = 0.76
        phase_m = 3.0/(8.0*np.pi)*((1.0 - g*g)*(1.0 + mu*mu)) / ((2.0 + g*g)*((1.0 + g*g - 2.0*g*mu)**1.5))
        return (sum_rayleigh*beta_rayleigh*phase_r + sum_mie*beta_mie*phase_m)*20


def spherical_to_rectangular(phi, theta):
    return np.array([np.sin(theta)*np.cos(phi),
                     np.cos(theta),
                     np.sin(theta)*np.sin(phi)])


def tonemap(v):
    if v < 1.413:
        return (v*0.38317)**(1.0/2.2)
    return 1-np.exp(-v)


def clamp(v):
    v *= 255.0
    if v < 0:
        return 0
    if v > 255:
        return 255
    return int(v)


def interpolate(a, b, t):
    return (1-t)*a + t*b


def render_fisheye(atmosphere, img):
    img_width, img_height = img.size
    pixels = img.load()
    for i in range(img_width):
        x = 2.0*(i+0.5)/(img_width-1.0)-1.0
        for j in range(img_height):
            y = 2*(j+0.5)/(img_height-1.0)-1.0
            z2 = x*x + y*y
            if z2 <= 1.0:
                phi = np.arctan2(y, x)
                theta = np.arccos(1-z2)
                ro = np.array([0, atmosphere.radius_earth+1, 0])
                rd = spherical_to_rectangular(phi, theta)
                c = atmosphere.get_color(ro, rd, 0, np.inf)
                # c[0] = tonemap(c[0])
                # c[1] = tonemap(c[1])
                # c[2] = tonemap(c[2])
                pixels[i,j] = (clamp(c[0]), clamp(c[1]), clamp(c[2]))
        print(float(i)/img_width*100)


def render_perspective(atmosphere, img):
    img_width, img_height = img.size
    pixels = img.load()
    aspect_ratio = img_width/float(img_height)
    fov = 65.0
    angle = np.tan(fov * np.pi / 180 * 0.5)
    ro = np.array([0, atmosphere.radius_earth + 1000, 30000])
    for i in range(img_width):
        for j in range(img_height):
            rdx = (2.0*(i/float(img_width))-1.0)*angle*aspect_ratio
            rdy = (1.0-2.0*(j/float(img_height)))*angle
            rd = np.array([rdx, rdy, 1.0])
            rd /= np.linalg.norm(rd)
            tmax = np.inf
            ts = ray_sphere_intersection(ro, rd, atmosphere.radius_earth)
            if len(ts) != 0 and ts[0] > 0:
                tmax = ts[0]
            c = atmosphere.get_color(ro, rd, 0, tmax)
            # c[0] = tonemap(c[0])
            # c[1] = tonemap(c[1])
            # c[2] = tonemap(c[2])
            pixels[i,j] = (clamp(c[0]), clamp(c[1]), clamp(c[2]))
        print(float(i) / img_width * 100)


if __name__ == '__main__':
    radius_earth = 6360e3
    radius_atmosphere = 6420e3
    height_rayleigh = 7994.0
    height_mie = 1200.0
    beta_rayleigh = np.array([3.8e-6, 13.5e-6, 33.1e-6])
    beta_mie = np.array([21e-6, 21e-6, 21e-6])
    angle = np.pi/2.5
    sun_direction = np.array([0.0, 0.0, 1.0])
    n_samples_sight = 16
    n_samples_light = 8
    atmosphere = Atmosphere(radius_earth, radius_atmosphere,
                            height_rayleigh, height_mie,
                            beta_rayleigh, beta_mie, sun_direction,
                            n_samples_sight, n_samples_light)
    #
    # img = Image.new('RGB', (640, 480), 0)
    # render_perspective(atmosphere, img)
    # img.show()


    n_images = 64
    for i in range(n_images):
        t = i/(n_images - 1.0)
        angle = interpolate(-np.pi/2.0, np.pi/2.0, t)
        atmosphere.sun_direction = np.array([0.0, np.cos(angle), -np.sin(angle)])
        img = Image.new('RGB', (512, 512), 0)
        render_fisheye(atmosphere, img)
        img.save("atmosphere"+str(i)+".jpg")

    images = []
    for i in range(n_images):
        images.append(imageio.imread("atmosphere"+str(i)+".jpg"))
    # kargs = {'duration': 5}
    # imageio.mimsave('atmosphere.gif', np.array(images), 'GIF', **kargs)
    imageio.mimsave('atmosphere.gif', images)
