package users;

import bruteforce.AgentSolutionEntry;
import bruteforce.BruteforceSolutionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import jaxb.generated.CTEEnigma;
import machine.Engine;
import machine.IEngine;
import bruteforce.Difficulty;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;


public class UBoat extends User{


    // Battle Related Fields:
    private String battleName;
    private int maximumTeams;

    //private GameStatus status;

    private bruteforce.Difficulty difficulty;


    private final BruteforceSolutionManager solutionManager;

    private final List<AgentSolutionEntry> contestCandidateSolutions;



    private CTEEnigma machine;
    private List<AllyTeam> teams;

    // Contest related fields
    private String secretMessage = null;
    private AgentEntry winner = null;

    private boolean isReady = false;

    private int teamsReady;



    public UBoat(String name){
        super(name, ClientType.UBOAT);
        machine = null;
        // Create an empty list of ally teams
        teams = new ArrayList<>();
        solutionManager = new BruteforceSolutionManager();
        contestCandidateSolutions = new ArrayList<>();
        secretMessage = null;
        difficulty = Difficulty.LOAD_ENIGMA;
        this.maximumTeams = 0;
        this.teamsReady = 0;
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

    private void createDMs() {
        if(this.machine == null){
            return;
        }
        IEngine engine = new Engine();
        engine.loadFromJson(GSON_INSTANCE.toJson(this.machine));
        for (AllyTeam team : this.teams
        ) {
            team.createTeamDM(engine,this.difficulty);
        }

    }


    public List<AgentSolutionEntry> getSolutionsWithVersion(int version){
        List<AgentSolutionEntry> response = new ArrayList<>();

        if(version < 0){ version = 0;}
        for(int i=version; i<this.contestCandidateSolutions.size(); i++){
            response.add(contestCandidateSolutions.get(i));
        }
        return response;
    }



    public BruteforceSolutionManager getSolutionsManager(){
        return this.solutionManager;
    }

    public List<AgentSolutionEntry> getAllCandidates() {
        return solutionManager.getAllSolutions();
    }


    public void addAgentSolution(AgentSolutionEntry agentSolution){
            this.contestCandidateSolutions.add(agentSolution);
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

    public ObservableValue<String> TeamsRegisteredProperty() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.teams.size()).append("/").append(this.maximumTeams).append(" registered");
        return new SimpleStringProperty(sb.toString());
    }

    public String getSecretMessage() {
        return this.secretMessage;
    }
    public boolean isReady(){
        return isReady;
    }

    public boolean setReady(){
        if(isReady){
            return false;
        }
        for (AllyTeam myTeam : this.teams) {
            if(!myTeam.isReady){
                return false;
            }
        }
        this.isReady = true;
        createDMs();
        startAgents();
        return true;
    }

    private void startAgents() {
        for (AllyTeam team : this.teams) {
            for (AgentEntry agent  : team.agentList) {
                agent.setReady(true);
            }
        }
    }

    public void teamIsReady(String teamName){
        for (AllyTeam ally : this.teams) {
            if(ally.getName().equals(teamName) && !ally.isReady){
                ally.setReady(true);
                this.teamsReady++;
            }
        }
        if(teamsReady == this.maximumTeams){
            this.isReady = true;
            this.createDMs();
        }

    }






    public void uboatLogout(){
        this.teams = new ArrayList<>();
        this.winner = null;
        this.secretMessage = null;
        this.isReady = false;
    }


    public String getDifficulty() {
        switch(this.difficulty){
            case EASY:
                return "Easy";
            case MEDIUM:
                return "Medium";
            case HARD:
                return "Hard";
            case INSANE:
                return "Insane";

        }
        return "";
    }
}
