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
import java.util.*;

@WebServlet("/purchaser/close")
public class PurchaserRequestController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String reqIdStr = request.getParameter("requestId");
        if (reqIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(reqIdStr);
        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid request ID.");
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
            return;
        }

        PurchaseRequest pr = requestDAO.findById(requestId);
        int purchaserId = (int) session.getAttribute("user_id");

        if (pr == null || pr.getPurchaserId() != purchaserId || !"ordered".equals(pr.getStatus())) {
            session.setAttribute("message", "You can only close your own requests in 'ordered' status.");
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
            return;
        }

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser_close.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", requestId);

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

        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String result = request.getParameter("result"); // ✅ opraveno z "status" na "result"

        List<String> allowedStatuses = Arrays.asList("accepted", "rejected_non_compliant", "rejected_not_working");

        if (!allowedStatuses.contains(result)) {
            session.setAttribute("message", "Invalid closure status.");
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
            return;
        }

        PurchaseRequest pr = requestDAO.findById(requestId);
        int purchaserId = (int) session.getAttribute("user_id");

        if (pr == null || pr.getPurchaserId() != purchaserId || !"ordered".equals(pr.getStatus())) {
            session.setAttribute("message", "You can only close your own ordered requests.");
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
            return;
        }

        boolean updated = requestDAO.updateStatus(requestId, "closed_" + result);

        if (updated) {
            session.setAttribute("message", "Request #" + requestId + " closed as: " + result.replace('_', ' ') + ".");
        } else {
            session.setAttribute("message", "Failed to close request.");
        }

        response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
    }
}
