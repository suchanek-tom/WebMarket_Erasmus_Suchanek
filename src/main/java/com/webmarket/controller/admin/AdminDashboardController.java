package com.webmarket.controller.admin;

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

@WebServlet("/admin/dashboard")
public class AdminDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = (String) request.getSession().getAttribute("role");
        if (!"admin".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("admin/admin-dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("username", request.getSession().getAttribute("username"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker error", e);
        }
    }
}
