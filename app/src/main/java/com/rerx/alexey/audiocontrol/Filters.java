package com.rerx.alexey.audiocontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;

/**
 * Created by alexey on 13.01.16.
 */
//public class Filters {
//
//
//        //  Δ∂ωπ
//
//            public final double SinglePi = Math.PI;
//            public static final double DoublePi = 2*Math.PI;
//
//            public static Dictionary<Double, Double> GetJoinedSpectrum(
//                    ArrayList<Complex> spectrum0, ArrayList<Complex> spectrum1,
//                    double shiftsPerFrame, double sampleRate)
//            {
//                int frameSize = spectrum0.count;
//                double frameTime = frameSize/sampleRate;
//                double shiftTime = frameTime/shiftsPerFrame;
//                double binToFrequancy = sampleRate/frameSize;
//                Dictionary dictionary = new Dictionary<Double, Double>();
//
//                for (int bin = 0; bin < frameSize; bin++)
//                {
//                    double omegaExpected = DoublePi*(bin*binToFrequancy); // ω=2πf
//                    double omegaActual = (spectrum1[bin].Phase - spectrum0[bin].Phase)/shiftTime; // ω=∂φ/∂t
//                    double omegaDelta = Align(omegaActual - omegaExpected, DoublePi); // Δω=(∂ω + π)%2π - π
//                    double binDelta = omegaDelta/(DoublePi*binToFrequancy);
//                    double frequancyActual = (bin + binDelta)*binToFrequancy;
//                    double magnitude = spectrum1[bin].Magnitude + spectrum0[bin].Magnitude;
//                    dictionary.Add(frequancyActual, magnitude * (0.5 + Math.Abs(binDelta)));
//                }
//
//                return dictionary;
//            }
//
//            public static double Align(double angle, double period)
//            {
//                int qpd = (int) (angle/period);
//                if (qpd >= 0) qpd += qpd & 1;
//                else qpd -= qpd & 1;
//                angle -= period*qpd;
//                return angle;
//            }
//        }
//    }
//}
