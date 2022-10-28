package bruteforce;

import machine.Engine;
import machine.IEngine;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class bruteforceTest {
    public static void main(String[] args) throws JAXBException, FileNotFoundException {
        IEngine engine = new Engine();
        File xml = new File("C:\\Users\\omerg\\IdeaProjects\\Enigma\\uboat\\src\\resources\\enigma-xml\\ex3-basic.xml");
        engine.loadMachineFromXml(xml);
        int [] ids = [1,2,4]
        int [] positionArr = [0,0,0];
        List<Integer> rotors = new ArrayList<>();
        for (int id:ids) {
            rotors.add(id);
        }


        engine.setupMachine()
    }



    public static void printSolutions(List<AgentSolutionEntry> solutionEntryList){
        for (AgentSolutionEntry solution : solutionEntryList) {
            System.out.print(solution.getCandidate());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
