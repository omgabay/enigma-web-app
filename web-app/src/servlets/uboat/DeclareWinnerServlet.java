package servlets.uboat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.uboat.UBoatManager;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;


public class DeclareWinnerServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uboatFromSession = SessionUtils.getUsername(request);

        UBoatManager uboatManager = ServletUtils.getUBoatManager(getServletContext());


    }



    }
