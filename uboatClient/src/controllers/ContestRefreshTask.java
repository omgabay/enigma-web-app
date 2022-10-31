package controllers;

import bruteforce.AgentSolutionEntry;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Configuration.GSON_INSTANCE;

public class ContestRefreshTask extends TimerTask {

    private final BooleanProperty downloadCandidates;
    private final Consumer<UBoat> updateContestData;
    private final Consumer<List<AgentSolutionEntry>> solutionConsumer;

    private final String uboatName;

    public ContestRefreshTask(String uboatName, BooleanProperty downloadCandidates, Consumer<UBoat> uboatConsumer, Consumer<List<AgentSolutionEntry>> solutionConsumer) {
        this.updateContestData = uboatConsumer;
        this.downloadCandidates = downloadCandidates;
        this.solutionConsumer = solutionConsumer;
        this.uboatName = uboatName;
    }

    @Override
    public void run() {
        if (!downloadCandidates.get()) {
            requestForUBoat(uboatName, updateContestData, downloadCandidates);
        } else {
            requestForCandidates();
        }
    }


    public void requestForCandidates() {
        String finalUrl = HttpUrl
                .parse(Constants.GET_CANDIDATES_SOLUTIONS)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, uboatName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawBody = response.body().string();
                    Type listType = new TypeToken<List<AgentSolutionEntry>>() {
                    }.getType();
                    List<AgentSolutionEntry> solutionEntries = GSON_INSTANCE.fromJson(rawBody, listType);
                    Platform.runLater(()->
                                solutionConsumer.accept(solutionEntries)
                            );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

        });
    }


    public static void requestForUBoat(String uboatName, Consumer<UBoat> uboatConsumer, BooleanProperty downloadCandidates) {
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

                if (response.isSuccessful()) {
                    String rawBody = response.body().string();
                    UBoat uboat;
                    try {
                        uboat = GSON_INSTANCE.fromJson(rawBody, UBoat.class);
                        if (uboat.isReady()) {
                            downloadCandidates.set(true);
                        }
                        Platform.runLater(()->
                                uboatConsumer.accept(uboat)
                        );
                    } catch (JsonSyntaxException jse) {
                        System.out.println("Uboat request for " + uboatName);
                        System.out.println(rawBody);
                    }
                }
            }
        });
    }
}
