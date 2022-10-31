package servlets.allies;

import bruteforce.AgentSolutionEntry;
import bruteforce.AgentTask;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.allies.TeamManager;
import users.AllyTeam;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FetchNewTasksServlet extends HttpServlet {

    @Override
    // Response from Agent client to get new tasks from DM
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserManager users = ServletUtils.getUserManager(getServletContext());
        String allyName = request.getParameter(Constants.TEAM_NAME_PARAM);
        String taskCount = request.getParameter(Constants.AGENT_TASK_SIZE);
        AllyTeam ally  = users.getTeam(allyName);


        if(ally == null){
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Ally " + allyName + " does not exist, cannot fetch new tasks");
            return;
        }

        int tasksToPull = 10;
        if(taskCount != null){
            tasksToPull = Integer.parseInt(taskCount);
        }

        List<AgentTask> tasks = ally.getNewTasks(tasksToPull);
        // no tasks to pull - contest is over
        if(tasks == null){
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }


        String jsonResponse = Constants.GSON_INSTANCE.toJson(tasks);
        response.setContentType("application/json");
        try(PrintWriter writer = response.getWriter()){
            writer.print(jsonResponse);
            writer.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);

    }

}
