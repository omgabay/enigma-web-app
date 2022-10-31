package logic;

import bruteforce.AgentSolutionEntry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.http.HttpClientUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Configuration.SUBMIT_CANDIDATE_SOLUTION;

public class SolutionHandler implements Runnable{

    String agentName;
    List<String> candidateSolutionQueue;

    boolean gameIsActive;




    public SolutionHandler(String name){
        agentName = name;
        candidateSolutionQueue = new ArrayList<>();
        gameIsActive = true;
    }
    public void addCandidateSolution(String candidateSolution) {
        candidateSolutionQueue.add(candidateSolution);

    }

    @Override
    public void run() {
        while(gameIsActive){
            //
            if(candidateSolutionQueue.isEmpty()){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                String candidate = candidateSolutionQueue.remove(0);
                sendCandidateToServer(candidate);
            }

        }
    }

    private void sendCandidateToServer(String candidate) {

        String finalUrl = HttpUrl
                .parse(SUBMIT_CANDIDATE_SOLUTION)
                .newBuilder()
                .addQueryParameter("agent", agentName)
                .addQueryParameter("candidate",candidate)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
               // httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure...:(");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure. Error code: " + response.code());
                }
            }
        });
    }
}
