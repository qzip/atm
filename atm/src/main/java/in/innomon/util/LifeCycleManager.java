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

import in.innomon.event.Event;
import in.innomon.event.EventCentral;
import in.innomon.event.EventListner;
import in.innomon.event.ExitEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Ashish Banerjee
 */
public class LifeCycleManager implements Runnable, LifeCycle, EventListner {

    protected ArrayList<LifeCycle> lives = new ArrayList<LifeCycle>();

    protected boolean shutDown = false;
    protected long snooze = 10000; // 10 seconds
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    public void setLifeCycle(LifeCycle obj) {
        lives.add(obj);
    }

    @Override
    public void run() {
        EventCentral.subscribe(this);
        start();
        
        while (!shutDown) {
            try {
                Thread.sleep(snooze);
            } catch (InterruptedException e) {
            }
        }
        EventCentral.unsubscribe(this);
        stop();
    }

    @Override
    public void start() {
        int siz = lives.size();
        for (int i=0; i < siz; i++) 
            lives.get(i).start();
        
    }

    @Override
    public void stop() {
       int siz = lives.size();
        for (int i=0; i < siz; i++) 
            lives.get(i).stop();
        
    }

    @Override
    public boolean isEventOfInterest(Event evt) {        
        return (evt instanceof ExitEvent);
    }

    @Override
    public void onEvent(Event evt) {
        if(evt instanceof ExitEvent)
            shutDown = true;
    }

}
