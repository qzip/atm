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


package in.innomon.event;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Review concurrency and syncro design
 * @author ashish
 */
public class EventCentral implements Runnable {
    private static ArrayList<EventListner> listners = new ArrayList<EventListner>();
    // TODO: Review the design
    public static int maxEventThreads = 4;
    public static int maxShutDownWaitSec = 1;
    
    public static ExecutorService execServ = null;
 
    public  static  Logger logger = Logger.getLogger("UPAY");


     
    private Event event;
    private EventCentral(Event event) {
       this.event = event;
    }
    
    public synchronized static void publish(Event evt) {
       // System.out.println("EventCentral.Pub enter");
        if(!listners.isEmpty()) {
            if(execServ == null) {
                synchronized(EventCentral.class) {
                execServ = Executors.newFixedThreadPool(maxEventThreads);
                }
            }    
            execServ.submit(new EventCentral(evt));
        }
        if( evt instanceof ExitEvent) 
            shutdown();
    }
    private synchronized static void shutdown() {
         if(execServ != null) {
             try {
                 execServ.shutdown();
                 execServ.awaitTermination(maxShutDownWaitSec, TimeUnit.SECONDS);
             } catch (InterruptedException ex) {
                 // do nothing
             }
         }
    }
    public synchronized static void subscribe(EventListner listner) {
        listners.add(listner);
    }
    // TODO: Fix concorrent exception issues, trying to remove when some events are still pending execution.
    public synchronized static void unsubscribe(EventListner listner) {
        listners.remove(listner);
    }
     public synchronized static void runsync(EventCentral inst) {
        ListIterator<EventListner> iter = listners.listIterator(); // issue with thread safety
        while(iter.hasNext()) {
            EventListner listner = iter.next();
            inst.doEvent(listner);
        }
     }
     private void doEvent(EventListner listner) {
         if(listner.isEventOfInterest(event)) {
                listner.onEvent(event);
         }
            else
                  logger.log(Level.FINEST, "EventCentral: Event [{0}] refused by [{1}]", new Object[]{event.getClass().getName(),listner.getClass().getName()});
     }
    @Override
    public void run() {
        runsync(this);// issue with thread safety
    }
}
