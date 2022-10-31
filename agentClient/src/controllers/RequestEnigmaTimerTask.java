package controllers;

import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class RequestEnigmaTimerTask extends TimerTask {
    private String teamName;
    private Consumer<UBoat> startContest;

    public RequestEnigmaTimerTask(String team_name, Consumer<UBoat> startContestConsumer){
        this.teamName =team_name;
        this.startContest = startContestConsumer;
    }

    @Override
    public void run() {
        createEnigmaRequest();
    }



    public void createEnigmaRequest(){
        String finalUrl = HttpUrl
                .parse(Constants.ENIGMA_MACHINE_REQUEST)
                .newBuilder()
                .addQueryParameter(Constants.TEAM_NAME_PARAM ,this.teamName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("something went wrong in request enigma agent client");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody;
                if(response.isSuccessful()){
                    rawBody = response.body().string();
                    UBoat uboat = GSON_INSTANCE.fromJson(rawBody, UBoat.class);
                    if(uboat.isReady()){
                        Platform.runLater(()->{
                            startContest.accept(uboat);
                        });
                    }

                }else{
                    String message  = response.body().string();
                    System.out.println(message);

                }
            }
        });
    }

}
