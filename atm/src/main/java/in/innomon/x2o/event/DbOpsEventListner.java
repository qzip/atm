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
import in.innomon.util.DbOps;
import in.innomon.util.LifeCycle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class DbOpsEventListner implements EventListner, LifeCycle {

    private String tableName = "";
    private DbOps dbOps = null;
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void setLoggerName(String loggerName) {
        log = Logger.getLogger(loggerName);
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public DbOps getDbOps() {
        return dbOps;
    }

    public void setDbOps(DbOps dbOps) {
        this.dbOps = dbOps;
    }

    @Override
    public boolean isEventOfInterest(Event evt) {
       boolean ret =  ((evt instanceof TxnEvent) && ((TxnEvent) evt).getDataType().equalsIgnoreCase(tableName));
       log.log(Level.FINEST, "isEventOfInterest {0}, Event Class [{1}], Table Name [{2}] ",  new Object[]{ret, evt.getClass().getName(), ((TxnEvent) evt).getDataType()});
       return ret;
    }

       @Override
    public void onEvent(Event evt) {
        log.log(Level.FINEST, "On Event: enter {0}", this.getClass().getName());
        if (evt instanceof InsertEvent) {
            onInsert((InsertEvent) evt);
        } else if (evt instanceof UpdateEvent) {
            onUpdate((UpdateEvent) evt);
        } else if (evt instanceof DeleteEvent) {
            onDelete((DeleteEvent) evt);
        } else 
            System.out.println("On Event: None Match");
    }
    public void onInsert(InsertEvent evt) {
        log.log(Level.FINEST, "On Event: onInsert {0}\n **txnId [{1}]", new Object[]{this.getClass().getName(), evt.getTxnId()});
        dbOps.insert(evt.getTxnId(), evt);
    }

    public void onUpdate(UpdateEvent evt) {
        dbOps.update(evt.getTxnId(), evt);
    }

    public void onDelete(DeleteEvent evt) {
        dbOps.delete(evt.getTxnId(), evt);
    }

    /**
     *
     */
    @Override
   public void start() {
        EventCentral.subscribe(this);
       log.log(Level.FINEST, "Subscrined{0}", this.getClass().getName());
    }

    /**
     *
     */
    @Override
    public void stop() {
        EventCentral.unsubscribe(this);
    }
}
