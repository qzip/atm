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

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.net.httpserver.HttpHandler;
import java.util.Enumeration;
import java.util.Hashtable;
import in.innomon.util.LifeCycle;

/**
 * For source code of Sun Http Sever : /openjdk/jdk/src/share/classes/sun/
 *
 * @author ashish
 */
public class HttpService implements LifeCycle {

    private long snooze = 1000; // millisecs
    private boolean debug = false;
    private boolean ssl = false;
    private HttpServer srv = null;
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private String hostName = "localhost";
    private int    hostPort = 7070;
    private Hashtable<String,HttpHandler> handlers = new Hashtable<String,HttpHandler>();
    private AuthHandler authHandler = null;
    private Hashtable<String,Authenticator> auths = new Hashtable<String,Authenticator>();
 
    

    public void setLoggerName(String loggerName) {
        log = Logger.getLogger(loggerName);
    }
  
    @Override
      public void start() {
       if (srv == null) {
            try {
                InetSocketAddress sokAddr = new InetSocketAddress(hostName, hostPort);
                if (ssl) {
                    srv = HttpsServer.create();
                } else {
                    srv = HttpServer.create();
                }
                srv.bind(sokAddr, 0);
                setContexts();
                srv.start();
            } catch (IOException ex) {
                log.log(Level.SEVERE, null, ex);
            }

        }          
          
      }
    @Override
      public void stop() {
         if(srv !=null)
             srv.stop(1);
         srv = null;
      }

    public void setHandler(HttpReqHelper helper) {
        registerHandler(helper.getContextPath(), helper.getHandler());
        Authenticator auth = helper.getAuthenticator();
        if(auth != null) {
            registerAuthenticator(helper.getContextPath(),auth);
        }
    }
    public void registerHandler(String path, HttpHandler handler) {
        handlers.put(path,handler);
    }
    public void registerAuthenticator(String path,Authenticator oath) {
        auths.put(path, oath);
    }
    protected void setContexts() {
        if(srv != null) {
            Enumeration<String> keys = handlers.keys();
            while(keys.hasMoreElements()) {
               String path = keys.nextElement();
               HttpContext ctx =  srv.createContext(path, handlers.get(path));
               Authenticator auth = auths.get(path);
               if(auth != null)
                   ctx.setAuthenticator(auth);
               else if(authHandler != null)
                   authHandler.setAuthenticator(ctx);
               
            }
        }
    }

    public long getSnooze() {
        return snooze;
    }

    public void setSnooze(long snooze) {
        this.snooze = snooze;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public HttpServer getSrv() {
        return srv;
    }

   
    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public AuthHandler getAuthHandler() {
        return authHandler;
    }

    public void setAuthHandler(AuthHandler authHandler) {
        this.authHandler = authHandler;
    }

}
