
def run(datafile, labelfile):

    # data_names = ["breast_cancer", "climate", "hill_valley",
    #              "ionosphere", "micromass", "qsar",
    #              "test", "test2", "test_fscore", "project"]
    # data_name = data_names[9]
    # print("using ", data_name)
    # datafile = "./data/" + data_name + "/" + data_name + ".data"
    # labelfile = "./data/" + data_name + "/" + data_name + ".labels"

    nn = 0
    np = 0

    # load labels

    # print("loading labels...")

    labels = []
    with open(labelfile) as f:
        for line in f:
            a = line.split()
            label = int(a[0])
            if label == 0:
                nn += 1
            else:
                np += 1
            labels.append(label)

    # print("calculating means...")

    cols = 0

    # get the number of columns
    with open(datafile) as f:
        line = f.readline()
        a = line.split()
        cols = len(a)

    print("cols: ", cols)

    mn = [0]*cols
    mp = [0]*cols

    i = 0
    with open(datafile) as f:
        for line in f:
            a = line.split()
            for j in range(len(a)):
                xij = float(a[j])
                if labels[i] == 0:
                    mn[j] += xij
                else:
                    mp[j] += xij

            if i % 100 == 0:
                print(i/8000*100)
            i += 1

    m = [0]*cols
    for j in range(cols):
        m[j] = (mn[j] + mp[j])/(nn + np)
        mn[j] /= nn
        mp[j] /= np

    # print("calculating variances...")

    vn = [0]*cols
    vp = [0]*cols

    i = 0
    with open(datafile) as f:
        for line in f:
            a = line.split()
            for j in range(len(a)):
                xij = float(a[j])
                if labels[i] == 0:
                    vn[j] += (xij - mn[j]) ** 2
                else:
                    vp[j] += (xij - mp[j]) ** 2

            if i % 100 == 0:
               print(i/8000*100)
            i += 1

    for j in range(cols):
        vn[j] /= nn - 1
        vp[j] /= np - 1

    print("calculating fscores...")

    fscores = []

    for j in range(cols):

        fj = (mn[j] - m[j])**2 + (mp[j] - m[j])**2
        fj /= vn[j] + vp[j]

        fscores.append((j, fj))

    fscores = sorted(fscores, key=lambda fs: fs[1], reverse=True)

    r = []
    for j in range(min(cols,15)):
        print(fscores[j][0])
        r.append(fscores[j][0])

    return r