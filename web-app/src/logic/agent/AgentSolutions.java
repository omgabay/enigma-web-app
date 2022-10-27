package logic.agent;

import java.util.List;

public class AgentSolutions {

    private final String agentName;

    private final String uboatName;

    private final List<String> possibleSolutions;


    public AgentSolutions(String agentName, String uboatName, List<String> possibleSolutions) {
        this.agentName = agentName;
        this.uboatName = uboatName;
        this.possibleSolutions = possibleSolutions;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getUboatName() {
        return uboatName;
    }

    public List<String> getPossibleSolutions() {
        return possibleSolutions;
    }
}
