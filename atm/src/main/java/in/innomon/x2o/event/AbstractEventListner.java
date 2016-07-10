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

/**
 *
 * @author ashish
 */
public abstract class AbstractEventListner implements EventListner, LifeCycle {

    @Override
    public boolean isEventOfInterest(Event evt) {
        //System.out.println("Event of Interst: enter");
       // return (evt instanceof TxnEvent);
         boolean ret = (evt instanceof TxnEvent);
        // System.out.println("Event of Interst: "+this.getClass().getName()+": "+ret);
         //return ret;
         return true;
    }

    @Override
    public void onEvent(Event evt) {
         //System.out.println("On Event: enter "+this.getClass().getName());
        if (evt instanceof InsertEvent) {
            onInsert((InsertEvent) evt);
        } else if (evt instanceof UpdateEvent) {
            onUpdate((UpdateEvent) evt);
        } else if (evt instanceof DeleteEvent) {
            onDelete((DeleteEvent) evt);
        } /*else 
            System.out.println("On Event: None Match");*/
    }

    public abstract void onInsert(InsertEvent evt);

    public abstract void onUpdate(UpdateEvent evt);

    public abstract void onDelete(DeleteEvent evt);

    public void start() {
        EventCentral.subscribe(this);
        //System.out.println("Subscrined"+this.getClass().getName());
    }

    public void stop() {
        EventCentral.unsubscribe(this);
    }

}
