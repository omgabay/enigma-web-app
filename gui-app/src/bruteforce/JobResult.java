package bruteforce;

import java.util.List;

public class JobResult {

    private final String agentID;
    private final boolean successful;
    private final List<String> solutions;

    AgentJob completedJob;





    private JobResult(String agentID, List<String> solutions, boolean success){
        this.agentID = agentID;
        this.successful = success;
        this.solutions = solutions;

    }

    public JobResult(String agentName, List<String> solutions , AgentJob request, boolean successful){
        this(agentName, solutions, successful);
        this.completedJob = request;
    }


    public List<Integer> getRotorSetup(){
        return this.completedJob.getRotorsIds();
    }

    public List<Integer> getRotorsPosition(){
        return this.completedJob.getPositions();
    }

    public List<String> getSolutionCandidate(){
        return this.solutions;
    }

    public boolean successful(){
        return successful;
    }


    public String getAgentID() {
        return agentID;
    }
}
