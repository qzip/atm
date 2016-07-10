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

import com.sun.net.httpserver.HttpExchange;
import java.io.PrintStream;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author ashish
 */
public class DumpHttpExchange {

   

    DumpHttpExchange() {
    }

    public void dump(HttpExchange hx) throws IOException {
        Headers hdr =  hx.getResponseHeaders();
        hdr.add("Content-type", "application/json");
        hx.sendResponseHeaders (200, 0);
        OutputStream out =  hx.getResponseBody();
        PrintStream ps = new PrintStream(out);
        
        Gson gson = new Gson();
        ps.println("{");
        ps.print("\"RemoteAddress\": ");
        ps.print(gson.toJson(hx.getRemoteAddress()));
        ps.println(","); // end Remote Addess

        ps.print("\"RequestURI\": ");
        ps.print(gson.toJson(hx.getRequestURI()));
        ps.println(",");

        ps.print("\"RequestHeaders\": ");
        ps.print(gson.toJson(hx.getRequestHeaders()));
        ps.println("");

        ps.print("\"RequestMethod\": ");
        ps.print(gson.toJson(hx.getRequestMethod()));
        ps.println("");

        // Stack Trace
        try {
            throw new Exception("Inspecting Stack");
        } catch (Exception ex) {
            ps.print("\"StackTrace\": ");
            ex.printStackTrace(ps);
            ps.println("");
        }

        ps.println("}");
        ps.flush();
        ps.close();

       

    }

    
}
