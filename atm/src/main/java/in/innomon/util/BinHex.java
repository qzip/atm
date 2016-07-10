/*
* Copyright (c) 2016, BON BIZ IT Services Pvt LTD.
*
* The Universal Permissive License (UPL), Version 1.0
* 
* Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data (collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both

* (a) the Software, and

* (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with the Software (each a “Larger Work” to which the Software is contributed by such licensors),
* 
* without restriction, including without limitation the rights to copy, create derivative works of, display, perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made, and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either these or other terms.
* 
* This license is subject to the following condition:
* 
* The above copyright notice and either this complete permission notice or at a minimum a reference to the UPL must be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* Author: Ashish Banerjee, tech@innomon.in
*/

package in.innomon.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class BinHex {

    static final String hexy = "0123456789ABCDEF";

    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) {
        byte[] bya = {(byte) 0x21, (byte) 0xCF, (byte) 0xBA, (byte) 0x75};
        
        String x = bin2hex(bya);
        System.out.println(x);
        
        
        byte y = hex2nibble((byte)'a');
        System.out.printf("hex2nibble = %x\n",y); 
        
        byte [] h2b = hex2bin("AF0136");
        System.out.printf("Len = %d\n", h2b.length);
        for(int i=0; i < h2b.length; i++) 
            System.out.printf("%d: %x\n",i,h2b[i]);
    }
*/

    public static String bin2hex(byte[] raw) {
        StringBuilder sb = new StringBuilder();
        for (int i =0 ; i < raw.length; i++) {
            byte lsNib, msNib;
            lsNib = (byte) (raw[i] & (byte) 0x0f);
           
            msNib = (byte) ((byte) ((raw[i] & (byte) 0xf0) >> 4)&(byte) 0x0f) ;
           
            sb.append(nibble2hex(msNib));
            sb.append(nibble2hex(lsNib));
        }
        return sb.toString();
    }

    public static byte[] hex2bin(String hex) {
        int len = hex.length();
        hex = hex.toUpperCase();
        byte[] inarr = hex.getBytes();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        for (int i=0; i < len ; i+=2) {
            if (hexy.indexOf(inarr[i]) >= 0) {
                byte msNib, lsNib, full;
                lsNib = hex2nibble(inarr[i]);
                msNib = (i+1 < len)? hex2nibble(inarr[i+1]): 0;                                       
                full = (byte) (msNib | (lsNib << 4));
                bos.write(full);
            } else {
                throw new IllegalArgumentException("invalid char [" + (char) inarr[i] + "] at " + i);
            }
        }
        try {
            bos.close();
        } catch (IOException ex) {
            Logger.getLogger(BinHex.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return bos.toByteArray();
       
    }
    private static byte  hex2nibble(byte in) {
        byte ret;
        if(in >= '0' && in <= '9' ) {
            ret = (byte) (in - '0');
        } else if (in >= 'a' && in <= 'f' ) {
            ret = (byte) (in - 'a'+10);
        } else if (in >= 'A' && in <= 'F' ) {
            ret = (byte) (in - 'A'+10);
        }
        else 
            throw new IllegalArgumentException("invalid char ["+in+"]");
        return ret;
    }
    private static char nibble2hex(byte in) {
        
        byte [] ab = {0};
        ab[0] = (byte) ((in <= (byte)9)? in + '0' : in - 10 + 'A');
        String ret = new String(ab);
        //System.out.printf("%x is %s than 9 --> %s\n", in, (in <= (byte)9)?"less": "more",ret);
        return (char)ab[0];
    } 
}
