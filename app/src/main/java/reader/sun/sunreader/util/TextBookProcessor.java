package reader.sun.sunreader.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

                //parse book char2byte and calc book length
                FileInputStream inStream = new FileInputStream(bookFile);
                int curByteOffset = 0;
                int curCharOffset = 0;
                int block = ReaderConstantValue.TextCapacity;
                byte[] buffer = new byte[block];
                int size;
                bookInfo.mChar2Byte.clear();
                do{
                    size = inStream.read(buffer, 0, block);
                    if(size == 0) {
                        break;
                    }
                    bookInfo.mChar2Byte.put(curCharOffset, curByteOffset);
                    curByteOffset += size;
                    curCharOffset += (new String(buffer)).length();
                }while(size == block);
                bookInfo.mChar2Byte.put(curCharOffset, curByteOffset);
                bookInfo.mLengthInByte = (long)curByteOffset;
            }while(false);
        }catch(IOException e){}
        return bookInfo;
    }
}
