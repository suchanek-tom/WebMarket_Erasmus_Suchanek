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
        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser_request.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Template error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoryId = Integer.parseInt(request.getParameter("category_id"));
        String notes = request.getParameter("notes");

        HttpSession session = request.getSession();
        int purchaserId = (int) session.getAttribute("user_id");

        PurchaseRequest pr = new PurchaseRequest();
        pr.setCategoryId(categoryId);
        pr.setPurchaserId(purchaserId);
        pr.setNotes(notes);
        pr.setStatus("pending");

        requestDAO.insert(pr);

        response.sendRedirect("dashboard");
    }
}