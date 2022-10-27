package users;

public class User {

    public final String username;
    public enum ClientType {UBOAT, ALLY, AGENT, BATTLE};
    ClientType type;


    public User(String name){
        username = name;
    }

    public User(String name, ClientType type){
        username = name;
        this.type = type;
    }

    public String getName(){
        return username;
    }
}
