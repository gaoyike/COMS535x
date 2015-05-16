/**
 * Created by chenguanghe on 2/26/15.
 */
public class MinHashSpeed {
    private MinHashSpeed(){}
    private MinHash minHash;
    public MinHashSpeed(String folder, int numPermutations) {
        this.minHash = new MinHash(folder,numPermutations);
    }

    public double TimeUsedInExactJac (){
        Timer tmp = new Timer();
        String[] allDocs = minHash.allDocs();
        for (int i = 0; i < allDocs.length - 1; i++) {
            for (int j = i+1; j < allDocs.length; j++) {
                    String d1 = allDocs[i];
                    String d2 = allDocs[j];
                    minHash.exactJaccard(d1, d2);
            }
        }
        return tmp.elapsedTime();
    }
    public double TimeUsedInApproxJac (){
        Timer tmp = new Timer();
        String[] allDocs = minHash.allDocs();
        for (int i = 0; i < allDocs.length - 1; i++) {
            for (int j = i+1; j < allDocs.length; j++) {
                    String d1 = allDocs[i];
                    String d2 = allDocs[j];
                    minHash.approximateJaccard(d1, d2);
            }
        }
        return tmp.elapsedTime() + minHash.getMinHashTime();
    }

    public static void main(String[] args) {
        MinHashSpeed minHashSpeed = new MinHashSpeed("/Users/chenguanghe/Desktop/pa", 100);
        System.out.println("Exact Jac takes: "+minHashSpeed.TimeUsedInExactJac()+" seconds");
        System.out.println("Approx Jac takes: "+minHashSpeed.TimeUsedInApproxJac()+" seconds");
    }
}
