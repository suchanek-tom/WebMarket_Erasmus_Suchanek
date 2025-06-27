package com.webmarket.controller.purchaser;

import com.webmarket.dao.PurchaseProposalDAO;
import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.PurchaseProposal;
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

@WebServlet("/purchaser/requests/detail")
public class PurchaserRequestDetailController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();
    private final PurchaseProposalDAO proposalDAO = new PurchaseProposalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        int requestId = Integer.parseInt(request.getParameter("requestId"));

        // ✅ načti požadavek s názvem kategorie a jménem uživatele
        PurchaseRequest purchaseRequest = requestDAO.findByIdWithCategoryAndUser(requestId);
        if (purchaseRequest == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
            return;
        }

        List<PurchaseProposal> proposals = proposalDAO.findByRequestId(requestId);


        
        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser/purchaser_request_detail.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("request", purchaseRequest);
        data.put("proposals", proposals);

        String message = (String) session.getAttribute("message");
        if (message != null) {
            data.put("message", message);
            session.removeAttribute("message");
        }

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Template rendering error", e);
        }
    }
}