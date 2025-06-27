package com.webmarket.controller.technician;

import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.dao.PurchaseProposalDAO;
import com.webmarket.model.PurchaseRequest;
import com.webmarket.model.PurchaseProposal;
import com.webmarket.utils.FreemarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/technician/*")
public class TechnicianController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();
    private final PurchaseProposalDAO proposalDAO = new PurchaseProposalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"technician".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String path = request.getPathInfo();
        if (path == null || path.equals("/") || path.equals("/dashboard")) {
            showDashboard(request, response, session);
        } else if (path.equals("/propose")) {
            showProposalForm(request, response, session);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        int technicianId = (int) session.getAttribute("user_id");

        List<PurchaseRequest> unassignedRequests = requestDAO.findUnassignedRequests();
        List<PurchaseRequest> myRequests = requestDAO.findByTechnicianId(technicianId);

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("technician_dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("unassignedRequests", unassignedRequests);
        data.put("myRequests", myRequests);
        data.put("username", session.getAttribute("username"));

        String message = (String) session.getAttribute("message");
        if (message != null) {
            data.put("message", message);
            session.removeAttribute("message");
        }

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker template error", e);
        }
    }

    private void showProposalForm(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        // Ověření existence parametru a validace
        String reqIdStr = request.getParameter("requestId");
        if (reqIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing requestId parameter");
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(reqIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid requestId parameter");
            return;
        }

        int technicianId = (int) session.getAttribute("user_id");

        // Kontrola, zda technik patří k danému requestu nebo zda je request nepřidělený (podle pravidel)
        PurchaseRequest pr = requestDAO.findById(requestId);
        if (pr == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase request not found");
            return;
        }
        if (pr.getAssignedTechnicianId() != technicianId) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not assigned to this request");
            return;
        }

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("technician_propose.ftl.html");

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
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"technician".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String path = request.getPathInfo();

        if ("/propose".equals(path)) {
            handleProposalPost(request, response, session);
        } else if ("/take".equals(path)) {
            handleTakePost(request, response, session);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleProposalPost(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String features = request.getParameter("features");
        double price = Double.parseDouble(request.getParameter("price"));
        LocalDate date = LocalDate.parse(request.getParameter("date"));

        int technicianId = (int) session.getAttribute("user_id");

        // Kontrola, zda technik patří k danému requestu
        PurchaseRequest pr = requestDAO.findById(requestId);
        if (pr == null || pr.getAssignedTechnicianId() != technicianId) {
            session.setAttribute("message", "You are not assigned to this request.");
            response.sendRedirect(request.getContextPath() + "/technician/dashboard");
            return;
        }

        if (proposalDAO.existsProposal(requestId, technicianId)) {
            session.setAttribute("message", "You have already submitted a proposal for this request.");
            response.sendRedirect(request.getContextPath() + "/technician/dashboard");
            return;
        }

        PurchaseProposal proposal = new PurchaseProposal();
        proposal.setRequestId(requestId);
        proposal.setTechnicianId(technicianId);
        proposal.setFeatures(features);
        proposal.setPrice(price);
        proposal.setDate(date);

        proposalDAO.insert(proposal);
        requestDAO.updateStatus(requestId, "proposed");

        session.setAttribute("message", "Proposal submitted for request #" + requestId + ".");
        response.sendRedirect(request.getContextPath() + "/technician/dashboard");
    }

    private void handleTakePost(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        int technicianId = (int) session.getAttribute("user_id");

        boolean success = requestDAO.assignTechnician(requestId, technicianId);

        if (success) {
            session.setAttribute("message", "Request #" + requestId + " has been assigned to you.");
        } else {
            session.setAttribute("message", "Failed to assign request #" + requestId + ".");
        }

        response.sendRedirect(request.getContextPath() + "/technician/dashboard");
    }
}
