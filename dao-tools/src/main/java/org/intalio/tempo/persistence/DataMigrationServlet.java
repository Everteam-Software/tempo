package org.intalio.tempo.persistence;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.SystemPropertyUtils;

public class DataMigrationServlet extends HttpServlet {

    private static final long serialVersionUID = -843998942004165103L;
    private static final String FILE_PREFIX = "file:";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter response = resp.getWriter();
        try {
            resp.setContentType("text/html");
            migrationTemplate(req, response);
            response.write("<form ACTION=\"" + req.getRequestURL() + "\" method=\"post\"><input style=\"\" type=\"submit\" value=\"Start Migration\"/></form>");
        } catch (Exception e) {
            response.write("An error has occured");
            response.flush();
            e.printStackTrace(response);
        }
        response.close();
    }

    private JDBC2JPAConverter migrationTemplate(HttpServletRequest req, PrintWriter response) throws IOException, FileNotFoundException {
        response.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + req.getContextPath() + "/intalio.css\"/>");
        response.write("<h1>Tempo Data Migration Tool</h1>");
        response.flush();
        response.write("<img src=\"http://www.alternative-tourism.com/Japan/Mountaineering/Day_walks/Okutama/Kumotori/Fuji_Kumotori_t.jpg\"><br/>");
        response.flush();

        response.write("Searching config file<br/>");
        response.flush();
        String configFile = getServletConfig().getInitParameter("migrationConfigLocation");
        configFile = SystemPropertyUtils.resolvePlaceholders(configFile);
        if (configFile.startsWith(FILE_PREFIX)) {
            configFile = configFile.substring(FILE_PREFIX.length());
        }
        response.write("Found file:" + configFile + "<br/>");
        response.flush();

        Properties props = new Properties();
        props.load(new BufferedInputStream(new FileInputStream(configFile)));
        response.write("Using the following migration properties<br/>");
        response.write("<table>");
        response.flush();
        Iterator iter = props.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            response.write("<tr><td>" + key + "</td><td>" + props.getProperty(key) + "</td></tr>");
            response.flush();
        }
        response.write("</table>");
        response.write("Creating converter<br/>");
        response.flush();
        try {
            JDBC2JPAConverter converter = new JDBC2JPAConverter(props);
            return converter;
        } catch (Exception e) {
            response.write("<div class=\"error\">Could not instanciate the converter. Make sure all the drivers are accessible to the servlet<div/>");
            response.flush();
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter response = resp.getWriter();
        JDBC2JPAConverter converter = migrationTemplate(req, response);
        if (converter == null) {
            response.write("Stopping..<br/>");
            return;
        }
        try {
            response.write("Copying tasks<br/>");
            response.flush();
            List<String> tasks = converter.findAllTasks();
            response.write("<table class=\"tasks\">");
            for (String task : tasks) {
                response.write("<tr><td>" + task + "</td>");
                try {
                    converter.copyTask(task);
                    response.write("<td class=\"success\"/>");
                } catch (Exception e) {
                    response.write("<td class=\"failure\"/>");
                }
                response.flush();
            }
            response.write("</table>");
            
            
            response.write("Copying items<br/>");
            response.write("<table class=\"items\">");
            response.flush();
            List<String> items = converter.findAllItems();
            for (String item : items) {
                response.write("<tr><td>" + item + "</td>");
                try {
                    converter.copyItem(item);
                    response.write("<td class=\"success\"/>");
                } catch (Exception e) {
                    response.write("<td class=\"failure\"/>");
                }
                response.flush();
            }
            response.write("</table>");
            
            response.write("Migration finished<br/>");
            response.flush();
        } catch (Exception e) {
            response.write("An error has occured");
            response.flush();
            e.printStackTrace(response);
        }
        response.close();
    }

}
