package com.bz;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StreamUtil {  

    public static byte[] inputStream2Byte(InputStream inStream)  
            throws Exception {  
        int count = 0;  
        while (count == 0) {  
            count = inStream.available();  
        }  
        byte[] b = new byte[count];  
        inStream.read(b);  
        return b;  
    }  

    public static InputStream byte2InputStream(byte[] b) throws Exception {  
        InputStream is = new ByteArrayInputStream(b);  
        return is;  
    }  

    public static short byteToShort(byte[] b) {  
    	return   (short) (b[1] & 0xFF |(b[0] & 0xFF) << 8); 
    }  

    public static byte[] intToByte(int res) {  
    	byte[] targets = new byte[4]; 
		targets[3] = (byte) (res & 0xff);// 最低位
		targets[2] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[1] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[0] = (byte) (res >>> 24);// 最高位
		return targets; 
    }  

    public static int bytesToInt(byte[] b) {  
    	return   b[3] & 0xFF |   
                (b[2] & 0xFF) << 8 |   
                (b[1] & 0xFF) << 16 |   
                (b[0] & 0xFF) << 24;   
    }  

    public static byte[] longToByte(long number) {  
    	byte[] targets = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((number >>> offset) & 0xff);
        }
        return targets;
    }  

    public static long byteToLong(byte[] b) {  
        long s = 0;  
        long s0 = b[0] & 0xff;
        long s1 = b[1] & 0xff;  
        long s2 = b[2] & 0xff;  
        long s3 = b[3] & 0xff;  
        long s4 = b[4] & 0xff;
        long s5 = b[5] & 0xff;  
        long s6 = b[6] & 0xff;  
        long s7 = b[7] & 0xff;
        s1 <<= 8;  
        s2 <<= 16;  
        s3 <<= 24;  
        s4 <<= 8 * 4;  
        s5 <<= 8 * 5;  
        s6 <<= 8 * 6;  
        s7 <<= 8 * 7;  
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;  
        return s;  
    }
    
    public static byte[] byteArrays(byte[]... b){
		List<Byte> list = new ArrayList<>();
		for(int i=0;i<b.length;i++) {
			for(int j=0;j<b[i].length;j++) {
				list.add(b[i][j]);
			}
		}
		byte res[] = new byte[list.size()];
		for(int i=0;i<list.size();i++) {
			res[i]=list.get(i);
		}
		return res;
	}
    
    public static byte[] shortToByte(short res){
    	byte[] targets = new byte[2]; 
		targets[1] = (byte) (res & 0xff);
		targets[0] = (byte) ((res >>> 8) & 0xff);
		return targets; 
    }
}