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

import in.innomon.util.DbOps;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import twister.system.BeanSetter;

/**
 *
 * @author ashish
 */
public class DB2ObjMapper implements DbOps {

    private String beanName = null;
    private DbOps beanStore = null;
    private String keyPropertyName = null;

    transient BeanSetter beanSetter = new BeanSetter();

    //transient ArrayList<Tuple> map = new ArrayList();

    transient Properties col2beanMap = new Properties(); 

    private String loggerName = "UPAY";

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
                
    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public DbOps getBeanStore() {
        return beanStore;
    }

    public void setBeanStore(DbOps beanStore) {
        this.beanStore = beanStore;
    }

    public String getKeyPropertyName() {
        return keyPropertyName;
    }

    public void setKeyPropertyName(String keyPropertyName) {
        this.keyPropertyName = keyPropertyName;
    }

    /**
    public void setTuple(Tuple tup) {
        map.add(tup);
    }
    **/
    public void setCol2BeanMap(Properties map) {
        col2beanMap = map;
    }
    public void setCol2BeanMapFile(String flname) {
        try {
            col2beanMap.load(new FileInputStream(flname));
        } catch (IOException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void insert(String txnId, Object txn) {
         Logger.getLogger(loggerName).log(Level.FINEST,"Db2ObjMapper: Insert() enter");
        if (!(txn instanceof TxnEvent)) {
            throw new IllegalArgumentException("Expecting Properties instance");
        }
        Properties rec = ((TxnEvent)txn).getTxnVal();
        if (beanStore == null) {
            Logger.getLogger(loggerName).log(Level.WARNING,"Db2ObjMapper: Insert(): beanStore not specified\n");
            ((Properties) rec).list(System.out);
        } else {
            if (beanName != null) {
                try {
                    Object bean = Class.forName(beanName).newInstance();
                    Properties beanProp = columnToBean((Properties) rec);
                    beanSetter.assignValues(beanProp, bean);

                    if (keyPropertyName != null) {
                        String val = beanProp.getProperty(keyPropertyName);
                        if (val != null) {
                            txnId = val;  // change the key for DbOp
                        } else
                             Logger.getLogger(loggerName).log(Level.INFO, "value of [{0}] is null", keyPropertyName);
                    } else 
                         Logger.getLogger(loggerName).log(Level.INFO,"keyPropertyName is null");

                    beanStore.insert(txnId, bean);
                } catch (Exception ex) {
                    Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
                }
            } else
                Logger.getLogger(loggerName).log(Level.INFO,"Db2ObjMapper: Insert() beanName is null");
        }
    }

    @Override
    public void delete(String txnId, Object txn) {
          if (!(txn instanceof TxnEvent)) {
            throw new IllegalArgumentException("Expecting Properties instance");
        }
        Properties rec = ((TxnEvent)txn).getTxnVal();
        if (beanStore == null) {
            Logger.getLogger(loggerName).log(Level.INFO,"Db2ObjMapper: Delete(): beanStore not specified\n");
            ((Properties) rec).list(System.out);
        } else {
            if (beanName != null) {
                try {
                    Object bean = Class.forName(beanName).newInstance();
                    Properties beanProp = columnToBean((Properties) rec);
                    beanSetter.assignValues(beanProp, bean);
                    if (keyPropertyName != null) {
                        String val = beanProp.getProperty(keyPropertyName);
                        if (val != null) {
                            txnId = val;  // change the key for DbOp
                        }
                    }
                    beanStore.delete(txnId, bean);
                } catch (Exception ex) {
                    Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
                }
            }
        }    }

    @Override
    public void update(String txnId, Object txn) {
         if (!(txn instanceof TxnEvent)) {
            throw new IllegalArgumentException("Expecting Properties instance");
        }
        Properties rec = ((TxnEvent)txn).getTxnVal();
        if (beanStore == null) {
            Logger.getLogger(loggerName).log(Level.INFO,"Db2ObjMapper: Update(): beanStore not specified\n");
            ((Properties) rec).list(System.out);
        } else {
            if (beanName != null) {
                try {
                    Object bean = Class.forName(beanName).newInstance();
                    Properties beanProp = columnToBean((Properties) rec);
                    beanSetter.assignValues(beanProp, bean);
                    if (keyPropertyName != null) {
                        String val = beanProp.getProperty(keyPropertyName);
                        if (val != null) {
                            txnId = val;  // change the key for DbOp
                        }
                    }
                    beanStore.update(txnId, bean);
                } catch (Exception ex) {
                    Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
                }
            }
        }    }

    
    /*
     Transform the column name to Bean Name and add corresponding column value
     */
    protected Properties columnToBean(Properties in) {
        Properties out = new Properties();
        Enumeration<?> e = col2beanMap.propertyNames();
        
        while(e.hasMoreElements()) {
            String dbColName = (String)e.nextElement();
            String val = in.getProperty(dbColName);
            String beanMethodName = col2beanMap.getProperty(dbColName);                
            Logger.getLogger(loggerName).log(Level.FINEST, "dbColName[{0}], val[{1}], beanMethodName[{2}]\n", new Object[]{dbColName, val, beanMethodName});
            if( beanMethodName != null && val != null)
            out.setProperty(beanMethodName, val); 
        }
        
        return out;
    }
    /***
    protected Properties columnToBean(Properties in) {
        Properties out = new Properties();
        Iterator<Tuple> iter = map.iterator();
        while (iter.hasNext()) {
            Tuple t = iter.next();
            String val = in.getProperty(t.getKey());
            out.put(t.getVal(), val);
        }

        return out;
    }
    ********/
}
