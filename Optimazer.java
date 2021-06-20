import java.math.*;

public class Optimazer {
    private double[] a;

    private double[] b;

    public double[] getA() {
        return a;
    }

    public double[] getB() {
        return b;
    }

    public Optimazer(Glass[] gl, int n, double lambda, double DA, double DB) {
        int k = 2;
        a = new double[k];
        b = new double[k];
        double f = 0;
        for (int j = 0; j < 100; ++j) {
            for (int i = k; i < n - 1; ++i) {
                f = 0;
                for (int l = 0; l < k; ++l){
                    f += a[l] * (gl[i - l].getAskPrice()[0] - gl[i - l - 1].getAskPrice()[0]) / Math.sqrt(DA);
                    f += b[l] * (gl[i - l].getBidPrice()[0] - gl[i - l - 1].getBidPrice()[0]) / Math.sqrt(DB);
                }
                for (int l = 0; l < k; ++l){
                    a[l] -= 2 * lambda * (Math.tanh(f) * (gl[i + 1].getAskPrice()[0] - gl[i].getAskPrice()[0]) -
                            Math.abs(gl[i + 1].getAskPrice()[0] - gl[i].getAskPrice()[0])) *
                            (gl[i + 1].getAskPrice()[0] - gl[i].getAskPrice()[0]) / Math.cosh(f) / Math.cosh(f) *
                            (gl[i - l].getAskPrice()[0] - gl[i - l - 1].getAskPrice()[0]) / Math.sqrt(DA);
                    b[l] -= 2 * lambda * (Math.tanh(f) * (gl[i + 1].getBidPrice()[0] - gl[i].getBidPrice()[0]) -
                            Math.abs(gl[i + 1].getBidPrice()[0] - gl[i].getBidPrice()[0])) *
                            (gl[i + 1].getBidPrice()[0] - gl[i].getBidPrice()[0]) / Math.cosh(f) / Math.cosh(f) *
                            (gl[i - l].getBidPrice()[0] - gl[i - l - 1].getBidPrice()[0]) / Math.sqrt(DB);
                }
            }
        }
    }
}