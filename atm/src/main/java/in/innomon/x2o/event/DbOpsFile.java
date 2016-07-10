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

package in.innomon.x2o.event;

import com.google.gson.Gson;
import in.innomon.util.DbOps;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class DbOpsFile implements DbOps {
    private String loggerName = "UPAY";

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
    
 
    protected String outDir = "/tmp";
     Gson gson = new Gson();
  

    public String getOutDir() {
        return outDir;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    @Override
    public void insert(String txnId, Object rec) {
       storeProp( txnId, rec, "Insert");
    }

    @Override
    public void delete(String txnId, Object rec) {
      storeProp( txnId, rec, "Delete");
    }

    @Override
    public void update(String txnId, Object rec) {
      storeProp( txnId, rec, "Update");
    }

    protected void storeProp(String txnId, Object rec, String comment) {
        File fl = new File(outDir, txnId +comment+ ".json");
        Logger.getLogger(loggerName).log(Level.FINEST, "DbOpeFile.storeProp [{0}]", fl.getPath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fl, true); // append mode
            fos.write(gson.toJson(rec).getBytes());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    protected void storeObj(String txnId, Object obj, String comment) {
        if (obj instanceof Properties) {
            storeProp(txnId, (Properties) obj, comment);
        } else {
            File fl = new File(outDir, txnId + ".txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fl, true); // append mode
                fos.write('\n');
                fos.write(comment.getBytes());
                fos.write('\n');
                fos.write(obj.toString().getBytes());

            } catch (FileNotFoundException ex) {
                Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }// else
    }
}
