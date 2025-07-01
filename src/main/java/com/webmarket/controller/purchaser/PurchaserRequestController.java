package com.webmarket.controller.purchaser;

import com.webmarket.dao.CategoryDAO;
import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.Category;
import com.webmarket.model.PurchaseRequest;
import com.webmarket.utils.FreemarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/purchaser/request")
public class PurchaserRequestController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        List<Category> categories = categoryDAO.findAll();

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser/purchaser_request.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);

        response.setContentType("text/html");
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
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        int purchaserId = (int) session.getAttribute("user_id");
        String categoryIdStr = request.getParameter("categoryId");
        String notes = request.getParameter("notes");

        int categoryId;
        try {
            categoryId = Integer.parseInt(categoryIdStr);
        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid category.");
            response.sendRedirect(request.getContextPath() + "/purchaser/request");
            return;
        }

        PurchaseRequest pr = new PurchaseRequest();
        pr.setPurchaserId(purchaserId);
        pr.setCategoryId(categoryId);
        pr.setNotes(notes != null ? notes.trim() : "");
        pr.setStatus("pending");

        boolean inserted = requestDAO.insert(pr);

        if (inserted) {
            session.setAttribute("message", "Request submitted successfully.");
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
        } else {
            session.setAttribute("message", "Failed to submit request.");
            response.sendRedirect(request.getContextPath() + "/purchaser/request");
        }
    }
}
