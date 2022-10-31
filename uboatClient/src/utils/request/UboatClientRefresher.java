package utils.request;

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

import static utils.Constants.GSON_INSTANCE;

public class UboatClientRefresher extends TimerTask {
    String uboatName;


    public UboatClientRefresher(String clientName){
        uboatName = clientName;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, uboatName)
                .addQueryParameter(Constants.CLIENT_TYPE, Constants.UBOAT_PARAM)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonResponse = response.body().string();
                    UBoat uboat = GSON_INSTANCE.fromJson(jsonResponse ,UBoat.class);
                    List<AllyTeam> teamList = uboat.getAllyTeams();
                }
            }
        });

    }
}
