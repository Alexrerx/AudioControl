package com.rerx.alexey.audiocontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexey on 13.01.16.
 */
public class Filters {


    //  Δ∂ωπ

    public final double SinglePi = Math.PI;
    public static final double DoublePi = 2 * Math.PI;

    public static Map<Double, Double> GetJoinedSpectrum(
            ArrayList<Complex> spectrum0, ArrayList<Complex> spectrum1,
            double shiftsPerFrame, double sampleRate) {

        int frameSize = spectrum0.size();//?????
        double frameTime = frameSize / sampleRate;
        double shiftTime = frameTime / shiftsPerFrame;
        double binToFrequancy = sampleRate / frameSize;
        Map<Double, Double> dictionary = new Hashtable<>();//new Dictionary

        for (int bin = 0; bin < frameSize; bin++) {
            double omegaExpected = DoublePi * (bin * binToFrequancy); // ω=2πf
            double omegaActual = (spectrum1.get(bin).phase() - spectrum0.get(bin).phase()) / shiftTime; // ω=∂φ/∂t
            double omegaDelta = Align(omegaActual - omegaExpected, DoublePi); // Δω=(∂ω + π)%2π - π
            double binDelta = omegaDelta / (DoublePi * binToFrequancy);
            double frequancyActual = (bin + binDelta) * binToFrequancy;
            double magnitude = spectrum1.get(bin).abs() + spectrum0.get(bin).abs();
            dictionary.put(frequancyActual, magnitude * (0.5 + Math.abs(binDelta)));
        }

        return dictionary;
    }


    public static Map<Double, Double> GetJoinedSpectrum(
            Complex[] spectrum0, Complex[] spectrum1,
            double shiftsPerFrame, double sampleRate) {

        int frameSize = spectrum0.length;//?????
        double frameTime = frameSize / sampleRate;
        double shiftTime = frameTime / shiftsPerFrame;
        double binToFrequancy = sampleRate / frameSize;
        Map<Double, Double> dictionary = new Hashtable<>();//new Dictionary

        for (int bin = 0; bin < frameSize; bin++) {
            double omegaExpected = DoublePi * (bin * binToFrequancy); // ω=2πf
            double omegaActual = (spectrum1[bin].phase() - spectrum0[bin].phase()) / shiftTime; // ω=∂φ/∂t
            double omegaDelta = Align(omegaActual - omegaExpected, DoublePi); // Δω=(∂ω + π)%2π - π
            double binDelta = omegaDelta / (DoublePi * binToFrequancy);
            double frequancyActual = (bin + binDelta) * binToFrequancy;
            double magnitude = spectrum1[bin].abs() + spectrum0[bin].abs();
            dictionary.put(frequancyActual, magnitude * (0.5 + Math.abs(binDelta)));
        }

        return dictionary;
    }

    public static double Align(double angle, double period) {
        int qpd = (int) (angle / period);
        if (qpd >= 0) qpd += qpd & 1;
        else qpd -= qpd & 1;
        angle -= period * qpd;
        return angle;
    }

//    public static Map<Double, Double> Antialiasing(Map<Double, Double> spectrum) {
//        Map result = new LinkedHashMap();
//        List data = new ArrayList(spectrum.entrySet());
//        for (int j = 0; j < spectrum.size() - 4; j++) {
////            int i = j;
//////            double x0 = data.get(i).Key;
//////            double x1 = data.get(i+1).Key;
//////            double y0 = data.get(i).Value;
//////            double y1 = data.get(i+1).Value;
//////
//////            double a = (y1 - y0)/(x1 - x0);
//////            double b = y0 - a*x0;
//////
//////            i += 2;
//////            double u0 = data.get(i).Key;
//////            double u1 = data.get(i+1).Key;
//////            double v0 = data.get(i).Value;
//////            double v1 = data.get(i+1).Value;
////
////            double c = (v1 - v0)/(u1 - u0);
////            double d = v0 - c*u0;
////
////            double x = (d - b)/(a - c);
////            double y = (a*d - b*c)/(a - c);
////
////            if (y > y0 && y > y1 && y > v0 && y > v1 &&
////                    x > x0 && x > x1 && x < u0 && x < u1)
////            {
////                result.put(x1, y1);
////                result.put(x, y);
////            }
////            else
////            {
////                result.put(x1, y1);
////            }
////        }
////
////        return result;
////    }
//
//        }
//    }
//}
}