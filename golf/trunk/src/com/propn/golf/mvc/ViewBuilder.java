/**
 * 
 */
package com.propn.golf.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.propn.golf.tools.JsonUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class ViewBuilder {

    public static void build(HttpServletRequest request, HttpServletResponse response, String viewType, Object rst)
            throws IOException, ServletException {
        // download view
        String contentType = response.getContentType();
        if (null != contentType && contentType.equalsIgnoreCase("application/x-msdownload")) {
            return;
        }
        response.setCharacterEncoding("UTF-8");
        // error view
        if (rst instanceof Throwable) {
            Throwable e = (Throwable) rst;
            PrintWriter out = response.getWriter();
            response.setStatus(500);
            out.print("Message:" + e.getMessage());
            out.println("");
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            out.print("StackTrace:" + stringWriter.toString());
            out.flush();
            out.close();
            return;
        }
        // jsp view
        if (rst instanceof String && ((String) rst).endsWith(".jsp")) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.include(request, response);
            // out.close();
            return;
        }
        // application/json
        if (viewType.equals(MediaType.APPLICATION_JSON)) {
            response.setContentType(MediaType.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.append(JsonUtils.toJson(rst));
            out.flush();
            out.close();
            return;
        }
        // application/xml
        if (viewType.equals(MediaType.APPLICATION_XML)) {
            PrintWriter out = response.getWriter();
            out.append(JsonUtils.toJson(rst));
            out.flush();
            out.close();
            return;
        }
    }
}
