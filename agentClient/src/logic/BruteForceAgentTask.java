package logic;


import bruteforce.AgentSolutionEntry;
import bruteforce.AgentTask;
import bruteforce.AgentWorker;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;
import machine.Engine;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import sun.management.Agent;
import users.AgentEntry;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static utils.Constants.*;


/*
*   BruteForceTask sends the Bruteforce solutions to the server*
*
 */
public class BruteForceAgentTask extends Task {

    private final String agentName;
    private final String teamName;
    private final long tasksPerRequest;
    private static final int TASKS_QUEUE_SIZE = 10000;
    private final BlockingQueue<AgentTask> tasksQueue;
    private final List<AgentSolutionEntry> agentSolutions;
    private final List<AgentWorker> bruteforceWorkers;

    private final UBoat uboat;
    private final String secret;

    private BooleanProperty contestEnded;



    public BruteForceAgentTask(Engine engine, UBoat uboat, AgentEntry agentEntry){
        // agent fields
        this.agentName = agentEntry.getName();
        this.teamName = agentEntry.getTeamName();
        tasksPerRequest = agentEntry.getTaskSize();
        this.uboat = uboat;
        secret = uboat.getSecretMessage();

        tasksQueue = new ArrayBlockingQueue<>(TASKS_QUEUE_SIZE);
        agentSolutions = new ArrayList<>();




        // check for valid workers parameter
        int workers = agentEntry.getWorkersCount();
        if(workers < 0 || workers > 4){
            workers = 1;
        }
        bruteforceWorkers = new ArrayList<>();
        for (int i = 0; i < workers; i++) {
            AgentWorker worker = new AgentWorker(engine, secret,tasksQueue, this::addSolutions);
            bruteforceWorkers.add(worker);
        }



    }

    @Override
    protected Void call() throws Exception {

        startWorkerThreads();
        while(!contestEnded.get()){
            if(tasksQueue.isEmpty()){
                sendAgentSolutions();
                createAgentTaskRequest();
            }

            Thread.sleep(1000);
        }


        // contact the Server's DM to get more tasks


        System.out.println("Agent " + agentName + " Bruteforce task is done!");
        return null;
    }




    private void startWorkerThreads() {
        int i=1;
        for (AgentWorker worker  : this.bruteforceWorkers) {
            Thread t = new Thread(worker);
            t.setName(this.agentName + String.valueOf(i++));
            t.start();
        }
    }


    public void addSolutions(List<AgentSolutionEntry> solutions){
        this.agentSolutions.addAll(solutions);
    }

    private void sendAgentSolutions() {
        AgentSolutionEntry solutionEntry = this.agentSolutions.remove(0);
        if(solutionEntry == null){
            return;
        }
        String candidateJson = GSON_INSTANCE.toJson(solutionEntry);
        String uboatName = uboat.getName();
        RequestBody formBody = new FormBody.Builder()
                .add(UBOAT_PARAM, uboatName)
                .add(CANDIDATE_PARAM, candidateJson)
                .build();
        Request request = new Request.Builder()
                .url(SEND_CANDIDTATE_URL)
                .post(formBody)
                .build();
        HttpClientUtil.postAsyncRequest(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    AgentSolutionEntry solution = GSON_INSTANCE.fromJson(rawBody,AgentSolutionEntry.class);
                    System.out.println(solution);
                }
                response.close();
            }
        });


    }

    private void createAgentTaskRequest() {
        String finalUrl = HttpUrl
                .parse(Constants.FETCH_AGENT_TASKS_URL)
                .newBuilder()
                .addQueryParameter(Constants.TEAM_NAME_PARAM, this.teamName)
                .addQueryParameter(Constants.AGENT_TASK_SIZE, String.valueOf(this.tasksPerRequest))
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
                    Type listType = new TypeToken<List<Agent>>(){}.getType();
                    List<AgentTask> taskList = GSON_INSTANCE.fromJson(rawBody,listType);
                    if(taskList != null){
                        tasksQueue.addAll(taskList);
                    }
                }
            }
        });

    }

}
