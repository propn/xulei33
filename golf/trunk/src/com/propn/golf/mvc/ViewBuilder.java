/**
 * 
 */
package com.propn.golf.mvc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import com.propn.golf.tools.JsonUtils;
import com.propn.golf.tools.XmlUtils;

/**
 * 
 * 
 * @author Thunder.Hsu
 * @CreateDate 2012-11-17
 */
public class ViewBuilder {

    public static void build(HttpServletRequest request, HttpServletResponse response, String mediaType, Object rst)
            throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        // error view
        if (rst instanceof Throwable) {
            response.setContentType(MediaType.TEXT_PLAIN);
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

        // download view
        if (rst instanceof File) {
            File file = (File) rst;
            response.setContentType("application/x-msdownload");
            String filename = java.net.URLEncoder.encode(file.getName(), "UTF-8");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream buff = new BufferedInputStream(fis);
            byte[] b = new byte[1024];
            long i = 0;
            OutputStream outs = response.getOutputStream();
            while (i < file.length()) {
                int j = buff.read(b, 0, 1024);
                i += j;
                outs.write(b, 0, j);
            }
            outs.flush();
            return;
        }

        // code view
        if (rst instanceof View) {
            View v = (View) rst;
            if (v.getKind().equals(View.jsp)) {
                Map<String, Object> model = v.getModel();
                for (Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                RequestDispatcher dispatcher = request.getRequestDispatcher(v.getPath());
                response.setContentType(MediaType.TEXT_HTML);
                // dispatcher.include(request, response);
                dispatcher.forward(request, response);
            }

            if (v.getKind().equals(View.freeMarker)) {
                Map<String, Object> model = v.getModel();
                for (Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                RequestDispatcher dispatcher = request.getRequestDispatcher(v.getPath());
                response.setContentType(MediaType.TEXT_HTML);
                // dispatcher.include(request, response);
                dispatcher.forward(request, response);
            }
            return;
        }

        // application/json
        if (mediaType.equals(MediaType.APPLICATION_JSON)) {
            response.setContentType(MediaType.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.append(JsonUtils.toJson(rst));
            out.flush();
            out.close();
            return;
        }
        // application/xml
        if (mediaType.equals(MediaType.APPLICATION_XML)) {
            response.setContentType(MediaType.APPLICATION_XML);
            PrintWriter out = response.getWriter();
            try {
                out.append(XmlUtils.toXml(rst));
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
            return;
        }

    }
}
