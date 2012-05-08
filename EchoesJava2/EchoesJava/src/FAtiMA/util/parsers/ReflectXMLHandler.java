/** 
 * ReflectXMLHandler.java - Implements a XML Handler with class reflection
 * 
 * Company: GAIPS/INESC-ID
 * Project: FAtiMA
 * Created: 17/01/2004 
 * @author: unspecified
 * Email to: joao.assis@tagus.ist.utl.pt
 * 
 * History: 
 * João Dias: 17/01/2004 - File created
 */
package FAtiMA.util.parsers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class ReflectXMLHandler extends DefaultHandler {
    // used for reflect
    Class[] argTypes = {Attributes.class};
    Class[] charArgTypes = {String.class};
    // used for reflect
    Class cl;
    String lastTag;

    public ReflectXMLHandler() {
        super();
        cl = this.getClass();
    }

    public void callCharMethod(String methodName, String str) {
      Method meth = null;
      try {
        // Fetches the method
        meth = cl.getMethod(methodName, charArgTypes);
        Object args[] = {str};
        // invokes the method
        meth.invoke(this,args);
      }
      catch (java.lang.NoSuchMethodException e) {
        //System.err.println("Unable to handle message! No such method " + methodName + "(" + argTypes + ")");
      }
      catch(InvocationTargetException e) {
        e.printStackTrace();
      }
      catch(IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    public void callEndMethod(String methodName) {
      Method meth = null;
      try {
        // Fetches the method
        meth = cl.getMethod(methodName);
        meth.invoke(this);
      }
      catch (java.lang.NoSuchMethodException e) {
        //System.err.println("Unable to handle message! No such method " + methodName + "(" + argTypes + ")");
      }
      catch(InvocationTargetException e) {
        e.printStackTrace();
      }
      catch(IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    public void callTagMethod(String methodName, Attributes attributes) {
      Method meth = null;
      try {
        // Fetches the method
        meth = cl.getMethod(methodName,argTypes);
        Object args[] = {attributes};
        meth.invoke(this,args);
      }
      catch (java.lang.NoSuchMethodException e) {
        //System.err.println("Unable to handle message! No such method " + methodName + "(" + argTypes + ")");
      }
      catch(InvocationTargetException e) {
        e.printStackTrace();
      }
      catch(IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    
    public void characters(char[] ch, int start, int length) {
        callCharMethod(lastTag + "Characters", new String(ch).substring(start,start+length));
        //System.out.println("start " + start + " lehngth " + length);
        //System.out.println("characters = '" + new String(ch).substring(start,start+length) + "')");
    }

    public void endDocument() {
//        System.out.println("endDocument");
    }
    
    public void endElement(String namespaceURI, String localName, String qName)
    {
    	callEndMethod(qName + "End");
    }

    /* Dealing with errors */
    public void error(SAXParseException e) {
    }

    public void fatalError(SAXParseException e) {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) {
//        System.out.println("start " + start + " length " + length);
//        System.out.println("whitespaces = '" + new String(ch).substring(start,start+length) + "')");
    }

    /* */
    public void notationDecl(java.lang.String name, java.lang.String publicId, java.lang.String systemId) {
    }

    public void processingInstruction(java.lang.String target, java.lang.String data) {
    }

    public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId)  {
//        System.out.println("public " + publicId + " system " + systemId);
        return null;
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() {
//        System.out.println("beginDocument");
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
    	callTagMethod(qName, attributes);
    	lastTag = qName;
    }

    public void unparsedEntityDecl(java.lang.String name, java.lang.String publicId, java.lang.String systemId, java.lang.String notationName) {
    }

    public void warning(SAXParseException e) {
    }
}