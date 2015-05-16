import java.io.FileNotFoundException;

/**
 * Created by chenguanghe on 2/10/15.
 */
public class mymain {
    public static void main(String[] args) throws FileNotFoundException {
        BloomSearch search = new BloomSearch("/Users/chenguanghe/Desktop/sci-1.space");
        System.out.println(search.query("black hole"));
    }
}
