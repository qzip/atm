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

package in.innomon.srv;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import in.innomon.util.LifeCycle;


public class TelnetSrv implements Runnable, LifeCycle {



    public void run() {
       childs = new Thread[maxListners];
       try {
            servSok = new ServerSocket(port);
            for(int i=0; i < childs.length; i++) {
                childs[i] = new CmdHelper(i);
                childs[i].start();
            }
            log.log(Level.INFO,"INFO: CmdServ Started "+childs.length+" Threads and serving at port "+port);
        }
        catch(Exception x) {
            log.log(Level.SEVERE, null, x);
                    
        }

    }

    private boolean runOnStart = true;
    private int maxListners = 1;
    private int port = 2001;
    private Thread[] childs = null;
    private ServerSocket servSok = null;
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private TelnetLineProcessor processor =null;

    public boolean isRunOnStart() {
        return runOnStart;
    }

    public void setRunOnStart(boolean runOnStart) {
        this.runOnStart = runOnStart;
    }

    public void setLoggerName(String loggerName) {
       log = Logger.getLogger(loggerName);
    }
    public void setMaxListners(int maxListners) {
        this.maxListners = maxListners;
    }

    public void setProcessor(TelnetLineProcessor processor) {
        this.processor = processor;
    }
   

    public void setLog(Logger log) {
        this.log = log;
    }
 
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        if(runOnStart)
            run(); 
    }

    @Override
    public void stop() {
       if( servSok != null) {
           try {
               servSok.close();
           } catch (IOException ex) {
               log.log(Level.SEVERE, null, ex);
           } finally {
               servSok = null;
           }
           
       } 
    }

    public class CmdHelper extends  Thread {
       public CmdHelper(int ndx) {
            this.ndx = ndx;
       }

       @Override
       public void run() {
          
          while(servSok != null && childs[ndx] == this) {
              try {
                 sok = servSok.accept();
                 wrt = new PrintWriter(new OutputStreamWriter(sok.getOutputStream()),true);
                 rdr = new BufferedReader(new InputStreamReader(sok.getInputStream()));

                 process();
               }
               catch(Exception ex) {
                  log.log(Level.SEVERE, null, ex);
               }
               finally {
                  try { if(sok != null) sok.close();}catch(Exception x) {}
                  sok = null;
               }

          }

       }

       protected void process() throws Exception {           
           wrt.print("+OK ("+InetAddress.getLocalHost()+ "Telnet IOT server "+VER+" ready\r\n");
           wrt.flush();
           loop = true;
           if( processor == null) {
                wrt.print("-ERR Config Error No Line Processor Specified\r\n");
                wrt.flush();
                loop = false;
           }
           while(loop) {
               String cmd = rdr.readLine();
               if(cmd != null) {
                  try {                    
                      String ret = processor.process(cmd);
                      if(ret != null) {
                          wrt.print(ret);
                          wrt.flush();
                      }    
                    
                  }  // end of Try...
                  catch(Exception ex) {
                     log.log(Level.SEVERE, null, ex);
                     log.log(Level.WARNING, "WARN: '{'LineProcessor'}' [{0}]", sok.toString());
                     wrt.print("-ERR unrecognized Command: \""+cmd+"\"\r\n");
                     wrt.flush();
                     loop = false;
                  }
               }
               else {
                 loop = false;
                }
           }     // end while loop...


       } // process

 
       protected Socket sok = null;
       protected int ndx = 0;
       protected PrintWriter wrt;
       protected BufferedReader rdr;

       public boolean loop = true;
       public static final String VER = "18-June-2016";

    }  // class CmdHelper

 
}