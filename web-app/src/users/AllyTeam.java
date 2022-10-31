package users;

import bruteforce.AgentTask;
import bruteforce.Decryption;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import machine.IEngine;
import bruteforce.Difficulty;
import java.util.ArrayList;
import java.util.List;

public class AllyTeam extends User{

    List<AgentEntry> agentList;
    GameStatus status;

    Decryption DM = null;
    boolean isReady;
    int missionSize;

    String uboatName;



    public AllyTeam(String name){
        super(name,ClientType.ALLY);
        agentList = new ArrayList<>();
        status = GameStatus.WAITING_FOR_TEAMS;
        isReady = false;
        missionSize = 900;
        uboatName = null;
    }


    public AgentEntry getAgent(String agentName){

        for(AgentEntry agent : agentList){
            if(agent.username.equals(agentName)){
                return agent;
            }
        }

        return null;

    }

    public List<AgentEntry> getAgentList(){
        return this.agentList;
    }



    public void addAgentToTeam(AgentEntry agent) {
        this.agentList.add(agent);
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public ObservableValue<Integer> getAgentCount() {
        int size = this.agentList.size();
        return new SimpleIntegerProperty(size).asObject();
    }


    public SimpleStringProperty readyStatusProperty(){
        if(isReady){
            return  new SimpleStringProperty("TEAM IS READY");
        }
        return new SimpleStringProperty("NOT READY");
    }

    public void setMissionSize(int size) {
        this.missionSize = size;
    }


    public void createTeamDM(IEngine engine, Difficulty difficulty ){
        this.DM = new Decryption(engine, difficulty, 900);
        this.status = GameStatus.CONTEST_RUNNING;
    }



    public ObservableValue<Integer> missionSizeProperty() {
        return new SimpleIntegerProperty(this.missionSize).asObject();
    }

    public List<AgentTask> getNewTasks(long taskCount) {
        if(this.status != GameStatus.CONTEST_RUNNING){
            return null;
        }
        List<AgentTask> tasks = new ArrayList<>();
        if(isReady && !DM.isDone()){
            tasks.addAll(DM.fetchAgentTasks(taskCount));
        }
        if(DM.isDone()){
            this.status = GameStatus.DONE;
        }

        return tasks;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("team name: ").append(this.username).append('\n');
        System.out.println("team name: " + this.username);
        for (AgentEntry agent   : this.agentList ) {
            sb.append(agent.username).append(" ");
        }
        sb.append('\n');
        return sb.toString();
    }
}
