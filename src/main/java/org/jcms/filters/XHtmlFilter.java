/*
 * XHtmlFilter.java
 *
 * Created on July 12, 2004, 7:49 PM
 */

package org.jcms.filters;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author ngeor
 */
public class XHtmlFilter implements Filter {

    private final boolean debug = false;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig;

    public XHtmlFilter() {
    }

    private void doBeforeProcessing(RequestWrapper request, ResponseWrapper response)
        throws IOException, ServletException {
        if (debug) {
            log("XHtmlFilter:DoBeforeProcessing");
        }
    }

    private void doAfterProcessing(RequestWrapper request, ResponseWrapper response)
        throws IOException, ServletException {
        if (debug) {
            log("XHtmlFilter:DoAfterProcessing");
        }
    }

    /**
     * @param request  The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain    The filter chain we are processing
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
        throws IOException, ServletException {

        if (debug) {
            log("XHtmlFilter:doFilter()");
        }

        request.setCharacterEncoding("utf-8");
        //
        // Create wrappers for the request and response objects.
        // Using these, you can extend the capabilities of the
        // request and response, for example, allow setting parameters
        // on the request before sending the request to the rest of the filter chain,
        // or keep track of the cookies that are set on the response.
        //
        // Caveat: some servers do not handle wrappers very well for forward or
        // include requests.
        //
        RequestWrapper wrappedRequest = new RequestWrapper((HttpServletRequest) request);
        ResponseWrapper wrappedResponse = new ResponseWrapper((HttpServletResponse) response);

        doBeforeProcessing(wrappedRequest, wrappedResponse);

        Throwable problem = null;

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);


            String xmlData = wrappedResponse.getResultString();
            if (wrappedResponse.getContentType().startsWith("text/xml")) {
                String s = ((HttpServletRequest) request).getRequestURI();
                String sContext = ((HttpServletRequest) request).getContextPath();
                int i = s.indexOf("://");
                s = s.substring(i + 2);
                i = s.indexOf(sContext);
                s = s.substring(i + sContext.length());

                i = s.lastIndexOf('/');
                if (i == -1) {
                    throw new ServletException("Invalid path to servlet");
                }

                String sPath = s.substring(0, i + 1);

                String magic = "<?xml-stylesheet href=\""; //<%= rootNode.getType() %>.xsl"
                int j = xmlData.indexOf(magic);

                String sFile = getSFile(xmlData, s, i, magic, j);
                File xslFile = new File(this.getFilterConfig().getServletContext().getRealPath(sPath + sFile));
                renderXML(xmlData, xslFile, (HttpServletResponse) response);
            } else {
                response.getWriter().println(xmlData);
            }

        } catch (IOException | ServletException t) {
            //
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            //
            problem = t;
            t.printStackTrace();
        }

        doAfterProcessing(wrappedRequest, wrappedResponse);


        //
        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        //
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }

            if (problem instanceof IOException) {
                throw (IOException) problem;
            }

            sendProcessingError(problem, response);
        }
    }

    private String getSFile(String xmlData, String s, int i, String magic, int j) {
        String sFile;
        if (j != -1) {
            sFile = xmlData.substring(j + magic.length());
            sFile = sFile.substring(0, sFile.indexOf('"'));
        } else {
            sFile = s.substring(i + 1);
            sFile = sFile.substring(0, sFile.lastIndexOf('.') + 1);
            if (sFile.length() <= 0) {
                sFile = "index.";
            }

            sFile += "xsl";
        }
        return sFile;
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {

        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter.
     */
    public void destroy() {
    }

