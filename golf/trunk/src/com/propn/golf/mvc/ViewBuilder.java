/**
 * 
 */
package com.propn.golf.mvc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.propn.golf.tools.JsonUtils;

/**
 * @author Thunder.Hsu
 * 
 */
public class ViewBuilder {

    public static void build(HttpServletRequest request, HttpServletResponse response, String viewType, Object rst)
            throws IOException, ServletException {

        // error view
        if (rst instanceof Throwable) {
            PrintWriter out = response.getWriter();
            Throwable e = (Throwable) rst;
            response.setStatus(500);
            out.append(e.getMessage());
            out.append(JsonUtils.toJson(e));
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

        // download view
        String contentType = response.getContentType();
        if (null != contentType && contentType.equalsIgnoreCase("application/x-msdownload")) {
            return;
        }

        switch (Views.valueOf(viewType)) {
        case json:
            PrintWriter out = response.getWriter();
            out.append(JsonUtils.toJson(rst));
            out.flush();
            out.close();
            break;
        case xml:
            break;
        default:
            throw new ServletException("视图" + viewType + "不支持");
        }
    }
}
