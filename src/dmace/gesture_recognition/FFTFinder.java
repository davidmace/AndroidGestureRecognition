package dmace.gesture_recognition;

import java.util.ArrayList;

public class FFTFinder {
	public static double[] getFrequencyData(double[] in) {
		Complex[] step2=toComplexArray(in);
		Complex[] step3=fft(step2);
		double[] out=toDoubleArray(step3);
		return out;
	}
	
	private static Complex[] toComplexArray(double[] in) {
		Complex[] out=new Complex[128];
		for(int i=0; i<in.length; i++) {
			out[i]=new Complex(in[i],0);
		}
		return out;
	}
		
	private static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new RuntimeException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
	
	private static double[] toDoubleArray(Complex[] in) {
		double[] out=new double[12];
		for(int i=0; i<out.length/2; i++) {
			out[i*2]=in[i+1].re();//+1 to ignore 0 frequency coefficient
			out[i*2+1]=in[i+1].im();
		}
		return out;
	}
}
