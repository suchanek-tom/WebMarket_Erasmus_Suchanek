package com.webmarket.controller.technician;

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

@WebServlet("/technician/dashboard")
public class TechnicianDashboardController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<PurchaseRequest> pendingRequests = requestDAO.findAllPendingWithCategoryName();

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("technician_dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requests", pendingRequests);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker template error", e);
        }
    }
}
