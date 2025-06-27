package com.webmarket.controller.purchaser;

import com.webmarket.dao.CategoryDAO;
import com.webmarket.dao.FeatureDAO;
import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.Category;
import com.webmarket.model.Feature;
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
    private final FeatureDAO featureDAO = new FeatureDAO();
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
        List<Feature> features = featureDAO.findAll();

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser/purchaser_request.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        data.put("features", features);
        data.put("username", session.getAttribute("username"));

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Template rendering failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        int purchaserId = (int) session.getAttribute("user_id");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String notes = request.getParameter("notes");

        PurchaseRequest req = new PurchaseRequest();
        req.setPurchaserId(purchaserId);
        req.setCategoryId(categoryId);
        req.setNotes(notes);
        req.setStatus("pending");

        requestDAO.insert(req);

        session.setAttribute("message", "Purchase request submitted successfully.");
        response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
    }
}
