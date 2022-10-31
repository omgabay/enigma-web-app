package servlets.testing;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AllyTeam;
import users.UBoat;
import users.User;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class GetUserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String username = request.getParameter(Constants.USERNAME);
        String clientType = request.getParameter(Constants.CLIENT_TYPE);

        UserManager users = ServletUtils.getUserManager(getServletContext());
        User user = users.getUser(username);

        if(user == null){
            try(PrintWriter out = response.getWriter()){
                out.print("User was not found in database");
                out.flush();
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(clientType != null){
            switch (clientType){
                case Constants.ALLY_PARAM:
                    if( !(user instanceof AllyTeam)){
                        printMessage(response,"User was found but it is not of type ally");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                case Constants.UBOAT_PARAM:
                    if( !(user instanceof AllyTeam)){
                        printMessage(response,"User was found but it is not of type uboat");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }

                case Constants.AGENT_PARAM:
                    if( !(user instanceof AllyTeam)){
                        printMessage(response,"User was found but it is not of type agent");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                }
            }
        }

        String json = Constants.GSON_INSTANCE.toJson(user);
        try(PrintWriter out = response.getWriter()){
            out.print(json);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);


    }

    private void printMessage(HttpServletResponse response, String message) {
        response.setContentType("text/plain");
        try(PrintWriter out = response.getWriter()){
            out.print(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
