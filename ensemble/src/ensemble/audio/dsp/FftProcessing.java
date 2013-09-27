package ensemble.audio.dsp;

// TODO: Auto-generated Javadoc
//WTest.java
/*
    Copyright (C) 2003 Loreno Heer, (helohe at bluewin dot ch)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/
/**
 * The Class FftProcessing.
 */
public class FftProcessing {

    /**
     * Sin.
     *
     * @param step the step
     * @param size the size
     * @return the double[]
     */
    private static double[] sin(double step, int size){
        double f = 0;
        double[] ret = new double[size];
        for(int i = 0; i < size; i++){
            ret[i] = Math.sin(f);
            f += step;
        }
        return ret;
    }

    /**
     * Adds the.
     *
     * @param a the a
     * @param b the b
     * @return the double[]
     */
    private static double[] add(double[] a, double[] b){
        double[] c = new double[a.length];
        for(int i = 0; i < a.length; i++){
            c[i] = a[i] + b[i];
        }
        return c;
    }

    /**
     * Sub.
     *
     * @param a the a
     * @param b the b
     * @return the double[]
     */
    private static double[] sub(double[] a, double[] b){
        double[] c = new double[a.length];
        for(int i = 0; i < a.length; i++){
            c[i] = a[i] - b[i];
        }
        return c;
    }

    /**
     * Adds the.
     *
     * @param a the a
     * @param b the b
     * @return the double[]
     */
    private static double[] add(double[] a, double b){
        double[] c = new double[a.length];
        for(int i = 0; i < a.length; i++){
            c[i] = a[i] + b;
        }
        return c;
    }

    /**
     * Cp.
     *
     * @param a the a
     * @param size the size
     * @return the double[]
     */
    private static double[] cp(double[] a, int size){
        double[] c = new double[size];
        for(int i = 0; i < size; i++){
            c[i] = a[i];
        }
        return c;
    }

    /**
     * Mul.
     *
     * @param a the a
     * @param b the b
     * @return the double[]
     */
    private static double[] mul(double[] a, double b){
        double[] c = new double[a.length];
        for(int i = 0; i < a.length; i++){
            c[i] = a[i] * b;
        }
        return c;
    }

    /**
     * Prints the.
     *
     * @param value the value
     */
    public static void print(double[] value){
        for(int i = 0; i < value.length; i++){
            System.out.print(i + "," + value[i] + "\n");
        }
        System.out.println();
    }

    /**
     * Abs.
     *
     * @param a the a
     * @return the double
     */
    private static double abs(double[] a){
        double c = 0;
        for(int i = 0; i < a.length; i++){
            c = ((c * i) + Math.abs(a[i])) / (i + 1);
        }
        return c;
    }

    /**
     * Fft.
     *
     * @param a the a
     * @param min the min
     * @param max the max
     * @param step the step
     * @return the double[]
     */
    public static double[] fft(double[] a, int min, int max, int step){
        double[] ret = new double[(max - min) / step];
        int i = 0;
        for(int d = min; d < max; d = d + step){
            double[] f = sin(fc(d), a.length);
            double[] dif = sub(a, f);
            ret[i] = 1 - abs(dif);
            i++;
        }
        return ret;
    }

    /**
     * Fft_log.
     *
     * @param a the a
     * @return the double[]
     */
    private static double[] fft_log(double[] a){
        double[] ret = new double[1551];
        int i = 0;
        for(double d = 0; d < 15.5; d = d + 0.01){
            double[] f = sin(fc(Math.pow(2,d)), a.length);
            double[] dif = sub(a, f);
            ret[i] = Math.abs(1 - abs(dif));
            i++;
        }
        return ret;
    }

    /**
     * Fc.
     *
     * @param d the d
     * @return the double
     */
    private static double fc(double d){
        return d * Math.PI / res;
    }

    /**
     * Print_log.
     *
     * @param value the value
     */
    private static void print_log(double[] value){
        for(int i = 0; i < value.length; i++){
            System.out.print(Math.pow(2,((double)i/100d)) + "," + value[i] + "\n");
        }
        System.out.println();
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args){
        double[] f_0 = sin(fc(440), sample_length); // res / pi =>14005
        //double[] f_1 = sin(.02, sample_length);
        double[] f_2 = sin(fc(520), sample_length);
        //double[] f_3 = sin(.25, sample_length);

        //double[] f = add( add( add(f_0, f_1), f_2), f_3);

        double[] f = add(f_0, f_2);

        //print(f);

        double[] d = cp(f,1000);
        print_log(fft_log(d));
    }

    /** The length. */
    static double length = .2; // sec
    
    /** The res. */
    static int res = 44000; // resoultion (pro sec)
    
    /** The sample_length. */
    static int sample_length = res; // resoultion

}
