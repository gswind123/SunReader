package reader.sun.common.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Model for a book's info,include length, last reading position, etc
 * The model must can be serialized
 * Created by yw_sun on 2015/7/24.
 */
public class BookInfo {
    public BookInfo(){}
    /** Override this to create this from an input stream*/
    public void load(InputStream in) {}
    /** Override this to write this to an output stream*/
    public void save(OutputStream out) {}
    /** Convert book info to serialized data*/
    @Override
     public String toString() {
         ByteArrayOutputStream outStream = new ByteArrayOutputStream();
         save(outStream);
         return new String(outStream.toByteArray());
    }
}
