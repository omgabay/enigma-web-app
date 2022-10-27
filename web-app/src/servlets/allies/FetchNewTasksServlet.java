package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.allies.TeamManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;

public class FetchNewTasksServlet extends HttpServlet {

    @Override
    // Response from Agent client to get new tasks from DM
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TeamManager teamManager = ServletUtils.getTeamManager(getServletContext());
        long taskSize = Integer.parseInt(request.getParameter(Constants.TASK_SIZE));
        String userFromParameter = request.getParameter(Constants.USERNAME);
        if(userFromParameter == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String userFromSession = SessionUtils.getUsername(request);

        if(!userFromParameter.equals(userFromSession)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


            //teamManager.getTeam()
    }

}
