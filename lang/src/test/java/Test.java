import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;

/**
 * Created by tao on 5/24/16.
 */
public class Test {
    public static void main(String[] args) {
        String path = "/a/b/c/d/";
        File f = new File(path);
        System.out.println(f.getName());

    }
}
