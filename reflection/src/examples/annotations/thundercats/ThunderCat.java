package examples.annotations.thundercats;


public abstract class ThunderCat {

    public static enum Gender {MALE, FEMALE}

    private String name;

    private Gender gender;

    public ThunderCat(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }
}
