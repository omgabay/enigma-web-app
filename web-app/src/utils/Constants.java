package utils;

//import com.google.gson.Gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static int REFRESH_RATE = 2000;
    // GSON instance
    //public final static Gson GSON_INSTANCE = new Gson();
    public final static Gson GSON_INSTANCE = new Gson();


    // SERVER DOMAIN
    public final static String BASE_DOMAIN = "localhost";
    public static final String MACHINECODE_PARAM = "machineCode";

    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/cracking-enigma";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;




    // Server resources locations

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";

    public final static String USERS_LIST = FULL_SERVER_PATH + "/allUsers";

    public final static String LOGOUT = FULL_SERVER_PATH + "/logout";
    public static final String TEAMS_LIST_RESOURCE = FULL_SERVER_PATH +  "/teams";

    public static final String UBOAT_LIST_RESOURCE = FULL_SERVER_PATH + "/uboats";

    public static final String JOIN_CONTEST_URL = FULL_SERVER_PATH + "/join";
    public static final String ADD_AGENT_TO_TEAM_PAGE = FULL_SERVER_PATH + "/team/addAgent";

    public static final String GET_USER_RESOURCE_PAGE = FULL_SERVER_PATH + "/user";

    public static final String FETCH_AGENT_TASKS_URL = FULL_SERVER_PATH + "/fetch-tasks";

    public static final String GET_CANDIDATES_SOLUTIONS = FULL_SERVER_PATH + "/candidate-solutions";


    public static final String GET_ALLY_TEAM_RESOURCE = FULL_SERVER_PATH + "/team" ;

    public static final String READY_UPDATE_URL = FULL_SERVER_PATH + "/i-am-ready";

    public static final String WINNER_UPDATE_URL = FULL_SERVER_PATH + "/winner";

    public static final String GET_LIST_OF_TEAMS_RESOURCE = FULL_SERVER_PATH + "/allTeams" ;


    public static final String ENIGMA_MACHINE_REQUEST = FULL_SERVER_PATH + "/enigma";

    public static final String SEND_CANDIDTATE_URL = FULL_SERVER_PATH + "/candidate-solutions/add";


    // Parameters
    public static final String USERNAME = "username";
    public static final String AGENT_PARAM = "agent";
    public static final String UBOAT_PARAM = "uboat";

    public static final String ALLY_PARAM = "ally";
    public static final String CANDIDATE_PARAM = "candidate";

    public static final String CLIENT_TYPE = "type";
    public static final String TEAM_NAME_PARAM = "team";
    public static final String TASK_SIZE = "taskSize";

    public static final String AGENT_WORKER_COUNT = "workers";

    public static final String AGENT_TASK_SIZE = "taskSize";

    public static final String SOLUTIONS_VERSION_PARAMETER = "version";

    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;



    public static final String SOLUTIONS_VERSION_COOKIE = "solutionVersion";




}