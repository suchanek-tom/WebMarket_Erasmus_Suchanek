package com.webmarket.controller;

import com.webmarket.utils.FreemarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/error/404")
public class NotFoundController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType("text/html");

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("404.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Page not found");

        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Error rendering 404 page", e);
        }
    }
}
