package controllers.bruteforce;

import javafx.beans.property.SimpleStringProperty;

public class BruteforceSolution {

    private SimpleStringProperty agentName;
    private SimpleStringProperty solutionCandidate;

    public String getAgentName() {
        return agentName.get();
    }

    public SimpleStringProperty agentNameProperty() {
        return agentName;
    }

    public String getSolutionCandidate() {
        return solutionCandidate.get();
    }

    public SimpleStringProperty solutionCandidateProperty() {
        return solutionCandidate;
    }

    public BruteforceSolution(SimpleStringProperty agentName, SimpleStringProperty solutionCandidate) {
        this.agentName = agentName;
        this.solutionCandidate = solutionCandidate;
    }

    public BruteforceSolution(String name, String solution){
        this.agentName = new SimpleStringProperty(name);
        this.solutionCandidate = new SimpleStringProperty(solution);
    }
}
