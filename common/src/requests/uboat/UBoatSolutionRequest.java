package requests.uboat;

import bruteforce.AgentSolutionEntry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class UBoatSolutionRequest {


    public static void requestCandidates(String uboatName, int version, Consumer<List<AgentSolutionEntry>> solutionConsumer){
        String finalUrl = HttpUrl
                .parse(Constants.GET_CANDIDATES_SOLUTIONS)
                .newBuilder()
                .addQueryParameter(Constants.UBOAT_PARAM, uboatName)
                .addQueryParameter(Constants.SOLUTIONS_VERSION_PARAMETER, String.valueOf(version))
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
                    List<AgentSolutionEntry> solutionsList = uboat.getSolutionsWithVersion(version);
                    solutionConsumer.accept(solutionsList);
                }

            }
        });

    }

}
