package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AllyTeam;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;

public class JoinContestServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        String teamName = request.getParameter(Constants.USERNAME);
        String uboatName = request.getParameter(Constants.UBOAT_PARAM);

        if(teamName == null){
            teamName = SessionUtils.getUsername(request);
        }

        UserManager users = ServletUtils.getUserManager(getServletContext());


        UBoat contest = users.joinContest(uboatName,teamName);

        // uboat was not found
        if(contest == null){
            response.getWriter().print("Uboat was not found uboat=" + uboatName + " team=" + teamName);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType("application/json");
        String uboatJson = Constants.GSON_INSTANCE.toJson(contest);
        response.getWriter().print(uboatJson);
        response.setStatus(HttpServletResponse.SC_OK);

    }
}