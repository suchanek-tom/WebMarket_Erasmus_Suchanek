package com.webmarket.controller.purchaser;

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

@WebServlet("/purchaser/dashboard")
public class PurchaserDashboardController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Ověř, že uživatel je přihlášen
        if (session.getAttribute("user_id") == null || session.getAttribute("username") == null) {
            response.sendRedirect("/WebMarket/login");
            return;
        }

        int purchaserId = (int) session.getAttribute("user_id");
        String username = (String) session.getAttribute("username");

        List<PurchaseRequest> requests = requestDAO.findByPurchaserId(purchaserId);

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser_dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requests", requests);
        data.put("username", username);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker error", e);
        }
    }
}
