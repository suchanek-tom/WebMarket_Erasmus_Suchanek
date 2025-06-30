package com.webmarket.controller.admin;

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

@WebServlet("/admin/proposals")
public class AdminProposalsController extends HttpServlet {

    private final PurchaseProposalDAO proposalDAO = new PurchaseProposalDAO();
    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String reqIdStr = request.getParameter("requestId");
        if (reqIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing requestId");
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(reqIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid requestId");
            return;
        }

        List<PurchaseProposal> proposals = proposalDAO.findByRequestId(requestId);
        PurchaseRequest pr = requestDAO.findById(requestId);

        if (pr == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Request not found");
            return;
        }

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("admin_view_proposals.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", requestId);
        data.put("requestStatus", pr.getStatus());
        data.put("proposals", proposals);

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Template processing error", e);
        }
    }
}
