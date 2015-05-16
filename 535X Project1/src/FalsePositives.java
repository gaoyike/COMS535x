import java.util.HashSet;
import java.util.Random;

/**
 * Created by chenguanghe on 1/29/15.
 */
public class FalsePositives {
    private BloomFilterDet bloomFilterDet;
    private BloomFilterRan bloomFilterRan;
    private HashSet<String> strings;
    private Random random;
    private int totalNum;
    private final String alphas = "abcdefghijklmnopqrstuvwxyz0123456789";

    private FalsePositives() {
    }

    public FalsePositives(int setSize) {
        bloomFilterDet = new BloomFilterDet(setSize);
        bloomFilterRan = new BloomFilterRan(setSize);
        strings = new HashSet<String>();
        random = new Random();
        totalNum = 0;
    }

    public double[] falsePositive(int sampleNum, int LengthofString) {
        assert sampleNum >= 0;
        //add to filter ans hashset
        for (int i = 0; i < sampleNum; i++) {
            String s = generateRandomString(LengthofString);
            bloomFilterDet.add(s);
            bloomFilterRan.add(s);
            strings.add(s);
            totalNum++;
        }
        int countOfDie = 0;
        int countOfRan = 0;
        int countOfTest = 0;
        String test;
        do {
            test = generateRandomString(LengthofString);
            if (!strings.contains(test) && bloomFilterDet.appears(test))//false positive
                countOfDie++;
            countOfTest++;
        } while (countOfDie <= 100);
        double die = (double) countOfDie / countOfTest;
        countOfTest = 0;
        do {
            test = generateRandomString(LengthofString);
            if (!strings.contains(test) && bloomFilterRan.appears(test))//false positive
                countOfRan++;
            countOfTest++;
        } while (countOfRan <= 100);
        double ran = (double) countOfRan / countOfTest;
        return new double[]{die, ran};
    }

    public int getTotalNum() {
        return totalNum;
    }

    private String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int character = (int) (Math.random() * alphas.length());
            builder.append(alphas.charAt(character));
        }
        return builder.toString();
    }
}
