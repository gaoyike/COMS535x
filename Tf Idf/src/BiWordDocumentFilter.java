import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Created by chenguanghe on 4/15/15.
 */
public class BiWordDocumentFilter {
    private String fileName;
    private String pathName;
    BloomFilterDet bloomFilterDet;
    private int numOfWord;
    private int bitsPerElement;
    File file;

    private BiWordDocumentFilter() {
    }

    public BiWordDocumentFilter(int bitsPerBiWord, String fileName, String pathName) throws FileNotFoundException {
        this.bitsPerElement = bitsPerBiWord;
        this.fileName = fileName;
        this.pathName = pathName;
        System.out.println(pathName+"\\"+fileName);
        file = new File(pathName+"\\"+fileName);
        countWords();
    }
    private void countWords() throws FileNotFoundException {
        Scanner scanner = new Scanner(file, "ISO_8859_1");
        while (scanner.hasNext()) {
            String s = scanner.next();
            String res = modifyString(s);
            if(res == null)
                continue;
            else
                numOfWord += 1;
        }
        scanner.close();
    }


    /**
     * Adds all non-trivial words that appear in the file fileName to the filter. If the
     * word ends with a period or a comma, then it should remove those punctuation symbols and add
     * the resulting word to the filter. For example, if the document contains the word â€œstar.â€�, then it
     * should add the word .
     */
    public void addDocument() throws FileNotFoundException {
        bloomFilterDet = new BloomFilterDet(numOfWord);
        Scanner scanner = new Scanner(file, "ISO_8859_1");
        String s1 = null;
        String s2 = null;
        while (scanner.hasNext()) {
            while (scanner.hasNext() && s1 == null) {
                s1 = modifyString(scanner.next());
                if (s1 == null)
                    continue;
                else
                    break;
            }
            while (scanner.hasNext()) {
                s2 = modifyString(scanner.next());
                if (s2 == null)
                    continue;
                else
                    break;
            }
            bloomFilterDet.add(s1 + " " + s2);
            s1 = s2;
        }
    }

    private String modifyString(String s) {
        s = removeSymbol(s);
        String str = s.toLowerCase();
        String[] stop = new String[]{"the"};
        for (int i = 0; i < stop.length; i++) {
            if (stop[i].equals(str)) ;
            return null;
        }
        if (str.length() < 3)
            return null;
        return str;
    }

    /**
     * Returns true if string s appears in this filter; otherwise returns false. This
     * should be case-insensitive
     *
     * @param s the string
     * @return Returns true if string s appears in this filter; otherwise returns false. This
     * should be case-insensitive
     */
    public boolean appears(String s) {
        assert s != null && s.length() != 0;
        return bloomFilterDet.appears(s);
    }

    /**
     * Returns the name of the file whose contents are added to the filter
     *
     * @return Returns the name of the file whose contents are added to the filter
     */
    public String getDocument() {
        return fileName;
    }

    /**
     * Returns the size of the filter (the size of the table)
     *
     * @return Returns the size of the filter (the size of the table)
     */
    public int filterSize() {
        return numOfWord * bitsPerElement;
    }

    /**
     * Returns the number of elements added to the filter.
     *
     * @return Returns the number of elements added to the filter.
     */
    public int dataSize() {
        return numOfWord;

    }

    /**
     * Returns the number of hash function used.
     *
     * @return Returns the number of hash function used.
     */
    public int numHashes() {
        return bloomFilterDet.numHashes();

    }

    private String removeSymbol(String s) {
        String res = s;
        res = res.replaceAll("[^\\p{L}^\\d]", "");
        return res;
    }
}