    /**
     * Init method for this filter.
     */
    public void init(FilterConfig config) {

        this.filterConfig = config;
        if (config != null) {
            if (debug) {
                log("XHtmlFilter: Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    public String toString() {

        if (filterConfig == null) {
            return "XHtmlFilter()";
        }

        return "XHtmlFilter(" + filterConfig + ")";
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {

        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.isEmpty()) {

            try {

                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (IOException ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Gets the stack trace.
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {

        String stackTrace = null;

        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (IOException ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    /**
     * This request wrapper class extends the support class HttpServletRequestWrapper,
     * which implements all the methods in the HttpServletRequest interface, as
     * delegations to the wrapped request.
     * You only need to override the methods that you need to change.
     * You can get access to the wrapped request using the method getRequest()
     */
    class RequestWrapper extends HttpServletRequestWrapper {
        //
        // You might, for example, wish to add a setParameter() method. To do this
        // you must also override the getParameter, getParameterValues, getParameterMap,
        // and getParameterNames methods.
        //
        private Hashtable localParams;

        RequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * Sets a parameter.
         * @param name
         * @param values
         */
        public void setParameter(String name, String[] values) {
            if (debug) {
                System.out.println(
                    "XHtmlFilter::setParameter(" + name + "=" + values + ")" + " localParams = " + localParams);
            }

            if (localParams == null) {
                localParams = new Hashtable();
                //
                // Copy the parameters from the underlying request.
                Map wrappedParams = getRequest().getParameterMap();
                Set keySet = wrappedParams.keySet();
                for (Iterator it = keySet.iterator(); it.hasNext();) {
                    Object key = it.next();
                    Object value = wrappedParams.get(key);
                    localParams.put(key, value);
                }
            }

            localParams.put(name, values);
        }

        @Override
        public String getParameter(String name) {
            if (debug) {
                System.out.println("XHtmlFilter::getParameter(" + name + ") localParams = " + localParams);
            }

            if (localParams == null) {
                return getRequest().getParameter(name);
            }

            Object val = localParams.get(name);
            if (val instanceof String) {
                return (String) val;
            }

            if (val instanceof String[]) {
                String[] values = (String[]) val;
                return values[0];
            }

            return val == null ? null : val.toString();
        }

        @Override
        public String[] getParameterValues(String name) {
            if (debug) {
                System.out.println("XHtmlFilter::getParameterValues(" + name + ") localParams = " + localParams);
            }

            if (localParams == null) {
                return getRequest().getParameterValues(name);
            }

            return (String[]) localParams.get(name);
        }

        @Override
        public Enumeration getParameterNames() {
            if (debug) {
                System.out.println("XHtmlFilter::getParameterNames() localParams = " + localParams);
            }

            if (localParams == null) {
                return getRequest().getParameterNames();
            }

            return localParams.keys();
        }

        @Override
        public Map getParameterMap() {
            if (debug) {
                System.out.println("XHtmlFilter::getParameterMap() localParams = " + localParams);
            }

            if (localParams == null) {
                return getRequest().getParameterMap();
            }

            return localParams;
        }
    }

    /**
     * This response wrapper class extends the support class HttpServletResponseWrapper,
     * which implements all the methods in the HttpServletResponse interface, as
     * delegations to the wrapped response.
     * You only need to override the methods that you need to change.
     * You can get access to the wrapped response using the method getResponse()
     */
    class ResponseWrapper extends HttpServletResponseWrapper {
        private StringWriter sw = new StringWriter();
        private PrintWriter pw = new PrintWriter(sw);

        ResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public PrintWriter getWriter() {
            return pw;
        }

        public String getResultString() {
            return sw.toString();
        }
    }

    private void renderXML(String xmlData, File xslFile, HttpServletResponse response)
        throws ServletException {
        try {
            //Setup Transformer
            Source xsltSrc = new StreamSource(xslFile);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSrc);

            //Make sure the XSL transformation's result is piped through to FOP
            Result res = new StreamResult(response.getWriter());


            //Setup input
            Source src = new StreamSource(new StringReader(xmlData));
            //Prepare response
            response.setContentType("text/html");
            //Start the transformation and rendering process
            transformer.transform(src, res);
        } catch (IOException | TransformerException ex) {
            throw new ServletException(ex);
        }
    }
}
