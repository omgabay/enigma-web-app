package servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        if(usernameFromSession == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        System.out.println("Clearing session for " + usernameFromSession);
            userManager.removeUser(usernameFromSession);
            SessionUtils.clearSession(request);
            Cookie versionCookie = new Cookie(Constants.SOLUTIONS_VERSION_COOKIE, "0");
            response.addCookie(versionCookie);

    }

}
