package examples.annotations.thundercats;

import examples.annotations.ThunderCatSkill;

@ThunderCatSkill(skill = "Leader !")
public class Liono extends ThunderCat {

    public Liono() {
        super("Liono", Gender.MALE);
    }
}
