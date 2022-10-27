package requests;

import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class TeamViewRefresher extends TimerTask {

    Consumer<AllyTeam> allyConsumer;

    public TeamViewRefresher(Consumer<AllyTeam> allyConsumer) {
        this.allyConsumer = allyConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.TEAM_RESOURCE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    AllyTeam team =GSON_INSTANCE.fromJson(rawBody, AllyTeam.class);
                    Platform.runLater(()->
                            allyConsumer.accept(team)
                            );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }


        });

    }
}
