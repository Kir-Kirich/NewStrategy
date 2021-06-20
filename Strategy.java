import java.io.*;
import java.math.*;

public class Strategy {
    static public double price;
    public double profit;
    public double request;

    public Strategy(){
        request = 0;
        profit = 0;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader brTrain = null;
        BufferedReader brTest = null;
        BufferedReader brDatafortest = null;
        PrintWriter pwTrain = null;
        PrintWriter pwTest = null;
        String line;
        String[] numbers;
        Glass[] glTrain = new Glass[1000000];
        Glass[] glTest = new Glass[1000000];
        int count = 0;
        double f, askPriceD = 0, bidPriceD = 0;

        /*try {
            File datafortest = new File("datafortest(2).txt");
            File train = new File("train.txt");
            File test = new File("test.txt");
            pwTrain = new PrintWriter(train);
            pwTest = new PrintWriter(test);
            brDatafortest = new BufferedReader(new FileReader(datafortest));
            while (((line = brDatafortest.readLine()) != null) & (count < 100000)) {
                if (line.toCharArray()[0] != '0')
                    continue;
                pwTrain.println(line);
                ++count;
            }
            while (((line = brDatafortest.readLine()) != null) & (count < 1000000)) {
                if (line.toCharArray()[0] != '0')
                    continue;
                pwTest.println(line);
                ++count;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
            pwTrain.close();
            pwTest.close();
            brDatafortest.close();
        }*/


        try {
            File train = new File("train.txt");
            brTrain = new BufferedReader(new FileReader(train));
            count = 0;
            while (((line = brTrain.readLine()) != null) & (count < 1000000)) {
                if (line.toCharArray()[0] != '0')
                    continue;
                numbers = line.split(",");
                glTrain[count] = new Glass(line);
                ++count;
            }
        } catch (IOException e) {
            System.out.println("Error: " + e);
        } finally {
                brTrain.close();
        }
        for (int i = 1; i < count; ++i) {
            askPriceD += (glTrain[i].getAskPrice()[0] - glTrain[i - 1].getAskPrice()[0]) *
                    (glTrain[i].getAskPrice()[0] - glTrain[i - 1].getAskPrice()[0]);
            bidPriceD += (glTrain[i].getBidPrice()[0] - glTrain[i - 1].getBidPrice()[0]) *
                    (glTrain[i].getBidPrice()[0] - glTrain[i - 1].getBidPrice()[0]);
        }
        askPriceD /= count - 1;
        bidPriceD /= count - 1;

        double covAA = 0, covBA = 0, covAB = 0, covBB = 0;
        int k = 1;
        for (int i = 1; i < count - k; ++i){

            covAA += (glTrain[i].getAskPrice()[0] - glTrain[i - 1].getAskPrice()[0]) *
                    (glTrain[i + k].getAskPrice()[0] - glTrain[i - 1 + k].getAskPrice()[0]);
            covBA += (glTrain[i].getBidPrice()[0] - glTrain[i - 1].getBidPrice()[0]) *
                    (glTrain[i + k].getAskPrice()[0] - glTrain[i - 1 + k].getAskPrice()[0]);

            covAB += (glTrain[i].getAskPrice()[0] - glTrain[i - 1].getAskPrice()[0]) *
                    (glTrain[i + k].getBidPrice()[0] - glTrain[i - 1 + k].getBidPrice()[0]);
            covBB += (glTrain[i].getBidPrice()[0] - glTrain[i - 1].getBidPrice()[0]) *
                    (glTrain[i + k].getBidPrice()[0] - glTrain[i - 1 + k].getBidPrice()[0]);
        }
        covAA /= askPriceD * (count - k - 1);
        covBA /= Math.sqrt(askPriceD * bidPriceD) * (count - k - 1);
        covAB /= Math.sqrt(askPriceD * bidPriceD) * (count - k - 1);
        covBB /= bidPriceD * (count - k - 1);
        /*System.out.println(covAA + "  " + covBA);
        System.out.println(covAB + "  " + covBB);*/
        double eps = 1.0E-10, par = 0.4;
        double a1, a2, b1, b2;
        Optimazer opt = new Optimazer(glTrain, count, 0.01, askPriceD, bidPriceD);
        a1 = opt.getA()[0];
        a2 = opt.getA()[1];
        b1 = opt.getB()[0];
        b2 = opt.getB()[1];
        /*System.out.println("a1 = " + a1);
        System.out.println("a2 = " + a2);
        System.out.println("b1 = " + b1);
        System.out.println("b2 = " + b2);*/
        try {
            File test = new File("test.txt");
            brTest = new BufferedReader(new FileReader(test));
            count = 0;
            Strategy[] str = new Strategy[1000000];
            while (((line = brTest.readLine()) != null) & (count < 2)){
                if (line.toCharArray()[0] != '0')
                    continue;
                glTest[count] = new Glass(line);
                str[count] = new Strategy();
                ++count;
            }
            while (((line = brTest.readLine()) != null) & (count < 1000000)){
                if (line.toCharArray()[0] != '0')
                    continue;
                glTest[count] = new Glass(line);
                str[count] = new Strategy();
                f = Math.tanh(a1 * (glTest[count].getAskPrice()[0] - glTest[count - 1].getAskPrice()[0]) / Math.sqrt(askPriceD)
                        + b1 * (glTest[count].getBidPrice()[0] - glTest[count - 1].getBidPrice()[0]) / Math.sqrt(bidPriceD)
                + a2 * (glTest[count - 1].getAskPrice()[0] - glTest[count - 2].getAskPrice()[0]) / Math.sqrt(askPriceD)
                + b2 * (glTest[count - 1].getBidPrice()[0] - glTest[count - 2].getBidPrice()[0]) / Math.sqrt(bidPriceD));

                if ((str[count - 1].request >= glTest[count].getAskPrice()[0]) & (str[count - 1].request > eps)){
                    if (Math.abs(Strategy.price) < eps){
                        Strategy.price = glTest[count].getAskPrice()[0];
                    }
                    if (Math.abs(Strategy.price) < -eps){
                        str[count - 1].profit = (- Strategy.price - glTest[count].getAskPrice()[0]) * 0.001;
                        Strategy.price = 0;
                    }
                }

                if ((- str[count - 1].request <= glTest[count].getBidPrice()[0]) & (str[count - 1].request < - eps)){
                    if (Math.abs(Strategy.price) < eps){
                        Strategy.price = - glTest[count].getBidPrice()[0];
                    }
                    if (Math.abs(Strategy.price) > eps){
                        str[count - 1].profit = (- Strategy.price - glTest[count].getBidPrice()[0]) * 0.001;
                        Strategy.price = 0;
                    }
                }

                if (f > par){
                    str[count].request = glTest[count].getAskPrice()[0];
                }
                if (f < - par){
                    str[count].request = - glTest[count].getBidPrice()[0];
                }
                ++count;
            }
            double e = 0, d = 0;
            for (int i = 0; i < count; ++i){
                e += str[i].profit;
            }
            e /= count;
            for (int i = 0; i < count; ++i){
                d += (str[i].profit - e) * (str[i].profit - e);
            }
            d /= count;
            System.out.println("Мат. ожидание: " + e);
            System.out.println("Дисперсия: " + d);
            System.out.println("Шарп: " + e/Math.sqrt(d));
        } catch (IOException e) {
            System.out.println("Error: " + e);

        }finally {
            brTest.close();
        }
    }
}
