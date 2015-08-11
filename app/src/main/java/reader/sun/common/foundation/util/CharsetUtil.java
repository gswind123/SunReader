package reader.sun.common.foundation.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;

/**
 * Provide charset decoding and encoding tools
 * Created by yw_sun on 2015/8/10.
 */
public class CharsetUtil {
    private static ArrayList<Charset> charsetList = null;
    static {
        //The order is important: UTF-16 will succeed for most charset, but
        //mostly may be wrong
        charsetList = new ArrayList<Charset>();
        String[] charsets = {"ASCII", "GBK", "UTF-8", "UTF-16"};
        for(String charsetStr : charsets) {
            Charset charset = Charset.forName(charsetStr);
            if(charset == null) {
                continue;
            }
            charsetList.add(charset);
        }
    }

    /**
     * Decode text data with a specific charset
     * @param textData:Input data | decodedStr:Decoded data,if only used for charset-checking,set <code>null</code> | charset:Specific charset
     * @return true:Decode successfully | false:Otherwise
     * @throws java.io.IOException
     */
    public static boolean decode(byte[] textData, StringBuilder decodedStr, Charset charset)throws IOException {
        ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(textData));
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        boolean inputEnds = false;
        while(!inputEnds) {
            int size = channel.read(byteBuffer);
            byteBuffer.flip();
            if(size == -1){
                inputEnds = true;
            }
            CoderResult result = null;
            do{
                result = decoder.decode(byteBuffer, charBuffer, inputEnds);
                charBuffer.flip();
                if(result.isError()) {
                    return false;
                }
                if(decodedStr != null && charBuffer.hasRemaining()) {
                    decodedStr.append(charBuffer.toString());
                }
                charBuffer.clear();
            }while(result != CoderResult.UNDERFLOW);
            byteBuffer.compact();
        }
        CoderResult result = null;
        do{
            result = decoder.flush(charBuffer);
            charBuffer.flip();
            if(result.isError()) {
                return false;
            }
            if(decodedStr != null && charBuffer.hasRemaining()) {
                decodedStr.append(charBuffer);
            }
            charBuffer.clear();
        }while(result != CoderResult.UNDERFLOW);
        return true;
    }

    /**
     * Detect the charset of text data
     * @param inStream:Text data for detecting
     * @return Result charset, null for detection failed
     */
    public static Charset detectCharset(InputStream inStream) {
        Charset resCharset = null;
        if(inStream == null) {
            return resCharset;
        }
        byte[] startChars = new byte[2048];
        try{
            inStream.read(startChars, 0, 2048);
            for(Charset charset : charsetList) {
                if(decode(startChars, null, charset) == true) {
                    resCharset = charset;
                    break;
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
        return resCharset;
    }
}
