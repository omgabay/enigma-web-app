package servlets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.*;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import static utils.Constants.*;



public class LoginServlet extends HttpServlet {



    /**
     *  Params for request - username = user that wants to login
     *                       type = client type
     *
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet

            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            String clientType = request.getParameter(CLIENT_TYPE);   // UBoat , Allies team or Agent
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                //no username in session and no username in parameter - not standard situation. it's a conflict

                // stands for conflict in server state
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();


                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("text/plain");
                        response.getWriter().print(errorMessage);
                    }
                    else {
                        User user = createUserObject(request, usernameFromParameter, clientType);
                        userManager.addUser(usernameFromParameter, user);
                        request.getSession(true).setAttribute(USERNAME, usernameFromParameter);

                        //redirect the request to the chat room - in order to actually change the URL
                        System.out.println("On login, request URI is: " + request.getRequestURI());

                        // Writing back to the client the name that was added

                        //response.getWriter().print(Constants.GSON_INSTANCE.toJson(usernameFromParameter));
                        response.getWriter().print(Constants.GSON_INSTANCE.toJson(user));
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        } else {
            //user is already logged in
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private User createUserObject(HttpServletRequest request ,String username,  String type) {
        User user = null;
        switch(type) {
            case "uboat":
                user = new UBoat(username);
                break;
            case "ally":
                user = new AllyTeam(username);
                break;
            case "agent":
                String team = request.getParameter(TEAM_NAME_PARAM);
                int workers = Integer.parseInt(request.getParameter(AGENT_WORKER_COUNT));
                long taskSize = Long.parseLong(request.getParameter(AGENT_TASK_SIZE));
                user = new AgentEntry(username, team, taskSize, workers);
                break;
        }
        return user;
    }
}
