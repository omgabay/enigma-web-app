package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AllyTeam;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;

public class ReadyUpdateServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String teamName = request.getParameter(Constants.TEAM_NAME_PARAM);
        String usernameFromSession = SessionUtils.getUsername(request);

        if(teamName == null || usernameFromSession == null || teamName.equals(usernameFromSession)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        AllyTeam team = ServletUtils.getUserManager(getServletContext()).getTeam(teamName);
        team.setReady(true);


    }

}
