import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by chenguanghe on 2/25/15.
 */
public class MinHash {
    private String folder;
    private int numPermutations;
    private Permutations[] permutationses;
    private HashMap<String, HashSet<String>> allStringsPerFile = new HashMap<String, HashSet<String>>(2000);
    private HashMap<String, int[]> termsBinaryRepresenatation = new HashMap<String, int[]>(1500);
    private HashMap<String, int[]> MinHash = new HashMap<String, int[]>(20000);
    private double minHashTime = 0.0;
    private HashSet<String> terms = new HashSet<String>();

    private MinHash() {
    }

    public MinHash(String folder, int numPermutations) {
        this.folder = folder;
        this.numPermutations = numPermutations;
        this.permutationses = new Permutations[numPermutations];
        File file = new File(folder);
        File[] files = file.listFiles();
        for (File f : files) {
            try {
                Scanner scanner = new Scanner(f);
                HashSet<String> hashSet = new HashSet<String>();
                while (scanner.hasNext()) {
                    String s = scanner.next();
                    String[] strings = modifyString(s);
                    for (String st : strings) {
                        hashSet.add(st);
                        terms.add(st);
                    }
                }
                allStringsPerFile.put(f.getName(), hashSet);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //terms in binary representation
        for (String filename : allStringsPerFile.keySet()) {
            HashSet<String> hashSet = allStringsPerFile.get(filename);
            Iterator<String> iterator = terms.iterator();
            int[] ary = new int[terms.size()];
            Arrays.fill(ary, -1);
            int i = 0;
            int j = 0;
            while (iterator.hasNext()){
//                  ary[i++] = hashSet.contains(iterator.next())? true:false;
                    if (hashSet.contains(iterator.next())){
                        ary[i] = j;
                        i++;
                    }
                j++;
            }
            termsBinaryRepresenatation.put(filename, ary);
        }
        Timer minHashConsuming = new Timer();
        //initial permutation;
        for (int i = 0; i < permutationses.length; i++) {
            permutationses[i] = new Permutations(terms.size());
        }
        //MinHash
        for (String filename : termsBinaryRepresenatation.keySet()) {
            int[] minHash = new int[numPermutations];
            int[] bins = termsBinaryRepresenatation.get(filename);
            for (int i = 0; i < permutationses.length; i++) {
                int min = Integer.MAX_VALUE;
                for (int j = 0; j < bins.length && bins[j] != -1; j++) {
                    min = Math.min(permutationses[i].mapTo(bins[j]), min);
                }
                minHash[i] = min;
            }
            MinHash.put(filename, minHash);
        }
        minHashTime = minHashConsuming.elapsedTime();
    }

    public double approximateJaccard(String file1, String file2) {
        int[] d1 = MinHash.get(file1);
        int[] d2 = MinHash.get(file2);
        int intersection = 0;
        for (int i = 0; i < d1.length; i++) {
            if (d1[i] == d2[i])
                intersection++;
        }
        return (double) intersection / (double) numPermutations;
    }

    public String[] allDocs() {
        return MinHash.keySet().toArray(new String[]{});
    }

    public double exactJaccard(String file1, String file2) {
        int[] d1 = termsBinaryRepresenatation.get(file1);
        int[] d2 = termsBinaryRepresenatation.get(file2);
        int intersection = 0;
        int union = 0;
        int i = 0;
        int j = 0;
        while (d1[i] != -1 && d2[j] != -1) {
            if (d1[i] == d2[j]){
                intersection++;
                i++;
                j++;
            }
            else if (d1[i] < d2[j]){
                i++;
            }
            else{
                j++;
            }
            union++;
        }
        union++;
        return (double) intersection / (double) union;
    }

    public int numTerms() {
        return terms.size();
    }

    public double getMinHashTime (){
        return minHashTime;
    }
    public int numPermutations() {
        return numPermutations;
    }

    public int[][] minHashMatrix() {
        int[][] matrix = new int[numPermutations][MinHash.keySet().size()];
        Iterator<String> iter = MinHash.keySet().iterator();
        for (int i = 0; i < MinHash.keySet().size(); i++) {
            int[] hashs = MinHash.get(iter.next());
            for (int j = 0; j < numPermutations; j++) {
                matrix[j][i] = hashs[j];
            }
        }
        return matrix;
    }

    private String[] modifyString(String s) {
        ArrayList<String> rest = new ArrayList<String>();
        String res = s;
        res = res.toLowerCase();
        String[] result = res.split("[^a-zA-Z0-9]");
        for (String t : result){
            if (t.length() > 3 && !t.equals("the"))
                rest.add(t);
        }
        return rest.toArray(new String[rest.size()]);
    }

    public static void main(String[] args) {
        MinHash minHash = new MinHash("/Users/chenguanghe/Desktop/space", 5);
        int[][] minHashMatrix = minHash.minHashMatrix();
        for (int i = 0; i < minHashMatrix.length; i++) {
            for (int j = 0; j < minHashMatrix[0].length; j++) {
                System.out.print(minHashMatrix[i][j]+" ");
            }
            System.out.println();
        }
    }
}
