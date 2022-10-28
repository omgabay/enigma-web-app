package users;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

public class AllyTeam extends User{

    List<AgentEntry> agentList;
    GameStatus status;
    boolean isReady;
    int missionSize;


    public AllyTeam(String name){
        super(name,ClientType.ALLY);
        agentList = new ArrayList<>();
        status = GameStatus.REGISTRATION;
        isReady = false;
        missionSize = 900;


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

    public boolean getReady(){
        return this.isReady;
    }

    public ObservableValue<Integer> getAgentCount() {
        int size = this.agentList.size();
        return new SimpleIntegerProperty(size).asObject();
    }

    public void setMissionSize(int size) {
        this.missionSize = size;
    }



    public ObservableValue<Integer> missionSizeProperty() {
        return new SimpleIntegerProperty(this.missionSize).asObject();
    }
}
