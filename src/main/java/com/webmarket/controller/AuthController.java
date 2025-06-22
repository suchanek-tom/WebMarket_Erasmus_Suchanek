package com.webmarket.controller;

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

@WebServlet("/login")
public class AuthController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("login.ftl.html");

        Map<String, Object> data = new HashMap<>();
        if (request.getParameter("error") != null) {
            data.put("error", "Invalid username or password!");
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userDAO.findByUsernameAndPassword(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user_id", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            // Role-based redirect
            switch (user.getRole()) {
                case "admin":
                    response.sendRedirect("admin/dashboard");
                    break;
                case "technician":
                    response.sendRedirect("technician/dashboard");
                    break;
                case "purchaser":
                    response.sendRedirect("purchaser/dashboard");
                    break;
                default:
                    response.sendRedirect("login?error=true");
            }
        } else {
            response.sendRedirect("login?error=true");
        }
    }
}
