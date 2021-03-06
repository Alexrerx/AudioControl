package com.rerx.alexey.audiocontrol;

/**
 * Created by alexey on 08.01.16.
 */
public class FFTKuli_Turky {
    //ComplexKuli complexKuli;

    /// <summary>
    /// Calculates FFT using Cooley-Tukey FFT algorithm.
    /// </summary>
    /// <param name="x">input data</param>
    /// <returns>spectrogram of the data</returns>
    /// <remarks>
    /// If amount of data items not equal a power of 2, then algorithm
    /// automatically pad with 0s to the lowest amount of power of 2.
    /// </remarks>
    public double[] Calculate(short[] x)//было double
    {
        int length;
        int bitsInLength;
        if (IsPowerOfTwo(x.length))
        {
            length = x.length;
            bitsInLength = Log2(length) - 1;
        }
        else
        {
            bitsInLength = Log2(x.length);
            length = 1 << bitsInLength;
            // the items will be pad with zeros
        }

        // bit reversal
        ComplexKuli[] data = new ComplexKuli[length];
        for (int i = 0; i < x.length; i++)
        {
            int j = ReverseBits(i, bitsInLength);
            data[j] = new ComplexKuli(x[i]);
        }

        // Cooley-Tukey
        for (int i = 0; i < bitsInLength; i++)
        {
            int m = 1 << i;
            int n = m * 2;
            double alpha = -(2 * Math.PI / n);

            for (int k = 0; k < m; k++)
            {
                // e^(-2*pi/N*k)
                ComplexKuli oddPartMultiplier = new ComplexKuli(0, alpha * k).PoweredE();

                for (int j = k; j < length; j += n)
                {
                    ComplexKuli evenPart = data[j];
                    ComplexKuli oddPart = oddPartMultiplier.times(data[j + m]);
                    data[j] = evenPart.plus(oddPart);
                    data[j + m] = evenPart.minus(oddPart);
                }
            }
        }

        // calculate spectrogram
        double[] spectrogram = new double[length];
        for (int i = 0; i < spectrogram.length; i++)
        {
            spectrogram[i] = data[i].AbsPower2();
        }
        return spectrogram;
    }

    /// <summary>
    /// Gets number of significat bytes.
    /// </summary>
    /// <param name="n">Number</param>
    /// <returns>Amount of minimal bits to store the number.</returns>
    private int Log2(int n)
    {
        int i = 0;
        while (n > 0)
        {
            ++i; n >>= 1;
        }
        return i;
    }

    /// <summary>
    /// Reverses bits in the number.
    /// </summary>
    /// <param name="n">Number</param>
    /// <param name="bitsCount">Significant bits in the number.</param>
    /// <returns>Reversed binary number.</returns>
    private int ReverseBits(int n, int bitsCount)
    {
        int reversed = 0;
        for (int i = 0; i < bitsCount; i++)
        {
            int nextBit = n & 1;
            n >>= 1;
            reversed <<= 1;
            reversed |= nextBit;
        }
        return reversed;
    }

    /// <summary>
    /// Checks if number is power of 2.
    /// </summary>
    /// <param name="n">number</param>
    /// <returns>true if n=2^k and k is positive integer</returns>
    private boolean IsPowerOfTwo(int n)
    {
        return n > 1 && (n & (n - 1)) == 0;
    }
}
