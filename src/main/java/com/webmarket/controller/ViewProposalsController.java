package com.webmarket.controller;

import com.webmarket.dao.PurchaseProposalDAO;
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

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker error", e);
        }
    }
}
