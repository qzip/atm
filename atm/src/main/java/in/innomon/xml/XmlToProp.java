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

package in.innomon.xml;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;

/**
 *  TODO: Verify thread safety
 * @author Ashish Banerjee 
 */
public class XmlToProp   {
 
transient ArrayList<ElementToKey> e2kArr = new ArrayList<ElementToKey>();

    protected String loggerName = "UPAY";

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
  
    public void setElementToKey(ElementToKey e2k) {
        e2kArr.add(e2k);
    }
    public Properties parse(String xmlFileName) throws XMLStreamException {
        try {
            RandomAccessFile raf = new RandomAccessFile(xmlFileName, "r");
            byte[] arr = new byte[(int) raf.length()];
            raf.readFully(arr);
            raf.close();
            return parse(arr);
        } catch (IOException | XMLStreamException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
            throw new XMLStreamException(ex);
        }
    }
    public Properties parse(byte[] xmlMsgAsBytes) throws XMLStreamException {
        ByteArrayInputStream bai = new ByteArrayInputStream(xmlMsgAsBytes);
        return parse(bai);
    }   
 
    public Properties parse(InputStream xmlInStream) throws XMLStreamException {
       Properties fvmap = new Properties();
        
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader =
                inputFactory.createXMLEventReader(xmlInStream);

        String fldVal = null;
        while (reader.hasNext()) {
            XMLEvent e = reader.nextEvent();
            // insert your processing here
            int evtNo = e.getEventType();
            String  fldName, ns;
            switch (evtNo) {
                case XMLStreamConstants.CHARACTERS:
                    fldVal = ((Characters) e).getData();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    ns = ((EndElement) e).getName().getNamespaceURI();
                    fldName = ((EndElement) e).getName().getLocalPart();
                    
                    if(fldVal != null) {
                        Iterator<ElementToKey> iter = e2kArr.iterator();
                        while(iter.hasNext()) {
                            ElementToKey e2k = iter.next();
                            String key = e2k.getKey(fldName,ns);
                            if(key != null)
                                fvmap.setProperty(key, fldVal);
                    }
                    }
                    fldVal = null;
                    break;
            }

        } // while

 
        return fvmap;
    }
}