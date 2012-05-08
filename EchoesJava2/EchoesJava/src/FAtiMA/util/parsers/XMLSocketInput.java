/** 
 * XMLSocketInput.java - Parses xml from a socket connection
 *  
 * Copyright (C) 2006 GAIPS/INESC-ID 
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Company: GAIPS/INESC-ID
 * Project: FAtiMA
 * Created: 09/02/2001 
 * @author: Rui Prada
 * Email to: rui.prada@tagus.ist.utl.pt
 * 
 * History: 
 * Rui Prada: 09/02/2001 - File created
 */

package FAtiMA.util.parsers;

import java.io.ByteArrayInputStream;
import java.net.Socket;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author  rffp
 * @version
 */
public class XMLSocketInput extends SocketListener {

    org.xml.sax.ContentHandler handler;
    //com.sun.xml.parser.Parser parser;
    XMLReader reader;
    

    /** Creates new XMLDatagramInput */
    public XMLSocketInput(Socket socket) {
        super(socket);
        try {
        	SAXParserFactory factory = SAXParserFactory.newInstance();
        	SAXParser saxParser = factory.newSAXParser();
        
        	reader = saxParser.getXMLReader();

        	//parser = com.sun.xml.parser.Parser();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public org.xml.sax.ContentHandler getHandler() {
        return handler;
    }

    public void processMessage(String str) {
        while(str.length()>0 && !str.equals("\n")) {
            // get the first tag
            int i1 = str.indexOf('>');
            String head_tag = str.substring(1, i1);
//            System.out.println("TAG: " + head_tag);
            int i3 = head_tag.indexOf(' ');
            if(i3 != -1) {
                head_tag = head_tag.substring(0, i3);
//                System.out.println("TAG: " + head_tag);
            }
            int i2 = str.indexOf(head_tag,i1);
            String msg_str = str.substring(0, i2 + head_tag.length() + 1);
//            System.out.println("MSG: " + msg_str);
            try {
                InputSource inputSource = new InputSource(new ByteArrayInputStream(msg_str.getBytes()));
                reader.parse(inputSource);
                //parser.parse(inputSource);
            } catch (java.io.IOException e) {
                //e.printStackTrace();

            } catch (org.xml.sax.SAXException e) {
                //e.printStackTrace();
            }
            str = str.substring(i2 + head_tag.length() + 1);
            System.out.println("STR: " + str);
        }
    }

    public void setHandler(DefaultHandler handler) {
        this.handler = handler;
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        
    }
}