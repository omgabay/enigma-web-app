package users;

import java.util.ArrayList;
import java.util.List;

import static users.User.ClientType.BATTLE;

public class Battlefield  extends User{

    private List<AllyTeam> teamsList;
    private UBoat uboat;
    int teamsCount;

    public Battlefield(String name, UBoat uboat) {
        super(name, BATTLE);
        teamsList = new ArrayList<>();
        this.uboat = uboat;
        this.teamsCount = uboat.getAllies();

    }


    public void addTeam(AllyTeam ally){
        if(teamsList.size() < teamsCount){
            teamsList.add(ally);
        }
    }
}
