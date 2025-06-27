package com.webmarket.controller.admin;

import com.webmarket.dao.UserDAO;
import com.webmarket.model.User;
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

@WebServlet("/admin/register")
public class AdminRegisterController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("admin_register.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("username", session.getAttribute("username"));

        String message = (String) session.getAttribute("message");
        if (message != null) {
            data.put("message", message);
            session.removeAttribute("message");
        }

        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Template error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (username == null || password == null || role == null) {
            session.setAttribute("message", "Missing input values.");
            response.sendRedirect(request.getContextPath() + "/admin/register");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // v reálné aplikaci: HASH!
        user.setRole(role);

        boolean inserted = userDAO.insertUser(user);
        if (inserted) {
            session.setAttribute("message", "User registered successfully.");
        } else {
            session.setAttribute("message", "Failed to register user.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/register");
    }
}

