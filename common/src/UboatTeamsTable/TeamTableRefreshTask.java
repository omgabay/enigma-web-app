package UboatTeamsTable;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class TeamTableRefreshTask extends TimerTask {
    private final Timer timer;

    private final Consumer<List<AllyTeam>> teamListConsumer;

    private final String uboatName;


    public TeamTableRefreshTask(String uboat, Consumer<List<AllyTeam>> teamsConsumer, Timer timer){
        this.teamListConsumer = teamsConsumer;
        this.timer = timer;
        uboatName = uboat;
    }



    @Override
    public void run() {
        System.out.println(uboatName);
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, uboatName)
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

                    UBoat uboat = GSON_INSTANCE.fromJson(rawBody, UBoat.class);
                    List<AllyTeam> teamsList= uboat.getAllyTeams();

                    Platform.runLater(()->
                    {
                        teamListConsumer.accept(teamsList);
                    });
                }
            }
        });





    }
}
