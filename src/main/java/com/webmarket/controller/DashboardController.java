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

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Kontrola přihlášení
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login?error=true");
            return;
        }

        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");

        // Připravíme data pro šablonu
        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("role", role);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker error in dashboard", e);
        }
    }
}
