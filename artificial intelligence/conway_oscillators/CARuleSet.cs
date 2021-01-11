using System;
namespace Wyrd
{
    public class CARuleSet
    {
        public CARuleSet() {}

        public bool getTransition(int i, int j, CAState cas)
        {
            int neighbors = (cas.getValue(i-1,j-1)? 1: 0) +
                            (cas.getValue(i-1,j  )? 1: 0) +
                            (cas.getValue(i-1,j+1)? 1: 0) +
                            (cas.getValue(i  ,j-1)? 1: 0) +
                            (cas.getValue(i  ,j+1)? 1: 0) +
                            (cas.getValue(i+1,j-1)? 1: 0) +
                            (cas.getValue(i+1,j  )? 1: 0) +
                            (cas.getValue(i+1,j+1)? 1: 0);
            if (cas.getValue(i, j))
            {
                if (neighbors < 2 || neighbors > 3)
                {
                    return false;
                }
                return true;
            }
            else if (neighbors == 3)
            {
                return true;
            }
            return false;
        }
        public void getTransition(CAState a, CAState b)
        {
            for (int i=0; i<a.Width; ++i)
            {
                for (int j=0; j<a.Height; ++j)
                {
                    b[i, j] = getTransition(i, j, a);
                }
            }
        }
        public CAState getTransition(CAState a)
        {
            CAState r = new CAState(a);
            getTransition(a, r);
            return r;
        }

        public void getTransition(CAState a, CAState b, int n)
        {
            CAState temp = new CAState(a);
            for (int i=0; i<n; ++i)
            {
                getTransition(temp, b);
                temp.copyFrom(b);
            }
        }

        public int getPeriod(CAState a)
        {
            CAState b = new CAState(a);
            CAState c = new CAState(a);
            for (int i=0; i<1000; ++i)
            {
                getTransition(b, c);
                b.copyFrom(c);
                if (b.getRelativeDifference(a) == 0)
                {
                    return i+1;
                }
            }
            return 0;
        }
        public float getHeat(CAState a, int p)
        {
            CAState b = new CAState(a);
            CAState c = new CAState(a);
            float r = 0.0f;
            for (int i=0; i<p; ++i)
            {
                getTransition(b, c);
                r += b.getDifference(c);
                b.copyFrom(c);
            }
            return r / p;
        }

    }
}
