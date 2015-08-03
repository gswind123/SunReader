package reader.sun.sunreader.model;

import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import reader.sun.common.model.BookInfo;

/**
 * The BookInfo model for text book
 * Created by yw_sun on 2015/7/24.
 */
public class TextBookInfo extends BookInfo {
    /** tags for xml-parsing */
    static private final String TAG_BOOK = "BOOK";
    static private final String ATTR_BOOKNAME = "bookname";
    static private final String ATTR_FILEPATH = "filepath";
    static private final String ATTR_LENGTHINBYTE = "lenght_in_byte";
    static private final String ATTR_LASTCHARINDEX = "last_char_index";

    static private final String TAG_MAPITEM = "MAP_ITEM";
    static private final String ATTR_CHAR_INDEX = "char_index";
    static private final String ATTR_BYTE_INDEX = "byte_index";

    /** Basic book info */
    public String mBookName = "";
    public String mFilePath = "";
    public long mLengthInByte = 0;

    /**
     * ChatIndex-to-Byte mapping
     *  This map is used to help to load a part of the book
     *  with the byte offset.While char offset is commonly used
     *  in text page works, byte offset is necessary for file-loading
     */
    public HashMap<Long, Long> mChar2Byte = new HashMap<Long, Long>();

    /** last-reading info*/
    public TextDataLocator mLastLocator = new TextDataLocator();

    public TextDataLocator mLocator = new TextDataLocator();
    @Override
    public void load(InputStream in) {
        Document doc = null;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse(in);

        }catch(ParserConfigurationException e) {
            Log.e("reader.sun.sunreader","TextBookInfo::"+mBookName+" ParserConfigurationException:"+e.getMessage());
        }catch(IOException e) {
            Log.e("reader.sun.sunreader","TextBookInfo::"+mBookName+" IOException:"+e.getMessage());
        }catch(SAXException e) {
            Log.e("reader.sun.sunreader","TextBookInfo::"+mBookName+" SAXException:"+e.getMessage());
        }
        if(doc == null) {
            return ;
        }
    }
    @Override
    public void save(OutputStream out) {


    }
}
