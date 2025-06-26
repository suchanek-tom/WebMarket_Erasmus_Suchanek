package com.webmarket.controller;   

import com.webmarket.dao.PurchaseProposalDAO;
import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.PurchaseProposal;
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

@WebServlet("/view-proposals")
public class ViewProposalsController extends HttpServlet {

    private final PurchaseProposalDAO proposalDAO = new PurchaseProposalDAO();
    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int requestId = Integer.parseInt(request.getParameter("requestId"));
        List<PurchaseProposal> proposals = proposalDAO.findByRequestId(requestId);

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("view_proposals.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("proposals", proposals);
        data.put("requestId", requestId);

        String message = (String) request.getSession().getAttribute("message");
        if (message != null) {
            data.put("message", message);
            request.getSession().removeAttribute("message");
        }

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        int requestId = Integer.parseInt(request.getParameter("requestId"));
        int proposalId = Integer.parseInt(request.getParameter("proposalId"));

        // Označí návrh jako vítězný
        proposalDAO.setAsWinner(proposalId);

        // Nastaví stav žádosti jako "accepted"
        requestDAO.updateStatus(requestId, "accepted");

        session.setAttribute("message", "Proposal #" + proposalId + " was accepted successfully.");
        response.sendRedirect(request.getContextPath() + "/view-proposals?requestId=" + requestId);
    }
}
