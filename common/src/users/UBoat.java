package users;

import jaxb.generated.CTEEnigma;

import java.util.List;

;
public class UBoat extends User{


    // Battle Related Fields:
    private String battleName;
    private int maximumTeams;

    private GameStatus status;

    private Difficulty difficulty;







    private CTEEnigma machine;
    private List<AllyTeam> competingTeams;




    public UBoat(String name){
        super(name, ClientType.UBOAT);
        machine = null;
    }

    public void addTeam(AllyTeam ally){
        if(competingTeams.size() < maximumTeams){
            competingTeams.add(ally);
        }
    }




    public void setMachine(CTEEnigma enigma){
        this.machine = enigma;
        this.battleName = enigma.getCTEBattlefield().getBattleName();
        this.maximumTeams = enigma.getCTEBattlefield().getAllies();
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



    // here is the problem
    public int getAllies() {
        return this.machine.getCTEBattlefield().getAllies();
    }
}
