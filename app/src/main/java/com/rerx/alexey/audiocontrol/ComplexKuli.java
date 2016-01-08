package com.rerx.alexey.audiocontrol;

/**
 * Created by alexey on 08.01.16.
 */
public class ComplexKuli {

    public double Re;
    public double Im;

    public ComplexKuli(double re)
    {
        this.Re = re;
        this.Im = 0;
    }

    public ComplexKuli(double re, double im)
    {
        this.Re = re;
        this.Im = im;
    }

    public ComplexKuli times(ComplexKuli b)
    {
        ComplexKuli a = this;
        double real = a.Re * b.Re - a.Im * b.Im;
        double imag = a.Re * b.Im + a.Im * b.Re;
        return new ComplexKuli(real, imag);
    }

    public ComplexKuli plus( ComplexKuli b)
    {
        ComplexKuli a = this;             // invoking object
        double real = a.Re + b.Re;
        double imag = a.Im + b.Im;
        return new ComplexKuli(real, imag);
    }

    public ComplexKuli minus(ComplexKuli b)
    {
        ComplexKuli a = this;
        double real = a.Re - b.Re;
        double imag = a.Im - b.Im;
        return new ComplexKuli(real, imag);
    }

    public static ComplexKuli antiAbs(ComplexKuli n)
    {
        return new ComplexKuli(-n.Re, -n.Im);
    }

//    public static implicit operator ComplexNumber(double n)
//    {
//        return new ComplexNumber(n, 0);
//    }

    public ComplexKuli PoweredE()
    {
        double e = Math.exp(Re);
        return new ComplexKuli(e * Math.cos(Im), e * Math.sin(Im));
    }

    public double Power2()
    {
        return Re * Re - Im * Im;
    }

    public double AbsPower2()
    {
        return Re * Re + Im * Im;
    }


    public String ToString()
    {
        return String.format("{0}+i*{1}", Re, Im);
    }
}
