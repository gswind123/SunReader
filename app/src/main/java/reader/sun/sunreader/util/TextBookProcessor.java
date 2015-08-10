package reader.sun.sunreader.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import reader.sun.common.foundation.util.CharsetUtil;
import reader.sun.sunreader.model.ReaderConstantValue;
import reader.sun.sunreader.model.TextBookInfo;

/**
 * Tool for text book processing
 * Created by yw_sun on 2015/7/24.
 */
public class TextBookProcessor {

    /**
     * generate the book info,including book length,memory loc,etc
     * @param filePath:book path
     * @return book info
     */
    static public TextBookInfo generateBookInfo(String filePath) {
        TextBookInfo bookInfo = new TextBookInfo();
        try{
            do{//while false
                File bookFile = new File(filePath);
                if(bookFile.exists() == false) {
                    break;
                }
                //find book name
                //example: /mnt/file/path/book_name.txt,find "book_name"
                bookInfo.mFilePath = filePath;
                String pathArray[] = filePath.split("\\/");
                bookInfo.mBookName = pathArray[pathArray.length-1].split("\\.")[0];

                //detect encoding charset
                FileInputStream inStream = new FileInputStream(bookFile);
                Charset encoding = CharsetUtil.detectCharset(inStream);
                if(encoding != null) {
                    bookInfo.mCharset = encoding.name();
                } else {
                    bookInfo.mCharset = "ASCII";
                }
                inStream.close();
                inStream = new FileInputStream(bookFile);
                //parse book char2byte and calc book length

                int curByteOffset = 0;
                int curCharOffset = 0;
                int block = ReaderConstantValue.TextCapacity;
                char[] charBuffer = new char[block];
                InputStreamReader reader = new InputStreamReader(inStream, encoding.toString());
                bookInfo.mChar2Byte.clear();
                int size = 0;
                do{
                    size = reader.read(charBuffer,0,block);
                    if(size == 0) {
                        break;
                    }
                    bookInfo.mChar2Byte.put(curCharOffset, curByteOffset);
                    curByteOffset += charBuffer.toString().getBytes(encoding.toString()).length;
                    curCharOffset += size;
                }while(size == block);
                bookInfo.mChar2Byte.put(curCharOffset, curByteOffset);
                bookInfo.mLengthInByte = (long)curByteOffset;
                bookInfo.mLengthInChar = (long)curCharOffset;
            }while(false);
        }catch(IOException e){}
        return bookInfo;
    }
}
