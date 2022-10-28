package servlets.allies;

import com.sun.org.apache.bcel.internal.Const;
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
import java.util.List;

public class JoinContestServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        String username = request.getParameter(Constants.USERNAME);

        if(username == null){
            username = SessionUtils.getUsername(request);
        }
        UserManager users = ServletUtils.getUserManager(getServletContext());
        AllyTeam allyTeam = users.getTeam(username);




        String uboatFromParameter = request.getParameter(Constants.UBOAT);

        UBoat uboat = users.getUboat(uboatFromParameter);

        if(uboat == null){
            response.getWriter().print("Uboat was not found uboat=" + uboatFromParameter + " team=" + username);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        uboat.addTeam(allyTeam);
        List<AllyTeam> teamList = uboat.getAllyTeams();
        response.setContentType("application/json");
        String teamsListJson = Constants.GSON_INSTANCE.toJson(teamList);
        response.getWriter().print(teamsListJson);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}