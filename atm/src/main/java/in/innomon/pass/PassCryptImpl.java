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

package in.innomon.pass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
// java.util.Base64.Encoder  since jdk 8 ;)

/**
 *
 * @author ashish
 */
public class PassCryptImpl implements SecurePass {

    static final String VERSION = "C0FFEE01";
    public static final String ALGO = "AES";

    private transient SecureRandom rand = null;
    private transient SecretKey skey = null;

    public PassCryptImpl() {

    }

    public PassCryptImpl(String salt) {
        init(salt);
    }

    private void init(String salt) {
        if (rand == null) {
            rand = new SecureRandom(salt.getBytes());
            try {
                KeyGenerator kgen = javax.crypto.KeyGenerator.getInstance(ALGO);
                kgen.init(rand);
                skey = kgen.generateKey();
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(PassCryptImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void setSalt(String salt) {
        init(salt);
    }

    @Override
    public byte[] seal(String pass, String uid) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException {        
        SealedObject sealed = sealObj(pass, uid);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(sealed);
        os.flush();
        os.close();

        return bos.toByteArray();
    }

    /**
     *
     * @param pass
     * @param uid
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     */
    @Override
    public SealedObject sealObj(String pass, String uid) throws IOException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        Cipher c = null;
        try {
            c = Cipher.getInstance(ALGO);
            c.init(c.ENCRYPT_MODE, skey);
            
        } catch (InvalidKeyException ex) {
            Logger.getLogger(PassCryptImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
       return new SealedObject(new UPass(uid, pass), c);
    }
    @Override
    public long createChallenge() {
        if (rand == null) {
            init(VERSION);
        }
        return rand.nextLong();
    }

    @Override
    public boolean verify(byte[] sealedPass, String uid, long challenge, byte[] shaHash) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        ByteArrayInputStream bin = new ByteArrayInputStream(sealedPass);
        ObjectInputStream is = new ObjectInputStream(bin);
        SealedObject sealed = (SealedObject) is.readObject();
        

        return verify(sealed, uid, challenge, shaHash);
    }
     @Override
    public boolean verify(SealedObject sealed, String uid, long challenge, byte[] shaHash) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        
        UPass upass = (UPass) sealed.getObject(skey);
        boolean ret = false;
        if (upass.uid.equals(uid)) {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String toChk = upass.uid + ',' + upass.pass + ',' + challenge;
            byte[] digest = md.digest(toChk.getBytes());
            if (digest.length == shaHash.length) {
                ret = true;
                for (int i=0; i < digest.length;i++) {
                    if (digest[i] != shaHash[i]) {
                        ret = false;
                        break;
                    }
                }
            }
        } else {
            Logger.getLogger(PassCryptImpl.class.getName()).log(Level.WARNING, "uid missmatch [{0}],[{1}]", new Object[]{uid, upass.uid});
        }

        return ret;
    }


    public static class UPass implements Serializable {

        public UPass() {
        }

        public UPass(String uid, String pass) {
            this.uid = uid;
            this.pass = pass;
        }
        public String uid = null;
        public String pass = null;
    }
}
