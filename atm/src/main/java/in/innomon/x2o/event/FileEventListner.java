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

import in.innomon.event.Event;
import in.innomon.event.EventCentral;
import in.innomon.event.EventListner;
import in.innomon.util.LifeCycle;
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
public class FileEventListner implements EventListner, LifeCycle {

    public static final String PROP_TXN_ID = "__TXNID__";
    public static final String PROP_TABLE_NAME = "__TABLE_NAME__";
    
    protected String outDir = "/tmp";

    private String loggerName = "UPAY";

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
  

    public String getOutDir() {
        return outDir;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }
 
       @Override
    public boolean isEventOfInterest(Event evt) {
        Logger.getLogger(loggerName).log(Level.FINEST,"Event of Interst: enter [FileEventListner]");
       //return (evt instanceof TxnEvent);
         boolean ret = (evt instanceof TxnEvent);
         Logger.getLogger(loggerName).log(Level.FINEST,"Event of Interst: [FileEventListner]: "+ret);
         //return ret;
         return ret;
    }
    public void onEvent(Event evt) {
        Logger.getLogger(loggerName).log(Level.FINEST,"On Event: enter [FileEventListner]");
        if (evt instanceof InsertEvent) {
            onInsert((InsertEvent) evt);
        } else if (evt instanceof UpdateEvent) {
            onUpdate((UpdateEvent) evt);
        } else if (evt instanceof DeleteEvent) {
            onDelete((DeleteEvent) evt);
        } else 
            Logger.getLogger(loggerName).log(Level.INFO,"[FileEventListner]On Event: None Match");
    }

    public void onInsert(InsertEvent evt) {
        storeXml(evt, "INSERT");
    }

    public void onUpdate(UpdateEvent evt) {
        storeXml(evt, "UPDATE");
    }

    public void onDelete(DeleteEvent evt) {
        storeXml(evt, "DELETE");
    }

    protected void storeXml(TxnEvent evt, String comment) {
        Logger.getLogger(loggerName).log(Level.FINEST,"FileEventListner.storeXML :"+comment);
        Properties prop = evt.getTxnVal();
        prop.setProperty(PROP_TXN_ID, evt.getTxnId());
        prop.setProperty(PROP_TABLE_NAME, evt.getDataType());
        File fl = new File(outDir, evt.getTxnId() + ".xml");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fl,true); // append mode
            prop.storeToXML(fos, comment);

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

 
  

    public void start() {
        EventCentral.subscribe(this);
        Logger.getLogger(loggerName).log(Level.FINEST,"Subscribed"+this.getClass().getName());
    }

    public void stop() {
        EventCentral.unsubscribe(this);
    }

}
