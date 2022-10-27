package users;

import java.util.ArrayList;
import java.util.List;

public class AllyTeam extends User{


    List<AgentEntry> agentList;


    public AllyTeam(String name){
        super(name,ClientType.ALLY);
        agentList = new ArrayList<>();
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
}
