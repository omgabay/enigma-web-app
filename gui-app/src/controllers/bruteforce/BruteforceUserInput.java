package controllers.bruteforce;

public class BruteforceUserInput {
    // difficulty to update Label
    private final int difficulty;

    private enum DIFFICULTY_LVL {EASY, NORMAL, HARD, CRAZY};

    // Message to decrypt in BF
    private final String secretMessage;

    private final int agents;
    private final long taskSize;

    public BruteforceUserInput(int difficulty, String secretMessage, int agents, long taskSize) {
        this.difficulty = difficulty;
        this.secretMessage = secretMessage;
        this.agents = agents;
        this.taskSize = taskSize;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDifficultyStringFormat(){
        switch(difficulty){
            case 1:
                return "EASY";
            case 2:
                return "MEDIUM";
            case 3:
                return "HARD";

            case 4:
                return "CRAZY";

            default:
                return "NO IDEA";
        }
    }

    public String getSecretMessage() {
        return secretMessage;
    }

    public int getAgents() {
        return agents;
    }

    public long getTaskSize() {
        return taskSize;
    }
}
