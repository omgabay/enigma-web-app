package controllers;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AgentEntry;
import users.AllyTeam;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class AlliesClientRefreshTask extends TimerTask {

    Consumer<List<AgentEntry>> updateMyAgentsTable;
    Consumer<List<UBoat>> updateContestTable;

    String clientName;



    public AlliesClientRefreshTask(String clientName, Consumer<List<UBoat>> updateContestTable, Consumer<List<AgentEntry>> updateMyAgents){
        updateMyAgentsTable = updateMyAgents;
        this.updateContestTable = updateContestTable;
    }

    @Override
    public void run() {
        getUBoatListRequest(this.updateContestTable);
        getAllyRequest(this.clientName,this.updateMyAgentsTable);
    }

    public static void getAllyRequest(String allyName, Consumer<List<AgentEntry>> agentListConsumer){
        String finalUrl = HttpUrl
                .parse(Constants.GET_ALLY_TEAM_RESOURCE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, allyName)
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
                    AllyTeam myTeam = GSON_INSTANCE.fromJson(rawBody, AllyTeam.class);
                    List<AgentEntry> agents = myTeam.getAgentList();
                    Platform.runLater(()->
                            agentListConsumer.accept(agents)
                            );
                }
            }
        });

    }


    public static void getUBoatListRequest(Consumer<List<UBoat>> uboatListConsumer){
        String finalUrl = HttpUrl
                .parse(Constants.UBOAT_LIST_RESOURCE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("Something went wrong with UBoat list request in Allies client");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            String rawBody = response.body().string();
                            Type listType = new TypeToken<List<UBoat>>(){}.getType();
                            List<UBoat> uBoats = GSON_INSTANCE.fromJson(rawBody,listType);
                            Platform.runLater(() ->
                                    uboatListConsumer.accept(uBoats)
                                    );
                        }
                    }
                }

        );




    }


}
