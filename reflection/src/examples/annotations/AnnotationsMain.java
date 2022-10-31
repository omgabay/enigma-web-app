package examples.annotations;

import examples.annotations.thundercats.Liono;
import examples.annotations.thundercats.Pantro;
import examples.annotations.thundercats.ThunderCat;
import examples.annotations.thundercats.Zitara;

import java.util.ArrayList;
import java.util.List;

public class AnnotationsMain {

    public static void main(String[] args) {
        List<ThunderCat> thunderCats = new ArrayList<>();
        thunderCats.add(new Zitara());
        thunderCats.add(new Pantro());
        thunderCats.add(new Liono());

        for (ThunderCat aCat : thunderCats) {
            String skill = aCat.getClass().getDeclaredAnnotation(ThunderCatSkill.class).skill();
            printCat(aCat, skill);
        }
    }

    private static void printCat(ThunderCat aCat, String skill) {
        System.out.println("Thunder Cat " + aCat.getName() + " (" + aCat.getGender() + ") with skill of " + skill + " ahhhhhhhhhhhhhhhhhh !!!");
    }
}
