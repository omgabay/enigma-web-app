package requests;

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
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class UBoatClientRefresher extends TimerTask {


    Consumer<List<AllyTeam>> updateUboatTeams;

    public UBoatClientRefresher(Consumer<List<AllyTeam>> teamListConsumer){
        updateUboatTeams = teamListConsumer;
    }



    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.TEAMS_LIST_RESOURCE)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                List<AllyTeam> uboatTeams;
                if (response.isSuccessful()) {
                    String rawBody = response.body().string();
                    uboatTeams = GSON_INSTANCE.fromJson(rawBody, new TypeToken<List<UBoat>>(){}.getType());
                    Platform.runLater(() ->{
                        updateUboatTeams.accept(uboatTeams);
                    });
                }
            }
        });


    }
}
