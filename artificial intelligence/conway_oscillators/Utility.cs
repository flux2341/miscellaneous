using System;
using System.Text;
using System.IO;
using System.Drawing;
namespace Wyrd
{
    public static class Utility
    {
        public static void getStatistics(CAState[] population, out float mean, out float variance, out float min, out float max)
        {
            min = float.PositiveInfinity;
            max = float.NegativeInfinity;

            mean = 0;
            for (int i=0; i<population.Length; ++i)
            {
                mean += population[i].fitness;
                if (population[i].fitness < min)
                {
                    min = population[i].fitness;
                }
                if (population[i].fitness > max)
                {
                    max = population[i].fitness;
                }
            }
            mean /= population.Length;

            variance = 0;
            for (int i=0; i<population.Length; ++i)
            {
                float t = population[i].fitness - mean;
                variance += t*t;
            }
            variance /= population.Length;
        }




        public static void printOscillator(CAState c, CARuleSet rule_set)
        {
            CAState temp0 = new CAState(c);
            CAState temp1 = new CAState(c);
            while (true)
            {
                Console.WriteLine(temp0.ToString());
                Console.WriteLine();
                rule_set.getTransition(temp0, temp1);
                if (temp1.getRelativeDifference(c) == 0)
                {
                    break;
                }
                temp0.copyFrom(temp1);
            }
        }



        public static void saveImages(CAState c, CARuleSet rule_set, String path)
        {
            CAState temp0 = new CAState(c);
            CAState temp1 = new CAState(c);
            int i = 0;
            while (true)
            {
                Bitmap image = temp0.toImage(c.Width*100, c.Height*100);
                image.Save(path + "/" + i + ".bmp");
                rule_set.getTransition(temp0, temp1);
                if (temp1.getRelativeDifference(c) == 0)
                {
                    break;
                }
                temp0.copyFrom(temp1);
                ++i;
            }
        }
        public static void writeFile(StringBuilder sb, String path, String name)
        {
            using (StreamWriter outfile = new StreamWriter(path + "/" + name + ".csv"))
            {
                outfile.Write(sb.ToString());
            }
        }
        public static String createNewFolder(String path)
        {
            String date_pattern = "M,d,yyyy hh,mm,ss tt";
            string new_path = System.IO.Path.Combine(path, DateTime.Now.ToLocalTime().ToString(date_pattern));
            string full_path = System.IO.Path.GetFullPath(new_path);
            System.IO.Directory.CreateDirectory(full_path);
            return new_path;
        }



    }
}
