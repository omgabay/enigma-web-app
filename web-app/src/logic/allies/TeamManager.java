package logic.allies;

import logic.agent.AgentData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamManager {


    private final Map<String, List<AgentData>> teamToAgentsMapping;


    public TeamManager() { teamToAgentsMapping = new HashMap<>();}

    public boolean addAgent(String teamName, AgentData agentData){
        if(!teamToAgentsMapping.containsKey(teamName)){
            return false;
        }
        teamToAgentsMapping.get(teamName).add(agentData);
        return true;
    }

    public boolean removeAgent(String teamName, String agentName){
        if(!teamToAgentsMapping.containsKey(teamName)){
            // Team not found - remove operation was not performed
            return false;
        }

        List<AgentData> agents = teamToAgentsMapping.get(teamName);
        for (int i = 0; i < agents.size(); i++) {
            if(agents.get(i).getName().equals(agentName)) {
                agents.remove(i);
                return true;
            }
        }
        return false;
    }


    public List<AgentData> getTeamMembers(String teamName){
        return teamToAgentsMapping.getOrDefault(teamName,null);
    }

    public void createNewTeam(String teamName){
        teamToAgentsMapping.put(teamName, new ArrayList<>());
    }

}
