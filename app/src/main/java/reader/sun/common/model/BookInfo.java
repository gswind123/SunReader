package reader.sun.common.model;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Model for a book's info,include length, last reading position, etc
 * The model must can be serialized
 * Created by yw_sun on 2015/7/24.
 */
public abstract class BookInfo {
    public class BookIOResult {
        private boolean isError = false;
        private String errorMessage = "";

        public BookIOResult(boolean hasError, String msg) {
            isError = hasError;
            errorMessage = msg;
        }
        public BookIOResult(boolean hasError) {
            this(hasError,"");
        }
        public BookIOResult() {
            this(false, "");
        }

        public boolean isError() {
            return isError;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public void setError(boolean hasError,String msg) {
            isError = hasError;
            errorMessage = msg;
        }
    }

    public BookInfo(){}
    /** Override this to create this from an input stream*/
    abstract public BookIOResult load(InputStream in);
    /** Override this to write this to an output stream*/
    abstract public BookIOResult save(OutputStream out);
    /**
     * Convert book info to serialized data
     * @param bookInfo:book to serialize
     * @return serialized book data
     */
     static public String serialize(BookInfo bookInfo) {
         if(bookInfo == null) {
             return "";
         }
         ByteArrayOutputStream outStream = new ByteArrayOutputStream();
         BookIOResult result = bookInfo.save(outStream);
         if(result.isError()) {
             return "";
         }
         return new String(outStream.toByteArray());
    }
    /**
     * Convert serialized data to book info instance
     * @param data:obtained from {#serialize} | cls:child class of BookInfo
     * @return an instance of {#cls}
     */
    static public BookInfo deserialize(String data, Class<?> cls) {
        if(!BookInfo.class.isAssignableFrom(cls)) {
            return null;
        }
        BookInfo bookInfo = null;
        try{
            Constructor<?> constructor =  cls.getConstructor();
            Object object = constructor.newInstance();
            bookInfo = (BookInfo)object;
            ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes());
            BookIOResult result = bookInfo.load(input);
            if(result.isError()) {
                return null;
            }
        }catch(NoSuchMethodException e ){
            Log.e("reader.sun.sunreader", e.getMessage());
        }catch(IllegalAccessException e){
            Log.e("reader.sun.sunreader", e.getMessage());
        }catch(InstantiationException e){
            Log.e("reader.sun.sunreader", e.getMessage());
        }catch(InvocationTargetException e){
            Log.e("reader.sun.sunreader", e.getMessage());
        }
        return bookInfo;
    }
}
