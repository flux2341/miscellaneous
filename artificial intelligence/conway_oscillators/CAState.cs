using System;
using System.Drawing;
using System.IO;
namespace Wyrd
{
    public class CAState : IComparable<CAState>
    {
        public enum WrapMode { Extend, Toroidal };

        public bool[,] data;
        public int Width
        {
            get
            {
                return data.GetLength(0);
            }
        }
        public int Height
        {
            get
            {
                return data.GetLength(1);
            }
        }
        public WrapMode wrap_mode;
        public float fitness;
        public CAState(int w, int h)
        {
            wrap_mode = WrapMode.Extend;
            data = new bool[w, h];
            fitness = 0;
        }
        public CAState(int w, int h, WrapMode wm)
        {
            data = new bool[w, h];
            wrap_mode = wm;
        }
        public CAState(CAState c)
        {
            data = new bool[c.Width, c.Height];
            copyFrom(c);
        }
        public bool this[int i, int j]
        {
            get
            {
                return data[i, j];
            }
            set
            {
                data[i, j] = value;
            }
        }
        public void addPadding(int px, int py)
        {
            expand(Width + px, Height + py, true);
        }
        public void expand(int w, int h, bool center)
        {
            if (w <= Width || h <= Height)
            {
                Console.WriteLine("Invalid arguments for \"expand\"");
            }
            bool[,] dn = new bool[w, h];
            int xs, ys;
            if (center)
            {
                xs = w / 2 - Width/2;
                ys = h / 2 - Height/2;
            }
            else
            {
                Random r = new Random();
                xs = r.Next() % (w - Width);
                ys = r.Next() % (h - Height);
            }
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    dn[xs+i, ys+j] = data[i, j];
                }
            }
            data = dn;
        }
        public void copyFrom(CAState c)
        {
            if (Width != c.Width || Height != c.Height)
            {
                data = new bool[c.Width, c.Height];
            }
            wrap_mode = c.wrap_mode;
            fitness = c.fitness;
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    data[i, j] = c.data[i, j];
                }
            }
        }
        public void copyFrom(bool[,] b)
        {
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    data[i, j] = b[i, j];
                }
            }
        }


        public int numberOfLiveCells()
        {
            int r = 0;
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    if (data[i, j])
                    {
                        ++r;
                    }
                }
            }
            return r;
        }



        public bool[,] getSubstate(int i, int j)
        {
            bool[,] r = new bool[3, 3];
            r[0,0] = getValue(i-1, j-1);
            r[0,1] = getValue(i-1, j  );
            r[0,2] = getValue(i-1, j+1);
            r[1,0] = getValue(i  , j-1);
            r[1,1] = getValue(i  , j  );
            r[1,2] = getValue(i  , j+1);
            r[2,0] = getValue(i+1, j-1);
            r[2,1] = getValue(i+1, j  );
            r[2,2] = getValue(i+1, j+1);
            return r;
        }
        public void setSubstate(int i, int j, bool[,] s)
        {
            setValue(i-1, j-1, s[0,0]);
            setValue(i-1, j  , s[0,1]);
            setValue(i-1, j+1, s[0,2]);
            setValue(i  , j-1, s[1,0]);
            setValue(i  , j  , s[1,1]);
            setValue(i  , j+1, s[1,2]);
            setValue(i+1, j-1, s[2,0]);
            setValue(i+1, j  , s[2,1]);
            setValue(i+1, j+1, s[2,2]);
        }
        public void toLegalLocation(ref int i, ref int j)
        {
            if (wrap_mode == WrapMode.Extend)
            {
                if (i < 0 || i >= Width || j < 0 || j >= Height)
                {
                    i = -1;
                    j = -1;
                }
            }
            else if (wrap_mode == WrapMode.Toroidal)
            {
                while (i < 0)
                {
                    i += Width;
                }
                while (i >= Width)
                {
                    i -= Width;
                }
                while (j < 0)
                {
                    j += Height;
                }
                while (j >= Height)
                {
                    j -= Height;
                }
            }
        }
        public bool getValue(int i, int j)
        {
            toLegalLocation(ref i, ref j);
            if (i == -1)
            {
                return false;
            }
            return data[i, j];
        }

        public void setValue(int i, int j, bool v)
        {
            toLegalLocation(ref i, ref j);
            if (i != -1)
            {
                data[i, j] = v;
            }
        }

        public void setAll(bool v)
        {
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    data[i, j] = v;
                }
            }
        }
        public void randomize(Random rng)
        {
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    data[i, j] = (rng.Next()%2 == 0)? false: true;
                }
            }
        }


        public int getDifference(CAState s)
        {
            int d = 0;
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    if (data[i,j] != s.data[i,j])
                    {
                        ++d;
                    }
                }
            }
            return d;
        }
        public float getRelativeDifference(CAState s)
        {
            return (float)(getDifference(s))/(Width*Height);
        }


        public override String ToString()
        {
            String r = "";
            for (int i=0; i<Width; ++i)
            {
                r += '[';
                for (int j=0; j<Height; ++j)
                {
                    r += data[i, j]? 'X': '_';
                }
                r += "]\n";
            }
            return r;
        }


        public Bitmap toImage(int w, int h)
        {
            int sx = w / Width;
            int sy = h / Height;
            Bitmap r = new Bitmap(w, h);
            Graphics g = Graphics.FromImage(r);
            for (int i=0; i<Width; ++i)
            {
                for (int j=0; j<Height; ++j)
                {
                    Brush brush = (data[i, j])? Brushes.Black: Brushes.White;
                    g.FillRectangle(brush, new Rectangle(sx*i, sy*j, sx, sy));
                }
            }
            for (int i=0; i<Width; ++i)
            {
                g.DrawLine(Pens.LightGray, i*sx, 0, i*sx, h);
            }
            for (int j=0; j<Height; ++j)
            {
                g.DrawLine(Pens.LightGray, 0, j*sy, w, j*sy);
            }
            return r;
        }





        public int CompareTo(CAState other)
        {
            if (fitness < other.fitness)
            {
                return -1;
            }
            else if (fitness > other.fitness)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }



        


        public static String getRandomOscillatorName()
        {
            DirectoryInfo di = new DirectoryInfo("./oscillators/");
            FileInfo[] fi = di.GetFiles("*.*");
            Random random = new Random();
            String file_name = fi[random.Next() % fi.Length].Name;
            return file_name.Substring(0, file_name.LastIndexOf('.'));
        }
        public static CAState loadOscillatorFromName(String name)
        {
            return loadOscillator("./oscillators/" + name + ".cells");
        }
        public static CAState loadOscillator(String path)
        {
            String[] lines = System.IO.File.ReadAllLines(path);
            int w=0, h=0;
            for (int i=0; i<lines.Length; ++i)
            {
                if (lines[i].Length == 0 || lines[i][0] != '!')
                {
                    if (lines[i].Length > w)
                    {
                        w = lines[i].Length;
                    }
                    ++h;
                }
            }
            
            CAState r = new CAState(w, h);
            for (int i=0, y=0; i<lines.Length; ++i)
            {
                if (lines[i] == "" || lines[i][0] != '!')
                {
                    for (int x=0; x<w; ++x)
                    {
                        if (x < lines[i].Length)
                        {
                            r[x, y] = (lines[i][x] == 'O');
                        }
                        else
                        {
                            r[x, y] = false; 
                        }
                    }
                    ++y;
                }
            }
            return r;
        }
    }
}
