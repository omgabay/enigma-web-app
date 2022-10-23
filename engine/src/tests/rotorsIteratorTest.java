package tests;

import machine.Engine;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class rotorsIteratorTest {
    public static void main(String[] args) throws JAXBException {
        Engine engine = new Engine();
        File xml = new File("C:\\Users\\omerg\\IdeaProjects\\Enigma\\gui-app\\src\\resources\\input-xml\\ex2-basic.xml");
        engine.loadMachineFromXml(xml);
        HashMap<Character,Character> plugs = new HashMap<>();
        List<Integer> rotorIDs = new ArrayList<>();
        int [] ids = {1,2,3};
        for(int id : ids){
            rotorIDs.add(id);
        }
        char [] positions = {'A','A','A'};
        List<Character> rotorPositions = new ArrayList<>();
        for(char pos : positions){
            rotorPositions.add(pos);
        }

        engine.setupMachine(rotorIDs, rotorPositions, "I",plugs);

        Iterator<List<Integer>> it  = engine.getMachine().getRotorsIterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }

    }
}
