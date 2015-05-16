import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by chenguanghe on 2/8/15.
 */
public class Experiments {
//    n Number of items in the filter
//    p Probability of false positives, float between 0 and 1 or a number indicating 1-in-p
//    m Number of bits in the filter
//    k Number of hash function
//
//    m = ceil((n * log(p)) / log(1.0 / (pow(2.0, log(2.0)))));
//    k = round(log(2.0) * m / n);
    private final int lengthOfString = 8;
    private final int ExperimentTimes = 5;
    static NumberFormat numberFormat = NumberFormat.getPercentInstance();
    public static void main(String[] args) {
        Experiments experiments = new Experiments();
        numberFormat.setMinimumFractionDigits(2);
        experiments.FalsePositive1();
        experiments.FalsePositive2();
        experiments.FalsePositive3();

    }
    // Theoretically, p = 0.02, n = 1000000, m = 1017795 * 8(bit per element), k = 6
    private void FalsePositive1(){
        ArrayList<double[]> res = new ArrayList<double[]>();
        for (int i = 0 ; i < ExperimentTimes; i++) {
            FalsePositives falsePositives = new FalsePositives(1017795);
            res.add(falsePositives.falsePositive(1000000, lengthOfString));
        }
        double averageofDie = 0.0;
        double averageofRan = 0.0;
        for (double[] d: res){
            averageofDie +=d[0];
            averageofRan +=d[1];
        }
        averageofDie /= ExperimentTimes;
        averageofRan /= ExperimentTimes;
        ShowResult(new double[]{averageofDie, averageofRan}, 0.02, "FalsePositive1");
    }
    // Theoretically, p = 0.2, n = 2000000, m = 1017795 * 8(bit per element), k = 6
    private void FalsePositive2(){
        ArrayList<double[]> res = new ArrayList<double[]>();
        for (int i = 0 ; i < ExperimentTimes; i++) {
            FalsePositives falsePositives = new FalsePositives(1017795);
            res.add(falsePositives.falsePositive(1000000*2, lengthOfString));
        }
        double averageofDie = 0.0;
        double averageofRan = 0.0;
        for (double[] d: res){
            averageofDie +=d[0];
            averageofRan +=d[1];
        }
        averageofDie /= ExperimentTimes;
        averageofRan /= ExperimentTimes;
        ShowResult(new double[]{averageofDie, averageofRan}, 0.2, "FalsePositive2");
    }
    // Theoretically, p = 0.5, n = 1000000, m = 779403 * 8(bit per element), k = 4
    private void FalsePositive3(){
        ArrayList<double[]> res = new ArrayList<double[]>();
        for (int i = 0 ; i < ExperimentTimes; i++) {
            FalsePositives falsePositives = new FalsePositives(779403);
            res.add(falsePositives.falsePositive(1000000, lengthOfString));
        }
        double averageofDie = 0.0;
        double averageofRan = 0.0;
        for (double[] d: res){
            averageofDie +=d[0];
            averageofRan +=d[1];
        }
        averageofDie /= ExperimentTimes;
        averageofRan /= ExperimentTimes;
        ShowResult(new double[]{averageofDie, averageofRan}, 0.05, "FalsePositive3");
    }

    private void ShowResult(double[] doubles, double p, String s){
        System.out.println(s+": [Theoretically p is "+numberFormat.format(p)+"] [BloomFilterDet: "+numberFormat.format(doubles[0])+"] [BloomFilterRan: "+numberFormat.format(doubles[1])+"]");
    }
}
