public class User {

    public final String username;
    enum ClientType {UBOAT, ALLY, AGENT};
    ClientType type;


    public User(String name){
        username = name;
    }

    public User(String name, ClientType type){
        username = name;
        this.type = type;
    }
}
