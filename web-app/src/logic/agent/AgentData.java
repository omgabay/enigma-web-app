package logic.agent;

public class AgentData {

    private final String agentName;
    private final int numberOfWorkers;
    private final long bruteforceTaskSize;

    public String getName() {
        return agentName;
    }

    public int getNumberOfWorkers() {
        return numberOfWorkers;
    }

    public long getBruteforceTaskSize() {
        return bruteforceTaskSize;
    }

    public AgentData(String agentName, int numberOfWorkers, long bruteforceTaskSize) {
        this.agentName = agentName;
        this.numberOfWorkers = numberOfWorkers;
        this.bruteforceTaskSize = bruteforceTaskSize;
    }
}
