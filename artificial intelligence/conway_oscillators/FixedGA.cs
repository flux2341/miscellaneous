using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.IO;

namespace Wyrd
{
    public class FixedGA
    {
        private CAState archetype;
        private CAState[] population;
        private int period;
        private CARuleSet rule_set;
        private float outer_mutation_rate;
        private float inner_mutation_rate;
        private float cull_rate;
        private int max_iterations;

        private int n_cull;

        private float[] fitness_weights;

        public FixedGA(CAState archetype, int population_size, int period, CARuleSet rule_set, float outer_mutation_rate,
            float inner_mutation_rate, float cull_rate, int max_iterations)
        {
            this.archetype = archetype;
            this.period = period;
            this.rule_set = rule_set;
            this.outer_mutation_rate = outer_mutation_rate;
            this.inner_mutation_rate = inner_mutation_rate;
            this.cull_rate = cull_rate;
            this.max_iterations = max_iterations;
            population = new CAState[population_size];

            n_cull = (int)(cull_rate * population.Length);


            fitness_weights = new float[period];
            float fwt = 0.0f;
            for (int i=0; i<fitness_weights.Length; ++i)
            {
                float t = (float)(i+1)/(fitness_weights.Length);
                fitness_weights[i] = 0.75f - (float)(Math.Sin(10*t/Math.PI));
                fwt += Math.Abs(fitness_weights[i]);
            }
            for (int i=0; i<fitness_weights.Length; ++i)
            {
                fitness_weights[i] /= fwt;
            }

        }



        public void run(int seed, StringBuilder sb)
        {
            // initialize the population
            Random rng = new Random(seed);
            for (int i=0; i<population.Length; ++i)
            {
                population[i] = new CAState(archetype);
                population[i].randomize(rng);
                evaluateFitness(population[i]);
            }





            bool stop = false;
            for (int i=0; i<max_iterations; ++i)
            {
                float mean, variance, min, max;
                Utility.getStatistics(population, out mean, out variance, out min, out max);
                String float_pattern = "{0:0.0000}";
                String mv_txt = i + "," + 
                                String.Format(float_pattern, mean) + "," +
                                String.Format(float_pattern, variance) + "," +
                                String.Format(float_pattern, min) + "," +
                                String.Format(float_pattern, max);
                sb.AppendLine(mv_txt);
                Console.WriteLine(mv_txt);




                int n_om = (int)(outer_mutation_rate * population.Length);
                int n_im = (int)(inner_mutation_rate * archetype.Width * archetype.Height);
                for (int j=0; j<n_om; ++j)
                {
                    //population[j].randomize(rng);
                    CAState state = population[rng.Next() % population.Length];
                    //CAState state = population[j];
                    for (int k=0; k<n_im; ++k)
                    {
                        state[rng.Next()%archetype.Width, rng.Next()%archetype.Height] = (rng.Next()%2 == 1);
                    }
                    evaluateFitness(state);
                }




                Array.Sort(population);

                for (int j=0; j<n_cull; ++j)
                {
                    
                    population[j] = breed(population[population.Length-j-1],
                                          population[population.Length-j-2],
                                          rng);
                    evaluateFitness(population[j]);
                    

                    if (population[j].fitness == 1)
                    {
                        //Console.WriteLine(oscillatorness + "," + density);
                        //print(population[j]);
                        String path = Utility.createNewFolder("./");
                        Utility.saveImages(population[j], rule_set, path);
                        Utility.writeFile(sb, path, "data");
                        stop = true;
                        break;
                    }
                }

                if (stop)
                {
                    break;
                }

            }
        }







        //private void evaluateFitness(CAState state)
        //{
        //    CAState temp0 = new CAState();
        //    CAState temp1 = new CAState();
        //    temp0.copyFrom(state);
        //    for (int k=0; k<period; ++k)
        //    {
        //        rule_set.getTransition(temp0, temp1);
        //        temp0.copyFrom(temp1);
        //    }
        //    float oscillatorness = 1.0f - temp0.getDifference(state);


        //    //float density = (float)(population[j].numberOfLiveCells()) / (CAState.Width * CAState.Height);
        //    int nas = state.numberOfLiveCells();
        //    int min_nas = 20;
        //    float density = (nas > min_nas) ? 1.0f : nas / min_nas;

        //    float t = 0.75f;
        //    state.fitness = t * oscillatorness + (1.0f - t) * density;
        //}




        // no state should look more like the first than the end state
        private void evaluateFitness(CAState state)
        {
            float oscillatorness = 0.0f;
            CAState temp0 = new CAState(state);
            CAState temp1 = new CAState(state);
            float[] f = new float[period];
            for (int k=0; k<period; ++k)
            {
                rule_set.getTransition(temp0, temp1);
                temp0.copyFrom(temp1);

                f[k] = fitness_weights[k]*(1.0f - temp0.getRelativeDifference(state));
                oscillatorness += f[k];
            }

            float final_difference = temp0.getRelativeDifference(state);
            if (final_difference == 0)
            {
                state.fitness = 1.0f;
                return;
            }

            //oscillatorness = 1.0f - final_difference;

            //float oscillatorness = Math.Max(0, 1.0f - final_difference - 20*(final_difference - min_difference));
            //if (final_difference == min_difference)
            //{
            //    oscillatorness = 1.0f - final_difference + (final_difference - min_difference);
            //}
            //else
            //{
            //    oscillatorness = Math.Max(1.0f - 10.0f*min_difference, 0);
            //}



            //float density = (float)(population[j].numberOfLiveCells()) / (CAState.Width * CAState.Height);
            int nas = state.numberOfLiveCells();
            int min_nas = 20;
            float density = (nas > min_nas) ? 1.0f : nas / min_nas;

            float t = 0.8f;
            state.fitness = t * oscillatorness + (1.0f - t) * density;
        }



        // simple breeding model - randomly choose each cell from either A or B
        private CAState breed(CAState a, CAState b, Random rng)
        {
            CAState c = new CAState(a);
            for (int i=0; i<c.Width; ++i)
            {
                for (int j=0; j<c.Height; ++j)
                {
                    if (rng.Next()%2 == 0)
                    {
                        c.setValue(i, j, a.getValue(i, j));
                    }
                    else
                    {
                        c.setValue(i, j, b.getValue(i, j));
                    }
                }
            }
            return c;
        }

    }
}
