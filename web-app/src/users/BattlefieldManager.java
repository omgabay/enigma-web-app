package users;

import java.util.ArrayList;
import java.util.List;

public class BattlefieldManager {

    enum Status {REGISTRATION, WAITING_FOR_TEAMS, RUNNING, DONE };

    public class Battlefield{



        private UBoat uboat;
        private final String battleName;

        private final List<AllyTeam> teams;



        private final int maximumTeams;
        Status gameStatus;




        public Battlefield(int maximum){
            teams = new ArrayList<>();
            maximumTeams = maximum;
            gameStatus = Status.REGISTRATION;
            battleName = "Banana";
        }


        public void addTeam(AllyTeam ally){
            if(teams.size() < maximumTeams){
                teams.add(ally);
            }
        }

    }







}
