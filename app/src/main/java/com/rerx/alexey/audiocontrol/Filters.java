package com.rerx.alexey.audiocontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by alexey on 13.01.16.
 */
public class Filters {


        //  Δ∂ωπ

            public final double SinglePi = Math.PI;
            public static final double DoublePi = 2*Math.PI;

            public static Dictionary<Double, Double> GetJoinedSpectrum(
                    Complex[] spectrum0, Complex[] spectrum1,
                    double shiftsPerFrame, double sampleRate)
            {
                int frameSize = spectrum0.length;//?????
                double frameTime = frameSize/sampleRate;
                double shiftTime = frameTime/shiftsPerFrame;
                double binToFrequancy = sampleRate/frameSize;
                Dictionary <Double,Double> dictionary = new Hashtable<>();//new Dictionary

                for (int bin = 0; bin < frameSize; bin++)
                {
                    double omegaExpected = DoublePi*(bin*binToFrequancy); // ω=2πf
                    double omegaActual = (spectrum1[bin].phase() - spectrum0[bin].phase())/shiftTime; // ω=∂φ/∂t
                    double omegaDelta = Align(omegaActual - omegaExpected, DoublePi); // Δω=(∂ω + π)%2π - π
                    double binDelta = omegaDelta/(DoublePi*binToFrequancy);
                    double frequancyActual = (bin + binDelta)*binToFrequancy;
                    double magnitude = spectrum1[bin].abs() + spectrum0[bin].abs();
                    dictionary.put(frequancyActual, magnitude * (0.5 + Math.abs(binDelta)));
                }

                return dictionary;
            }

            public static double Align(double angle, double period)
            {
                int qpd = (int) (angle/period);
                if (qpd >= 0) qpd += qpd & 1;
                else qpd -= qpd & 1;
                angle -= period*qpd;
                return angle;
            }
//    public static Dictionary<Double, Double> Antialiasing(Dictionary<Double, Double> spectrum)
//    {
//      Dictionary result = new Hashtable<>();
//        List data = spectrum.to
//        for (int j = 0; j < spectrum.size() - 4; j++)
//        {
//            int i = j;
//            double x0 = data[i].Key;
//            var x1 = data[i + 1].Key;
//            var y0 = data[i].Value;
//            var y1 = data[i + 1].Value;
//
//            var a = (y1 - y0)/(x1 - x0);
//            var b = y0 - a*x0;
//
//            i += 2;
//            var u0 = data[i].Key;
//            var u1 = data[i + 1].Key;
//            var v0 = data[i].Value;
//            var v1 = data[i + 1].Value;
//
//            var c = (v1 - v0)/(u1 - u0);
//            var d = v0 - c*u0;
//
//            var x = (d - b)/(a - c);
//            var y = (a*d - b*c)/(a - c);
//
//            if (y > y0 && y > y1 && y > v0 && y > v1 &&
//                    x > x0 && x > x1 && x < u0 && x < u1)
//            {
//                result.Add(x1, y1);
//                result.Add(x, y);
//            }
//            else
//            {
//                result.Add(x1, y1);
//            }
//        }
//
//        return result;
//    }
//}
//}
        }
//    }
//}
