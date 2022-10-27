package users;

import java.util.*;

public class UserManager {
    private final Map<String, User> mapNameToUser;

    private final  List<String> teams;

    private final Map<String, AllyTeam> mapAllyTeams;

    private final List<String> uboats;




    public UserManager() {
        mapNameToUser = new HashMap<>();
        teams = new ArrayList<>();
        uboats = new ArrayList<>();
        mapAllyTeams = new HashMap<>();

    }

    public synchronized void addUser(String username, User user) {
        mapNameToUser.put(username, user);
        switch(user.type){
            case AGENT:
                AgentEntry agent = (AgentEntry) user;
                String teamName = agent.getTeamName();
                if(mapNameToUser.containsKey(teamName)){
                    if(mapNameToUser.get(teamName) instanceof AllyTeam){
                        AllyTeam team = (AllyTeam) mapNameToUser.get(teamName);
                        team.addAgentToTeam(agent);
                    }
                }
                break;
            case ALLY:
                System.out.println("welcome team " + username +"!");
                teams.add(username);
                mapAllyTeams.put(username, (AllyTeam) user);
                break;
            case UBOAT:
                System.out.println("welcome uboat " + username +"!");
                uboats.add(username);
        }
    }

    public synchronized void removeUser(String username) {
        mapNameToUser.remove(username);
    }

    public synchronized List<User> getUsers() {
        return new ArrayList<>(mapNameToUser.values());
    }

    public boolean isUserExists(String username) {
        return mapNameToUser.containsKey(username);
    }

    public User getUser(String name) {
        if(name == null){
            return null;
        }
        return mapNameToUser.get(name);
    }


    public UBoat getUboat(String name){
        User uboat = mapNameToUser.get(name);
        if(uboat instanceof UBoat){
            return (UBoat) uboat;
        }
        return null;
    }

    public List<String> getTeamsList() {
        return this.teams;
    }

    public AllyTeam getTeam(String teamName) {
        User user = this.mapNameToUser.get(teamName);
        if(!(user instanceof AllyTeam)){
            return null;
        }
        return (AllyTeam) user;
    }
}
