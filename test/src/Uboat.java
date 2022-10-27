import jaxb.generated.CTEEnigma;

import java.util.List;

public class Uboat extends User{

    private String battlefieldName;

    private CTEEnigma machine;
    private List<AllyTeam> competingTeams;


    public Uboat(String name){
        super(name);
        machine = null;
    }



    public void addTeam(AllyTeam myTeam){
        competingTeams.add(myTeam);
    }



    public void setMachine(CTEEnigma machine){
        this.machine = null;
    }

    public CTEEnigma getMachine(){
        return machine;
    }






}
