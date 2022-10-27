public class AgentEntry extends User {



    private String teamName; // the team agent is part of

    private final long taskSize; // number of tasks to pull from the server

    /**
     * Number of threads for BruteForce task
     */
    private final int workersCount;


    public AgentEntry(String name, String team, long taskSize, int workers){
        super(name);
        this.teamName = team;
        this.taskSize = taskSize;
        this.workersCount = workers;
    }



}
