package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import users.UserManager;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;

public class GetAllTeamsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
//        String teamName = request.getParameter(TEAM_NAME_PARAM);
//
//        if(teamName == null || teamName.isEmpty()){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
        UserManager users = ServletUtils.getUserManager(getServletContext());
        List<String> teams = users.getTeamsList();
        String jsonResponse = GSON_INSTANCE.toJson(teams);
        try(PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);


    }



//    @Override
//    protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        String teamName = request.getParameter(Constants.TEAM_NAME_PARAM);
//        if(teamName == null || teamName.isEmpty()){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        TeamManager teamManager = ServletUtils.getTeamManager(getServletContext());
//        List<AgentData> agentList = teamManager.getTeamMembers(teamName);
//
//        if(agentList == null){
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//        response.setContentType("application/json");
//        String jsonResponse = GSON_INSTANCE.toJson(agentList);
//        try(PrintWriter out = response.getWriter()) {
//            out.print(jsonResponse);
//            out.flush();
//        }
//
//    }

}
