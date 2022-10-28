package servlets.bruteforce;

import bruteforce.AgentSolutionEntry;
import bruteforce.BruteforceSolutionManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GetSolutionsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        /*
        verify chat version given from the user is a valid number. if not it is considered an error and nothing is returned back
        Obviously the UI should be ready for such a case and handle it properly
         */
        int chatVersion = ServletUtils.getIntParameter(request, Constants.SOLUTIONS_VERSION_PARAMETER);
        if (chatVersion == Constants.INT_PARAMETER_ERROR) {
            return;
        }

        String uboatName = request.getParameter(Constants.UBOAT);
        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(uboatName);
        BruteforceSolutionManager solutionManager = uboat.getSolutionsManager();

         /*
        Synchronizing as minimum as I can to fetch only the relevant information from the chat manager and then only processing and sending this information onward
        Note that the synchronization here is on the ServletContext, and the one that also synchronized on it is the chat servlet when adding new chat lines.
         */
        int solutionsManagerVersion = 0;
        List<AgentSolutionEntry> agentEntries = new ArrayList<>();
        synchronized (getServletContext()) {
            solutionsManagerVersion = solutionManager.getVersion();
            agentEntries = solutionManager.getCandidateSolutionFromIndex(solutionsManagerVersion);
        }

        String jsonResponse = Constants.GSON_INSTANCE.toJson(agentEntries);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);


    }
}
