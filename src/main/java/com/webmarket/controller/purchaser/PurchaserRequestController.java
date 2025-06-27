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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/purchaser/request")
public class PurchaserRequestController extends HttpServlet {
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Category> categories = categoryDAO.getAllCategories();

        if (categories == null || categories.isEmpty()) {
            // Pokud není žádná kategorie, přidej alespoň info do requestu
            request.setAttribute("error", "No categories found. Please add categories first.");
        }

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser/purchaser_request.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            // Loguj chybu a přesměruj nebo zobraz chybu
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Template processing failed.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Bezpečně získat parametry a ošetřit možné chyby
        String catIdParam = request.getParameter("category_id");
        String notes = request.getParameter("notes");

        if (catIdParam == null || catIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Category ID is required.");
            return;
        }

        int categoryId;
        try {
            categoryId = Integer.parseInt(catIdParam);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Category ID.");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }
        int purchaserId = (int) session.getAttribute("user_id");

        PurchaseRequest pr = new PurchaseRequest();
        pr.setCategoryId(categoryId);
        pr.setPurchaserId(purchaserId);
        pr.setNotes(notes != null ? notes.trim() : "");
        pr.setStatus("pending");

        requestDAO.insert(pr);

        // Přesměruj na dashboard po vložení
        response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
    }
}
