package servlets.bruteforce;

import bruteforce.AgentSolutionEntry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GetSolutionsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        /*
        verify chat version given from the user is a valid number. if not it is considered an error and nothing is returned back
        Obviously the UI should be ready for such a case and handle it properly
         */
//        int chatVersion = ServletUtils.getIntParameter(request, Constants.SOLUTIONS_VERSION_PARAMETER);
//        if (chatVersion == Constants.INT_PARAMETER_ERROR) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }

        String uboatName = request.getParameter(Constants.UBOAT_PARAM);
        int solutionsVersion = ServletUtils.getIntParameter(request,Constants.SOLUTIONS_VERSION_PARAMETER);
        if(solutionsVersion == Constants.INT_PARAMETER_ERROR){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(uboatName);
        if(uboat == null){
            response.setContentType("text/plain");
            response.getWriter().print("uboat was not found - GetSolutionsServlet");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

//        int solutionsVersion = 0;
//        Cookie versionCookie = getVersionCookie(request.getCookies());
//        solutionsVersion = Integer.parseInt(versionCookie.getValue());
//        solutionsVersion = solutionsVersion + solutions.size();
//        versionCookie.setValue(String.valueOf(solutionsVersion));
//        response.addCookie(versionCookie);
        System.out.println("request for candidate solution uboat " + uboat.username + " version=" + solutionsVersion);

        List<AgentSolutionEntry> solutions = uboat.getSolutionsWithVersion(solutionsVersion);

        String jsonResponse = Constants.GSON_INSTANCE.toJson(solutions);
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }






    private Cookie getVersionCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("solutionVersion")){
                return cookie;
            }
        }
        return new Cookie("solutionVersion", "0");
    }
}
