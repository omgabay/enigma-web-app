package users;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.util.List;

public class AgentEntry extends User {



    private String teamName; // the team agent is part of

    private final long taskSize; // number of tasks to pull from the server

    /**
     * Number of threads for BruteForce task
     */
    private int workersCount;
    private boolean startBruteforce;

    List<String> myCandidateSolutions;


    public AgentEntry(String name, String team, long taskSize, int workers){
        super(name, ClientType.AGENT);
        this.teamName = team;
        this.taskSize = taskSize;
        this.workersCount = workers;
        this.startBruteforce = false;
    }

    public String getTeamName() {
        return teamName;
    }

    public long getTaskSize() {
        return taskSize;
    }

    public int getWorkersCount() {
        return workersCount;
    }


    public void addNewCandidateSolution(String candidate){
        this.myCandidateSolutions.add(candidate);
    }

    public ObservableValue<String> WorkerCountProperty() {
        return new SimpleStringProperty(String.valueOf(this.workersCount));
    }

    public ObservableValue<String> TaskSizeProperty() {
        String taskSizeString = String.valueOf(this.taskSize);
        return new SimpleStringProperty(taskSizeString);
    }

    public void setReady(boolean startBF) {
        this.startBruteforce = startBF;
    }
}
