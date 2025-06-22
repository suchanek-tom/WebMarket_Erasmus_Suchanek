package com.webmarket.controller.admin;

import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.PurchaseRequest;
import com.webmarket.utils.FreemarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class AdminController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<PurchaseRequest> requests = requestDAO.findAllWithCategoryAndUser();

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("admin_dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requests", requests);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker rendering error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");
        int requestId = Integer.parseInt(request.getParameter("requestId"));

        switch (action) {
            case "approve":
                requestDAO.updateStatus(requestId, "approved");
                break;
            case "reject":
                requestDAO.updateStatus(requestId, "rejected");
                break;
            case "delete":
                requestDAO.deleteById(requestId);
                break;
        }

        response.sendRedirect("dashboard");
    }
}
