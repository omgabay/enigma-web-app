package servlets;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.*;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static utils.Constants.GSON_INSTANCE;

public class ReadyUpdateServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String uboatName = request.getParameter(Constants.UBOAT_PARAM);
        String teamName = request.getParameter(Constants.TEAM_NAME_PARAM);
        if(uboatName == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat myUboat = users.getUboat(uboatName);
        // UBoat was not found
        if(myUboat == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if(teamName != null) {
            myUboat.teamIsReady(teamName);
        }else{
            myUboat.setReady();
        }

        try(PrintWriter out = response.getWriter()){
            String jsonResponse = GSON_INSTANCE.toJson(myUboat);
            response.setContentType("application/json");
            out.print(jsonResponse);
            out.flush();
        }

    }

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String userFromParam = request.getParameter(Constants.USERNAME);
//        if(userFromParam == null){
//            userFromParam = SessionUtils.getUsername(request);
//        }
//
//        UserManager userManager = ServletUtils.getUserManager(getServletContext());
//        User user = userManager.getUser(userFromParam);
//        if(userFromParam == null){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return;
//        }
//
//        if(user == null){
//            response.setContentType("text/plain");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.getWriter().print("User "+ userFromParam +" was not found in user manager  (ReadyUpdateServlet)");
//            return;
//        }
//        if(user instanceof  AllyTeam){
//            AllyTeam ally = (AllyTeam) user;
//            ally.setReady(true);
//        }
//        if(user instanceof UBoat){
//            UBoat uboat = (UBoat) user;
//            uboat.setReady();
//        }
//
//        if(user instanceof AgentEntry){
//            AgentEntry agent = (AgentEntry) user;
//            agent.setReady(true);
//         }
//
//
//        response.setContentType("application/json");
//        try(PrintWriter out = response.getWriter()){
//            out.print(user);
//            out.flush();
//        }
//
//        response.setStatus(HttpServletResponse.SC_OK);
//    }

}
