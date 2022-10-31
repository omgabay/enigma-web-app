package bruteforce;

import auxiliary.Dictionary;
import auxiliary.Message;
import machine.Engine;
import machine.IEngine;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class bruteforceTest {
    public static void main(String[] args) throws JAXBException, FileNotFoundException, InterruptedException {
        IEngine engine = new Engine();
        File xml = new File("C:\\Users\\omerg\\IdeaProjects\\Enigma\\uboat\\src\\resources\\enigma-xml\\ex3-basic.xml");
        engine.loadMachineFromXml(xml);
        int [] ids = {1,2,4};
        char [] positionArr = {'A','C','D'};
        List<Integer> rotors = new ArrayList<>();
        for (int id:ids) {
            rotors.add(id);
        }
        List<Character> positions = new ArrayList<>();
        for(char pos : positionArr){
            positions.add(pos);
        }
        engine.setupMachine(rotors,positions,"I",new HashMap<>());

        Dictionary dictionary = engine.getDictionary();
        String word = dictionary.getWordsList().get(8) + " " +dictionary.getWordsList().get(2);
        System.out.println("the word to find is " + word);
        Message msg = (Message) engine.processText(word).getData();
        String secret = msg.getProcessed();
        Decryption dm = new Decryption(engine, Difficulty.INSANE,900);
        BlockingQueue<AgentTask> myTasks =  new ArrayBlockingQueue<>(1000);
        AgentWorker agentWorker = new AgentWorker(engine, secret, myTasks, bruteforceTest::printSolutions);
        new Thread(agentWorker).start();

        while(!dm.isDone()){
            List<AgentTask> agentTasks = dm.fetchAgentTasks(50);
            for (AgentTask task : agentTasks) {
                myTasks.put(task);
            }
        }


    }



    public static void printSolutions(List<AgentSolutionEntry> solutionEntryList){
        for (AgentSolutionEntry solution : solutionEntryList) {
            System.out.println(solution.getCandidate());
        }
    }
}
