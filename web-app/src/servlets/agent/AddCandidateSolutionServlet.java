package servlets.agent;

import bruteforce.AgentSolutionEntry;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.*;

import static utils.Constants.*;

public class AddCandidateSolutionServlet extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uboatName = request.getParameter(UBOAT_PARAM);
        String candidateJson = request.getParameter(CANDIDATE_PARAM);
        if(candidateJson == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        AgentSolutionEntry solutionEntry = GSON_INSTANCE.fromJson(candidateJson, AgentSolutionEntry.class);



    }
}
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        response.setContentType("application/json");
//
//        // Getting candidate in JSON format
//        String candidate = request.getParameter(Constants.CANDIDATE_PARAM);
//        String agent = request.getParameter(Constants.AGENT_PARAM);
//        String team = request.getParameter(Constants.TEAM_NAME_PARAM);
//        String machineCode = request.getParameter(Constants.MACHINECODE_PARAM);
//
//
//        // Getting Uboat name
//        String uboatName = request.getParameter(Constants.UBOAT_PARAM);
//        UBoat uboat = ServletUtils.getUserManager(getServletContext()).getUboat(uboatName);
//
//        if(uboat == null || candidate== null || agent == null || machineCode == null){
//            response.setContentType("text/plain");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        AgentSolutionEntry solutionEntry = new AgentSolutionEntry(agent,team,candidate,machineCode);
//
//        uboat.addAgentSolution(solutionEntry);
//
//        try(PrintWriter writer = response.getWriter()){
//            String jsonResponse = Constants.GSON_INSTANCE.toJson(solutionEntry);
//            writer.print(jsonResponse);
//            writer.flush();
//        }
//
//        response.setStatus(HttpServletResponse.SC_OK);
//
//
//
//
//    }

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        System.out.println("In get");
//        String usernameFromSession = SessionUtils.getUsername(request);
//        if(usernameFromSession == null){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        }
//
//        UserManager users = ServletUtils.getUserManager(getServletContext());
//        AgentEntry agent = users.getAgent(usernameFromSession);
//        if(agent == null){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        // Getting parameter from request
//        String candidate = request.getParameter(Constants.CANDIDATE_PARAM);
//        if(candidate != null && !candidate.isEmpty()){
//            agent.addNewCandidateSolution(candidate);
//        }
//
//        response.setStatus(HttpServletResponse.SC_OK);
//
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//       System.out.println("in post");
//        response.setContentType("application/json");
//
//       Part part = request.getParts().iterator().next();
//       if(part == null){
//           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//           return;
//       }
//
//       InputStream is = part.getInputStream();
//       InputStreamReader isr = new InputStreamReader(is);
//        JsonReader jsonReader = new JsonReader(isr);
//       AgentSolutionEntry solutionEntry = Constants.GSON_INSTANCE.fromJson(jsonReader, AgentSolutionEntry.class);
//       Gson gson = new GsonBuilder().setPrettyPrinting().create();
//       String json = gson.toJson(solutionEntry);
//       System.out.println(json);
//       try(PrintWriter writer = response.getWriter()){
//           writer.print(json);
//           writer.flush();
//       }
//       response.setStatus(HttpServletResponse.SC_OK);
//
//    }
//}
