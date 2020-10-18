/*
 * AddUserServlet.java
 *
 * Created on July 5, 2004, 8:43 PM
 */

package org.jcms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ngeor
 */
public class AddUserServlet extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        org.jcms.model.User user = new org.jcms.model.User();
        user.setEmail(request.getParameter("email"));
        user.setPassword(request.getParameter("password"));
        user.setLastname(request.getParameter("lastname"));
        user.setFirstname(request.getParameter("firstname"));
        user.setNickname(request.getParameter("nickname"));
        user.setActive("on".equals(request.getParameter("active")));
        org.jcms.dbfactory.UserFactory userFactory = new org.jcms.dbfactory.UserFactory();
        try {
            userFactory.insert(user);
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
