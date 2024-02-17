package net.ngeor.icqfriends;

import java.io.File;
import java.io.StringReader;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.*;

/**
 * Utility class for manipulating XML files.
 */
public final class Utility {
    private Utility() {}

    private static Document getDocument(String szFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new File(szFile));
    }

    private static Node getNodeByUIN(Document doc, String uin) throws Exception {
        NodeList list = doc.getElementsByTagName("icqfriend");
        if (list == null || list.getLength() <= 0) {
            return null;
        }

        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            NamedNodeMap nnm = n.getAttributes();
            if (nnm != null) {
                Node attr = nnm.getNamedItem("uin");
                if (uin.equals(attr.getNodeValue())) {
                    return n;
                }
            }
        }

        return null;
    }

    /**
     * Gets the XML of one friend.
     * @param szFile The data file.
     * @param uin The UIN of the friend.
     * @return The XML representation.
     */
    public static String getICQFriendXML(String szFile, String uin) throws Exception {
        String xmlOut = null;
        Document doc = getDocument(szFile);
        Node n = getNodeByUIN(doc, uin);
        if (n != null) {
            NamedNodeMap nnm = n.getAttributes();
            xmlOut = "<icqfriend uin=\"";
            xmlOut += uin;
            xmlOut += "\" nickname=\"";
            xmlOut += nnm.getNamedItem("nickname").getNodeValue();
            xmlOut += "\"/>";
        }
        return xmlOut;
    }

    private static void writeDocument(String szFile, Document doc) throws Exception {
        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        File outFile = new File(szFile);
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(outFile);
        transformer.transform(source, result);
    }

    /**
     * Updates the UIN.
     * @param szFile
     * @param originaluin
     * @param uin
     * @param nickname
     * @return
     * @throws Exception
     */
    public static boolean updateUIN(String szFile, String originaluin, String uin, String nickname) throws Exception {
        Document doc = getDocument(szFile);
        Node n = getNodeByUIN(doc, originaluin);
        if (n == null) {
            return false;
        }

        NamedNodeMap nnm = n.getAttributes();
        nnm.getNamedItem("uin").setNodeValue(uin);
        nnm.getNamedItem("nickname").setNodeValue(nickname);
        writeDocument(szFile, doc);
        return true;
    }

    /**
     * Adds a new UIN.
     * @param szFile
     * @param uin
     * @param nickname
     * @throws Exception
     */
    public static void addUIN(String szFile, String uin, String nickname) throws Exception {
        Document doc = getDocument(szFile);
        Element eNew = doc.createElement("icqfriend");
        eNew.setAttribute("nickname", nickname);
        eNew.setAttribute("uin", uin);
        doc.getDocumentElement().appendChild(eNew);
        writeDocument(szFile, doc);
    }

    /**
     * Deletes a UIN.
     * @param szFile
     * @param uin
     * @throws Exception
     */
    public static void deleteUIN(String szFile, String uin) throws Exception {
        Document doc = getDocument(szFile);
        Node n = getNodeByUIN(doc, uin);
        doc.getDocumentElement().removeChild(n);
        writeDocument(szFile, doc);
    }

    /**
     * Applies XSLT transformation.
     * @param source
     * @param dest
     * @param xslFile
     * @throws Exception
     */
    public static void transformString(String source, Writer dest, String xslFile) throws Exception {
        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xslFile)));
        transformer.transform(new StreamSource(new StringReader(source)), new StreamResult(dest));
    }

    /**
     * Applies XSLT transformation.
     * @param source
     * @param dest
     * @param xslFile
     * @throws Exception
     */
    public static void transformFile(String source, Writer dest, String xslFile) throws Exception {
        Transformer t = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(xslFile)));
        Source s = new StreamSource(new File(source));
        Result r = new StreamResult(dest);
        t.transform(s, r);
    }

    public static void sendRedirect(String sURL, String message, String goback, Writer dest, String xslFile)
            throws Exception {
        transformString(
                "<page url=\"" + sURL + "\" message=\"" + message + "\" goback=\"" + goback + "\" />", dest, xslFile);
    }
}
