package servlets.uboat;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;

import static utils.Constants.GSON_INSTANCE;

/**
 *  Uboat will send to this servlet the encrypted text the Allies teams will try to decrypt
 *  This servlet will create the contest and once all teams are ready the contest will begin
 */
public class CreateContestServlet extends HttpServlet {

    private final static String SECRET_PARAM_NAME = "secret";



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        String userFromSession = SessionUtils.getUsername(request);
        if(userFromSession == null || userFromSession.isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        String secret = request.getParameter(SECRET_PARAM_NAME);
        if(secret == null || secret.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(userFromSession);
        if(uboat == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        uboat.setSecretMessage(secret);
        boolean success = uboat.setReady();
        String jsonResponse = GSON_INSTANCE.toJson(uboat);
        response.setContentType("application/json");
        try(PrintWriter out = response.getWriter()){
            out.print(jsonResponse);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
