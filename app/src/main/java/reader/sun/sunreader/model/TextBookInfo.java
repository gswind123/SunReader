package reader.sun.sunreader.model;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import reader.sun.common.foundation.util.StringUtil;
import reader.sun.common.model.BookInfo;

/**
 * The BookInfo model for text book
 * Created by yw_sun on 2015/7/24.
 */
public class TextBookInfo extends BookInfo {
    /** tags for xml-parsing */
    static private final String TAG_BOOK_INFO = "BOOK_INFO";

    static private final String TAG_BOOK = "BOOK";
    static private final String ATTR_BOOKNAME = "bookname";
    static private final String ATTR_FILEPATH = "filepath";
    static private final String ATTR_CHARSET = "charset";
    static private final String ATTR_LENGTHINBYTE = "length_in_byte";
    static private final String ATTR_LENGTHINCHAR = "length_in_char";
    static private final String ATTR_LASTCHARINDEX = "last_char_index";

    static private final String TAG_MAP = "MAP";
    static private final String TAG_MAPITEM = "MAP_ITEM";
    static private final String ATTR_CHAR_INDEX = "char_index";
    static private final String ATTR_BYTE_INDEX = "byte_index";

    /** Basic book info */
    public String mBookName = "";
    public String mFilePath = "";
    public String mCharset = "";
    public long mLengthInByte = 0;
    public long mLengthInChar = 0;

    /**
     * ChatIndex-to-Byte mapping array
     *  This map is used to help to load a part of the book
     *  with the byte offset.While char offset is commonly used
     *  in text page works, byte offset is necessary for file-loading
     */
    public HashMap<Integer, Integer> mChar2Byte = new HashMap<Integer, Integer>();

    /** last-reading info*/
    public TextDataLocator mLastLocator = new TextDataLocator();

    public TextBookInfo() {
        super();
    }

    @Override
    public BookIOResult load(InputStream in) {
        String errorMsg = "";
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(in);
            Element root = doc.getDocumentElement();
            NodeList books = root.getElementsByTagName(TAG_BOOK);
            if(books.getLength() != 0) {
                Node book = books.item(0);
                NamedNodeMap attrMap = book.getAttributes();
                this.mBookName = attrMap.getNamedItem(ATTR_BOOKNAME).getNodeValue();
                this.mFilePath = attrMap.getNamedItem(ATTR_FILEPATH).getNodeValue();
                this.mCharset = attrMap.getNamedItem(ATTR_CHARSET).getNodeValue();
                this.mLengthInByte = StringUtil.toInt(attrMap.getNamedItem(ATTR_LENGTHINBYTE).getNodeValue());
                this.mLengthInChar = StringUtil.toInt(attrMap.getNamedItem(ATTR_LENGTHINCHAR).getNodeValue());
                this.mLastLocator = new TextDataLocator();
                this.mLastLocator.mStartIndex = StringUtil.toInt(attrMap.getNamedItem(ATTR_LASTCHARINDEX).getNodeValue());
            }

            Node char2ByteMap = root.getElementsByTagName(TAG_MAP).item(0);
            NodeList char2ByteList = char2ByteMap.getChildNodes();
            this.mChar2Byte = new HashMap<Integer, Integer>();
            for(int i=0;i<char2ByteList.getLength();i++) {
                Node item = char2ByteList.item(i);
                String localName = item.getNodeName();
                if(!StringUtil.emptyOrNull(localName) && localName.equals(TAG_MAPITEM)) {
                    NamedNodeMap attrMap = item.getAttributes();
                    int charIndex = StringUtil.toInt(attrMap.getNamedItem(ATTR_CHAR_INDEX).getNodeValue());
                    int byteIndex = StringUtil.toInt(attrMap.getNamedItem(ATTR_BYTE_INDEX).getNodeValue());
                    this.mChar2Byte.put(charIndex, byteIndex);
                }
            }
        }catch(ParserConfigurationException e) {
            errorMsg = "TextBookInfo::"+mBookName+" ParserConfigurationException:"+e.getMessage();
        }catch(IOException e) {
            errorMsg = "TextBookInfo::"+mBookName+" IOException:"+e.getMessage();
        }catch(SAXException e) {
            errorMsg = "TextBookInfo::"+mBookName+" SAXException:"+e.getMessage();
        }
        boolean hasError = !StringUtil.emptyOrNull(errorMsg);
        BookIOResult result = new BookIOResult(hasError, errorMsg);
        if(hasError) {
            Log.e("reader.sun.sunreader", errorMsg);
        }
        return result;
    }
    @Override
    public BookIOResult save(OutputStream out) {
        String errorMsg = "";
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement(TAG_BOOK_INFO);
            doc.appendChild(root);

            Element book = doc.createElement(TAG_BOOK);
            book.setAttribute(ATTR_BOOKNAME, this.mBookName);
            book.setAttribute(ATTR_FILEPATH, this.mFilePath);
            book.setAttribute(ATTR_CHARSET, this.mCharset);
            book.setAttribute(ATTR_LENGTHINBYTE, this.mLengthInByte+"");
            book.setAttribute(ATTR_LENGTHINCHAR, this.mLengthInChar+"");
            book.setAttribute(ATTR_LASTCHARINDEX, this.mLastLocator.mStartIndex+"");
            root.appendChild(book);

            Element map = doc.createElement(TAG_MAP);
            for(Map.Entry<Integer, Integer> entry : mChar2Byte.entrySet()) {
                Element item = doc.createElement(TAG_MAPITEM);
                item.setAttribute(ATTR_CHAR_INDEX, entry.getKey()+"");
                item.setAttribute(ATTR_BYTE_INDEX, entry.getValue()+"");
                map.appendChild(item);
            }
            root.appendChild(map);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            PrintWriter writer = new PrintWriter(out);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source,result);
        }catch(ParserConfigurationException e){
            errorMsg = "TextBookInfo::"+mBookName+" ParserConfigurationException:"+e.getMessage();
        }catch(TransformerConfigurationException e) {
            errorMsg = "TextBookInfo::"+mBookName+" TransformerConfigurationException:"+e.getMessage();
        }catch(TransformerException e) {
            errorMsg = "TextBookInfo::"+mBookName+" TransformerException:"+e.getMessage();
        }
        boolean hasError = !StringUtil.emptyOrNull(errorMsg);
        BookIOResult result = new BookIOResult(hasError, errorMsg);
        if(hasError) {
            Log.e("reader.sun.sunreader", errorMsg);
        }
        return result;
    }
}
