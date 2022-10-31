package utils;

//import com.google.gson.Gson;

import com.google.gson.Gson;

public class CommonConstants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static int REFRESH_RATE = 2000;



    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    public static final String CANDIDATE_PARAM = "candidate";
    public static final String ICON_RESOURCE = "/images/turing_icon.png";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "cracking-enigma";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    public final static String LOGOUT = FULL_SERVER_PATH + "/logout";
    public static final String TEAMS_LIST_RESOURCE = FULL_SERVER_PATH +  "/teams";

    public static final String UBOAT_LIST_RESOURCE = FULL_SERVER_PATH + "/uboats";

    public static final String AGENT_LIST_RESOURCE = FULL_SERVER_PATH + "/agents";

    public static final String TEAM_RESOURCE = FULL_SERVER_PATH + "/team" ;





    // Parameters
    public static final String USERNAME = "username";
    public static final String USER_NAME_ERROR = "username_error";
    public static final String CLIENT_TYPE = "type";
    public static final String TEAM_NAME_PARAM = "team";
    public static final String TASK_SIZE = "taskSize";

    public static final String AGENT_WORKER_COUNT = "workers";

    public static final String AGENT_TASK_SIZE = "taskSize";






    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;



    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}