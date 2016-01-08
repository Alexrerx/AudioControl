package com.rerx.alexey.audiocontrol;

/**
 * Created by alexey on 08.01.16.
 */
public class Window { private final static double Q=0.5;//стырено
    //Здесь разные окна,какое применять-решим потом

    public static double Rectangle(double n, double frameSize)
    {
        return 1;
    }

    public static double Gausse(double n, double frameSize)
    {
        double a = (frameSize - 1)/2;
        double t = (n - a)/(Q*a);
        t = t*t;
        return Math.exp(-t/2);
    }
    public static double Hamming(double n, double frameSize)
    {
        return 0.54 - 0.46*Math.cos((2 * Math.PI * n) / (frameSize - 1));
    }

    public static double Hann(double n, double frameSize)
    {
        return 0.5*(1 - Math.cos((2 * Math.PI * n) / (frameSize - 1)));
    }

    public static double BlackmannHarris(double n, double frameSize)
    {
        return 0.35875 - (0.48829*Math.cos((2 * Math.PI * n) / (frameSize - 1))) +
                (0.14128*Math.cos((4 * Math.PI * n) / (frameSize - 1))) - (0.01168*Math.cos((4 * Math.PI * n) / (frameSize - 1)));
    }

//    public static Complex WaveWavelet(Complex n, Complex frameSize)
//    {
//        double t = n/frameSize;
//        return -t*Math.exp((-t*t/2.0).real);
//    }

    public static double SombreroWavelet(double n, double frameSize)
    {
        double t = n / frameSize;
        return (t*t - 1)*Math.exp(-t*t/2);
    }

    public static double DOGWavelet(double n, double frameSize)
    {
        double t = n / frameSize;
        return Math.exp(-t*t/2) - Math.exp(-t*t/8)/2;
    }
}
