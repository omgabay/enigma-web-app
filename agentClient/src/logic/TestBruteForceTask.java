package logic;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import users.UBoat;
import users.User;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;

import static utils.Configuration.GSON_INSTANCE;

public class TestBruteForceTask {

    private UBoat uboat;
    private AllyTeam myTeam;
    private User agent;

    public static void main(String[] args) {
        String uboatName = "omerg";
        String allyName = "ally";
        String agentName = "agent123";
        TestBruteForceTask test = new TestBruteForceTask();
//        test.loginUBoat(uboatName);
//        test.loginAlly(allyName);
//        test.loginAgent(agentName, allyName);
//        test.loginAlly("ally3");
//        test.loginAlly("ally4");
//        test.allyJoinRequest("ally3", uboatName);
//        test.allyJoinRequest("ally4" , uboatName);
        test.allyReadyUpdateRequest("ally3",uboatName);
        test.allyReadyUpdateRequest("ally4", uboatName);

    }

    public static void allyReadyUpdateRequest(String allyClient, String uboatName) {
        String finalUrl = HttpUrl
                .parse(Constants.READY_UPDATE_URL)
                .newBuilder()
                .addQueryParameter(Constants.UBOAT_PARAM, uboatName)
                .addQueryParameter(Constants.TEAM_NAME_PARAM, allyClient)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error with ally ready update");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){

                }
            }
        });

    }


    public void loginAgent(String agentName, String teamName){
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, agentName)
                .addQueryParameter(Constants.CLIENT_TYPE, Constants.AGENT_PARAM)
                .addQueryParameter(Constants.TEAM_NAME_PARAM, teamName)
                .addQueryParameter(Constants.AGENT_WORKER_COUNT,String.valueOf(2))
                .addQueryParameter(Constants.AGENT_TASK_SIZE, String.valueOf(10))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("Error with agent login");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            String rawBody = response.body().string();
                            agent = GSON_INSTANCE.fromJson(rawBody, User.class);
                        }
                    }
                }

        );

    }


    public void loginAlly(String allyName){
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, allyName)
                .addQueryParameter(Constants.CLIENT_TYPE, Constants.ALLY_PARAM)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("failed in uboat login - was trying to create " + allyName);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    System.out.println(rawBody);
                    uboat = GSON_INSTANCE.fromJson(rawBody, UBoat.class);

                }
            }
        });


    }


    public void allyJoinRequest(String allyName, String uboatName){
        String finalUrl = HttpUrl
                .parse(Constants.JOIN_CONTEST_URL)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, allyName)
                .addQueryParameter(Constants.UBOAT_PARAM, uboatName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    System.out.println(rawBody);
                    uboat = GSON_INSTANCE.fromJson(rawBody,UBoat.class);
                }
            }
        });

    }





    public void loginUBoat(String username){
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, username)
                .addQueryParameter(Constants.CLIENT_TYPE, Constants.UBOAT_PARAM)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("failed in uboat login - was trying to create " + username);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    System.out.println(rawBody);
                    uboat = GSON_INSTANCE.fromJson(rawBody, UBoat.class);

                }
            }
        });


    }

}
