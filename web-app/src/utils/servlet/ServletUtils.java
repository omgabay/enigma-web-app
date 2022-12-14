package utils.servlet;

import logic.allies.TeamManager;
import logic.uboat.UBoatManager;
import users.UserManager;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import static utils.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String TEAM_MANAGER_ATTRIBUTE_NAME = "teamManager";

    private static final String UBOAT_MANAGER_ATTRIBUTE_NAME = "uboatManager";



    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
    private static final Object teamManagerLock = new Object();

    private static final Object uboatManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            //  Creates the User Manager if it was not created , in first login
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static TeamManager getTeamManager(ServletContext servletContext){

        synchronized (teamManagerLock){
            if(servletContext.getAttribute(TEAM_MANAGER_ATTRIBUTE_NAME) == null){
                servletContext.setAttribute(TEAM_MANAGER_ATTRIBUTE_NAME, new TeamManager());
            }
        }
        return (TeamManager)  servletContext.getAttribute(TEAM_MANAGER_ATTRIBUTE_NAME);
    }


    public static UBoatManager getUBoatManager(ServletContext servletContext){

        synchronized (uboatManagerLock){
            if(servletContext.getAttribute(UBOAT_MANAGER_ATTRIBUTE_NAME) == null){
                servletContext.setAttribute(UBOAT_MANAGER_ATTRIBUTE_NAME, new UBoatManager());
            }
        }
        return (UBoatManager)  servletContext.getAttribute(UBOAT_MANAGER_ATTRIBUTE_NAME);
    }


    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }



}
