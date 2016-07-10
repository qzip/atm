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

package in.innomon.srv.helper;

import com.google.gson.Gson;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import in.innomon.srv.HttpReqHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 *
 * @author ashish
 */
public class SysTimeHelper implements HttpReqHelper, HttpHandler{
    private String contextPath = "/";
    private String  mimeType = "application/json";
    private Authenticator auth = null; 
    @Override
    public String getContextPath() {
        return contextPath;
    }    

    @Override
    public HttpHandler getHandler() {
        return this;
    }
    
    /**
       For usage see:
       * http://docs.oracle.com/javase/7/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html
    */
    @Override
    public void handle(HttpExchange hx) throws IOException {
        System.out.println("In Handle Systime");
        Gson gson = new Gson();
        String json = gson.toJson(new Date());
        
        //hx.sendResponseHeaders (200, json.length());
        hx.sendResponseHeaders (200, 0);
        OutputStream out =  hx.getResponseBody();
        out.write(json.getBytes());
        out.close();
        System.out.println(gson.toJson(new Date()));
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public Authenticator getAuthenticator() {
        return auth;
    }
    public void setAuthenticator(Authenticator ath) {
        auth = ath;
    }
}
