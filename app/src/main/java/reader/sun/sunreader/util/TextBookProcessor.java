package reader.sun.sunreader.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
            do{
                File bookFile = new File(filePath);
                if(bookFile.exists() == false) {
                    break;
                }
                FileInputStream inStream = new FileInputStream(bookFile);
                int curByteOffset = 0;
                int curCharOffset = 0;
                int block = 2048;
                byte[] buffer = new byte[block];
                int size;
                do{
                    size = inStream.read(buffer, curByteOffset, curCharOffset);
                    if(size == 0) {
                        break;
                    }
                    curByteOffset += size;

                }while(size == block);
            }while(false);
        }catch(IOException e){

        }
        return bookInfo;
    }
}
