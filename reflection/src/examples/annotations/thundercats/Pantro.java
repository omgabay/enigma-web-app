package examples.annotations.thundercats;

import examples.annotations.ThunderCatSkill;

@ThunderCatSkill(skill = "Strong")

public class Pantro extends ThunderCat {

    public Pantro() {
        super("Pantro", Gender.MALE);
    }
}
