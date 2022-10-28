package servlets.agent;

import bruteforce.AgentSolutionEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import users.AgentEntry;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.*;

public class AddCandidateSolutionServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        // Getting candidate in JSON format
        String candidateJson = request.getParameter(Constants.CANDIDATE_PARAM);

        // Getting Uboat name
        String uboatName = request.getParameter(Constants.UBOAT);

        AgentSolutionEntry solutionEntry = Constants.GSON_INSTANCE.fromJson(candidateJson, AgentSolutionEntry.class);
        UBoat uboat = ServletUtils.getUserManager(getServletContext()).getUboat(uboatName);

        if(uboat == null || candidateJson == null || candidateJson.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        uboat.getSolutionsManager().addCandidateSolution(solutionEntry);
        PrintWriter writer = response.getWriter();
        String jsonResponse = Constants.GSON_INSTANCE.toJson(solutionEntry);
        writer.print(jsonResponse);
        writer.flush();
        response.setStatus(HttpServletResponse.SC_OK);




    }

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

}
