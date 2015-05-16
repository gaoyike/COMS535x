
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by chenguanghe on 4/15/15.
 */
public class IndexBuilder {
    class InvertedIndex {
        DictionaryPart dictionaryPart;
        ArrayList<PostingsPart> postingsParts;
        public InvertedIndex(String term, int DFt){
            dictionaryPart = new DictionaryPart(term,DFt);
            postingsParts = new ArrayList<PostingsPart>();
        }
    }
    class DictionaryPart {
        public DictionaryPart(String term, int DFt){
            this.term = term;
            this.DFt = DFt;
        }
        String term;
        int DFt = Integer.MIN_VALUE;
    }
    class PostingsPart {
        public PostingsPart(int document, int TFtd){
            this.document = document;
            this.TFtd = TFtd;
        }
        int document = Integer.MIN_VALUE;
        int TFtd = Integer.MIN_VALUE;
    }
    private String folderName;
    private int numOfFiles;
    private File fileses[];
    private IndexBuilder(){}
    private int curDocIndex = 0;
    private HashMap<String,InvertedIndex> Inverted = new HashMap<String,InvertedIndex>(20000);
    private ArrayList<ArrayList<Double>> WeightedVector;
    
    private double[] lengthOfDoc;
    public IndexBuilder(String folderName) {
        this.folderName = folderName;
    }
    public void buildIndex(){
        File file = new File(folderName);
        fileses = file.listFiles();
        numOfFiles = fileses.length;
        for (int i = 0; i < numOfFiles; i++) {
            buildFileIndex(fileses[i]);
        }
        WeightedVector = new ArrayList<ArrayList<Double>>();
        for (int i = 0 ; i < numOfFiles; i++){
            WeightedVector.add(new ArrayList<Double>());
        }
        for (InvertedIndex i : Inverted.values()){
            for (PostingsPart p : i.postingsParts) {
                WeightedVector.get(p.document).add(weigh(i.dictionaryPart.term, String.valueOf(p.document)));
            }
        }

        lengthOfDoc = new double[WeightedVector.size()];
        for (int i = 0; i < WeightedVector.size(); i++) {
            double sum = 0.0;
            for (int j = 0; j < WeightedVector.get(i).size(); j++) {
                sum += Math.pow(WeightedVector.get(i).get(j), 2);
            }
            double len = Math.sqrt(sum);
            lengthOfDoc[i] = len;
            for (int j = 0; j < WeightedVector.get(i).size(); j++) {
                WeightedVector.get(i).set(j, WeightedVector.get(i).get(j) / len);
            }
        }
    }

    private void buildFileIndex(File file) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(file, "ISO_8859_1");
        while (scanner.hasNext()) {
            String s1 = modifyString(scanner.next());
            if (s1 == null)
                continue;
            if (Inverted.containsKey(s1)){
                InvertedIndex i =  Inverted.get(s1);
                if (i.postingsParts.get(i.postingsParts.size()-1).document == curDocIndex){
                    i.postingsParts.get(i.postingsParts.size()-1).TFtd++;
                }
                else {
                    i.postingsParts.add(new PostingsPart(curDocIndex, 1));
                    i.dictionaryPart.DFt++;
                }

            }else {
                InvertedIndex i = new InvertedIndex(s1,1);
                i.postingsParts.add(new PostingsPart(curDocIndex, 1));
                Inverted.put(s1, i);
              //  System.out.println("The term in doc is " + curDocIndex + " is " + s1);
            }
        }
            curDocIndex++;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private int TFij (String i, String j) {
        if (Inverted.containsKey(i)){
            ArrayList<PostingsPart> postingsParts = Inverted.get(i).postingsParts;
            for (PostingsPart p : postingsParts){
                if (p.document == Integer.valueOf(j))
                    return p.TFtd;
            }
            return 0;
        }
        else
            return 0;
    }
    public int DFti (String t) {
        if (Inverted.containsKey(t)){
            return Inverted.get(t).dictionaryPart.DFt;
        }
        else
            return 0;
    }
    public double weigh(String t, String d) {
        return (Math.log(1 + TFij(t,d)) / Math.log(2)) * Math.log10((double) (Inverted.size()/ DFti(t)));
    }
    public File[] getFileses(){return fileses;}
    public String modifyString(String s) {
        s = removeSymbol(s);
        String str = s.toLowerCase();
        String[] stop = new String[]{"the"};
        for (int i = 0; i < stop.length; i++) {
            if (stop[i].equals(str))
            return null;
        }
        if (str.length() < 3)
            return null;
        return str;
    }
    private String removeSymbol(String s) {
        String res = s;
        res = res.replaceAll("[^\\p{L}^\\d]", "");
        return res;
    }
    
    //return the number of terms
    public int numTerm(){
    	return Inverted.size();
    }
    
    //return the length of each document
    public double[] lengthDoc(){
    	return lengthOfDoc;
    }


    public static void main(String[] args) {
        IndexBuilder indexBuilder = new IndexBuilder("D:\\cs_class\\CS535x\\Homework\\project4\\pa4\\pa4");
        indexBuilder.buildIndex();
        //
        System.out.println(indexBuilder.weigh("exhibition", "0"));
        
        System.out.println(indexBuilder.weigh("exhibition", "0"));

    }
}
