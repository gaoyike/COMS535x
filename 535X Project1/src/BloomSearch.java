import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by chenguanghe on 1/29/15.
 */
public class BloomSearch implements Configuration {
    private BloomSearch() {
    }

    ;
    private String folderName;
    private File fileses[];
    private int numOfFiles;
    private DocumentFilter[] documentFilter;

    public BloomSearch(String folderName) throws FileNotFoundException {
        this.folderName = folderName;
        File file = new File(folderName);
        fileses = file.listFiles();
        numOfFiles = fileses.length;
        documentFilter = new DocumentFilter[numOfFiles];
        for (int i = 0; i < numOfFiles; i++) {
            documentFilter[i] = new DocumentFilter(fileses[i].getName(), fileses[i].getParent());
            documentFilter[i].addDocument();
        }
    }

    public ArrayList<String> query(String s) {
        assert s != null && s.length() != 0;
        String[] strings = modifyString(s);
        ArrayList<String> arrayList = new ArrayList<String>();
        for (DocumentFilter d : documentFilter) {
            for (String str : strings) {
                if (d.appears(str) && !arrayList.contains(d.getDocument()))
                    arrayList.add(d.getDocument());
            }
        }
        return arrayList;
    }

    private String[] modifyString(String s) {
        String res = s;
        res = res.replaceAll("[,.]", "");
        return res.toLowerCase().split(" ");
    }
}
