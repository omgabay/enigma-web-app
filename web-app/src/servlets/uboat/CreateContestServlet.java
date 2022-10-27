package servlets.uboat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.servlet.SessionUtils;

import java.io.IOException;

/**
 *  Uboat will send to this servlet the encrypted text the Allies teams will try to decrypt
 *  This servlet will create the contest and once all teams are ready the contest will begin
 */
public class CreateContestServlet extends HttpServlet {

    private final static String SECRET_PARAM_NAME = "secret";

    private final static String UBOAT_PARAM_NAME = "uboat";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        String secret = request.getParameter(SECRET_PARAM_NAME);



        String usernameFromSession = SessionUtils.getUsername(request);
        if(usernameFromSession == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


    }
}
