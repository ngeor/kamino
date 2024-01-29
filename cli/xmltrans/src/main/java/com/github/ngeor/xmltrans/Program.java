package com.github.ngeor.xmltrans;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.File;

/**
 * Main program class.
 */
public final class Program {

    private static final int EXPECTED_PARAMETER_COUNT = 3;

    private Program() {

    }

    /**
     * Converts an XML file using an XSLT file.
     */
    public static void main(String[] params) throws TransformerException {
        if (params == null || params.length != EXPECTED_PARAMETER_COUNT) {
            System.out.println("Usage: java -jar xmltrans.jar xmlsource.xml xslsource.xsl outputfile\n");
        } else {
            Transformer t = TransformerFactory.newInstance().newTransformer(
                    new StreamSource(new File(params[1])));
            Source s = new StreamSource(new File(params[0]));
            Result r = new StreamResult(new File(params[2]));
            t.transform(s, r);
        }
    }
}
