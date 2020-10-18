/*
 * AddNodeServlet.java
 *
 * Created on July 6, 2004, 4:39 PM
 */

package org.jcms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jcms.dbfactory.Configuration;
import org.jcms.dbfactory.NodeFactory;
import org.jcms.model.Node;
import org.jcms.model.NodeType;

/**
 * @author ngeor
 */
public class AddNodeServlet extends HttpServlet {

    /**
     * Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

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
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        Configuration conf = Configuration.getInstance();
        NodeType nodeType = conf.nodeType(request.getParameter("type"));
        if (nodeType == null) {
            throw new ServletException("Cannot find node type");
        }

        String title = request.getParameter("title");

        NodeFactory nodeFactory = new NodeFactory();
        try {
            Node parent = nodeFactory.selectOne(Integer.parseInt(request.getParameter("parent")));
            if (parent == null) {
                throw new ServletException("Cannot find parent");
            }

            // create node
            Node child = nodeType.newInstance();

            // set properties
            child.setTitle(title);

            if (!nodeFactory.insert(parent, child)) {
                throw new ServletException("Not allowed to place node here");
            }

        } catch (java.sql.SQLException ex) {
            throw new ServletException(ex);
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>SUCCESS</h1>");
        out.println("<p><a href=\"index.jsp\">Return</a></p>");
        out.println("</body>");
        out.println("</html>");
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

}
