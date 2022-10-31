package examples.annotations.thundercats;

import examples.annotations.ThunderCatSkill;

@ThunderCatSkill(skill = "Fast")
public class Zitara extends ThunderCat {

    public Zitara() {
        super("Zitara", Gender.FEMALE);
    }
}
