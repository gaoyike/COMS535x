/**
 * Created by chenguanghe on 2/26/15.
 */
public class MinHashAccuracy {
    private MinHashAccuracy(){}
    private double error;
    private MinHash minHash;
    public MinHashAccuracy(String folder, int numPermutations, double error){
        this.error = error;
        this.minHash = new MinHash(folder, numPermutations);
    }
    public int PairsWithBiggerError (){
        int count = 0;
        String[] allDocs = minHash.allDocs();
        for (int i = 0; i < allDocs.length; i++) {
            for (int j = i+1; j < allDocs.length; j++) {
                if (Math.abs(minHash.exactJaccard(allDocs[i],allDocs[j]) - minHash.approximateJaccard(allDocs[i],allDocs[j])) > error){
                    count++;
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        double[] error = new double[]{0.04, 0.07, 0.09};
        int[] permu = new int[]{400, 600, 800};
        for (double d : error)
            for (int p : permu){
                MinHashAccuracy minHashAccuracy = new MinHashAccuracy("/Users/chenguanghe/Desktop/space", p, d);
                System.out.println("--------------- Experiment with error:" + d +" number of permutation:"+p+" ---------------");
                MinHashSpeed speed = new MinHashSpeed("/Users/chenguanghe/Desktop/space",p);
                System.out.println("Pairs with Bigger Error: "+minHashAccuracy.PairsWithBiggerError());
                System.out.println("Exact Speed:" + speed.TimeUsedInExactJac()+" Approximate Speed:" + speed.TimeUsedInApproxJac());
            }
    }

}
