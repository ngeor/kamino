/*
 * ConfigurationServlet.java
 *
 * Created on July 12, 2004, 12:34 PM
 */

package org.jcms.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jcms.dbfactory.Configuration;
import org.jcms.dbfactory.NodeTypeRelation;
import org.jcms.model.NodeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author ngeor
 */
public class ConfigurationServlet extends HttpServlet {

    /**
     * Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String modelFile = config.getInitParameter("modelFile");
        File f = new File(getServletContext().getRealPath(modelFile));
        try {
            initModel(f);
        } catch (IOException | ServletException | ParserConfigurationException | SAXException ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {

    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        out.println("<root>");
        out.println("<nodetypes>");
        Configuration conf = Configuration.getInstance();
        Enumeration enumeration = conf.nodeTypeNames();
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            NodeType nt = conf.nodeType(s);
            out.println("<nodetype>");
            out.println("<name>" + s + "</name>");
            out.println("<id>" + nt.getType() + "</id>");
            out.println("<class>" + nt.getClassname() + "</class>");
            out.println("</nodetype>");
        }
        out.println("</nodetypes>");
        out.println("</root>");

        out.close();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }

    private void parseNode(Configuration conf, NodeType parentNodeType, Node parentNode) throws ServletException {
        for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!(node instanceof Element)) {
                continue;
            }

            if (!"node".equals(node.getNodeName())) {
                throw new ServletException("Invalid XML File - expecting <node> found " + node.getNodeName());
            }

            NamedNodeMap nnm = node.getAttributes();

            // node type name
            String name = nnm.getNamedItem("name").getNodeValue();

            // cardinality in parent type
            String strCardinality = nnm.getNamedItem("cardinality").getNodeValue();
            int cardinality;
            if ("*".equals(strCardinality)) {
                cardinality = NodeTypeRelation.ZERO_OR_MORE;
            } else {
                cardinality = Integer.parseInt(strCardinality);
            }

            // if it is a new type we must provide id and classname
            String classname;
            int id;

            try {
                id = Integer.parseInt(nnm.getNamedItem("id").getNodeValue());
                classname = nnm.getNamedItem("classname").getNodeValue();
            } catch (NumberFormatException ex) {
                id = 0;
                classname = null;
            }

            if (id > 0) {
                /* add new nodetype */
                conf.addNodeType(new NodeType(id, classname, name));
            }

            conf.addNodeTypeRelation(parentNodeType.getName(), name, cardinality);
            parseNode(conf, conf.nodeType(name), node);
        }
    }

    /**
     * Parse the site's custom data model.
     */
    private void initModel(File f) throws ServletException, IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(f);
        Element element = doc.getDocumentElement();
        if (!"model".equals(element.getTagName())) {
            throw new ServletException("Invalid XML File - expecting <model>");
        }

        Configuration conf = Configuration.getInstance();
        conf.reset();
        parseNode(conf, conf.nodeType("root"), element);
    }
}
