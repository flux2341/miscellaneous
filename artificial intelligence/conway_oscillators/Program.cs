using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Text;

namespace Wyrd
{
    static class Program
    {
        [STAThread]
        static void Main()
        {

            //CAState.Width = 10;
            //CAState.Height = 10;
            //CAState.Wrap_Mode = CAState.WrapMode.Extend;
            //int population_size = 200;
            //int period = 2;
            //CARuleSet rule_set = new CARuleSet();
            //float mutation_rate = 0.01f;
            //float cull_rate = 0.5f;
            //float crossover_rate = 1.0f / (CAState.Width * CAState.Height);
            //int max_iterations = 10000;
            //int seed = 1102260512; //(int)(DateTime.Now.Ticks);



            CAState archetype = new CAState(12, 12, CAState.WrapMode.Extend);
            int population_size = 100;
            int period = 2;
            CARuleSet rule_set = new CARuleSet();
            float outer_mutation_rate = 0.5f;
            float inner_mutation_rate = 0.7f;
            float cull_rate = 0.25f;
            int max_iterations = 10000;
            int seed = (int)(DateTime.Now.Ticks);




            FixedGA ga = new FixedGA(archetype, population_size, period, rule_set, outer_mutation_rate, inner_mutation_rate, cull_rate, max_iterations);

            StringBuilder sb = new StringBuilder();
            sb.AppendLine("CA dimensions: " + archetype.Width + "x" + archetype.Height);
            sb.AppendLine("CA wrap_mode: " + archetype.wrap_mode);
            sb.AppendLine("population size: " + population_size);
            sb.AppendLine("period: " + period);
            sb.AppendLine("outer mutation rate: " + outer_mutation_rate);
            sb.AppendLine("inner mutation rate: " + inner_mutation_rate);
            sb.AppendLine("cull rate: " + cull_rate);
            sb.AppendLine("max iterations: " + max_iterations);
            sb.AppendLine("seed: " + seed);
            ga.run(seed, sb);
            
            //Application.EnableVisualStyles();
            //Application.SetCompatibleTextRenderingDefault(false);
            //Application.Run(new MainWindow());
			
            
            
            //CARuleSet crs = new CARuleSet();
            //String name = CAState.getRandomOscillatorName();
            //Console.WriteLine(name);
            //CAState c = CAState.loadOscillatorFromName(name);
            //c.addPadding(2, 2);
            //int period = crs.getPeriod(c);
            //float heat = crs.getHeat(c, period);
            //Console.WriteLine("period " + period);
            //Console.WriteLine("heat " + heat);

            //CAState temp0 = new CAState(c);
            //CAState temp1 = new CAState(c);
            //for (int i=0; i<period; ++i)
            //{
            //    Console.WriteLine(temp0.ToString());
            //    crs.getTransition(temp0, temp1);
            //    temp0.copyFrom(temp1);
            //}



        }
    }
}
