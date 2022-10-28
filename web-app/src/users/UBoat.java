package users;

import bruteforce.AgentSolutionEntry;
import bruteforce.BruteforceSolutionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import jaxb.generated.CTEEnigma;
import machine.Engine;
import machine.IEngine;

import java.util.ArrayList;
import java.util.List;


public class UBoat extends User{


    // Battle Related Fields:
    private String battleName;
    private int maximumTeams;

    private GameStatus status;

    private Difficulty difficulty;



    private BruteforceSolutionManager solutionManager;



    private CTEEnigma machine;
    private List<AllyTeam> teams;

    // Contest related fields
    private String secretMessage;
    private AgentEntry winner = null;






    public UBoat(String name){
        super(name, ClientType.UBOAT);
        machine = null;
        teams = new ArrayList<>();
        solutionManager = new BruteforceSolutionManager();
        secretMessage = null;
        difficulty = Difficulty.EASY;
    }

    public void addTeam(AllyTeam ally){
        if(teams.size() < maximumTeams){
            teams.add(ally);
        }
    }




    public void setMachine(CTEEnigma enigma){
        this.machine = enigma;
        this.battleName = enigma.getCTEBattlefield().getBattleName();
        this.maximumTeams = enigma.getCTEBattlefield().getAllies();
        this.winner = null;
        // Update difficulty level of Brute Force Task
        switch(enigma.getCTEBattlefield().getLevel()){
            case "Easy": default:
                difficulty = Difficulty.EASY;
                break;
            case "Medium":
                difficulty = Difficulty.MEDIUM;
                break;
            case "Hard":
                difficulty = Difficulty.HARD;
                break;
            case "Insane":
                difficulty = Difficulty.INSANE;
                break;
        }
    }

    public CTEEnigma getMachine(){
        return machine;
    }




    public int getAllies() {
        return this.machine.getCTEBattlefield().getAllies();
    }

    public void setSecretMessage(String secret) {
        this.secretMessage = secret;
    }

    public void createDMs() {
        IEngine engine = new Engine();
        for (AllyTeam team : this.teams) {


        }

    }


    public BruteforceSolutionManager getSolutionsManager(){
        return this.solutionManager;
    }

    public List<AgentSolutionEntry> getAllCandidates() {
        return solutionManager.getAllSolutions();
    }



    public List<AgentSolutionEntry> getAgentCandidates(String agentName){
        List<AgentSolutionEntry> allCandidates = this.solutionManager.getAllSolutions();
        List<AgentSolutionEntry> result = new ArrayList<>();
        for (AgentSolutionEntry solutionEntry :  allCandidates) {
            if(solutionEntry.getAgentName().equals(agentName)){
                result.add(solutionEntry);
            }
        }
        return result;
    }



    public List<AgentSolutionEntry> getTeamCandidates(String teamName){
        return null;
    }

    public List<AllyTeam> getAllyTeams() {
        return this.teams;
    }

    public synchronized void updateWinner(AgentEntry theWinner) {
        this.winner = theWinner;
    }


    public ObservableValue<String> BattleNameProperty() {
        return new SimpleStringProperty(this.battleName);
    }

    public ObservableValue<String> DifficultyProperty() {
        return new SimpleStringProperty(this.difficulty.name());
    }

    public ObservableValue<String> TeamsRegisterdProperty() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.teams.size()).append("/").append(this.maximumTeams).append(" registered");
        return new SimpleStringProperty(sb.toString());
    }
}
