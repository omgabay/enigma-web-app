package bruteforce;

import auxiliary.Dictionary;
import auxiliary.Message;
import machine.Enigma;
import machine.IEngine;
import machine.parts.Reflector;
import machine.parts.Rotor;
import machine.parts.Rotors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AgentWorker implements Runnable {
    private final Enigma machine;
    private final List<Rotor> rotors;
    private final List<Reflector> reflectors;
    private final BlockingQueue<AgentTask> tasksQueue;
    private final Dictionary dictionary;
    private Consumer<List<AgentSolutionEntry>> solutionsConsumer;

    private String encodedMesage;
    private boolean gameIsRunning;



    public AgentWorker(IEngine engine, String encodedMsg, BlockingQueue<AgentTask> tasks, Consumer<List<AgentSolutionEntry>> sendSolutions){

        this.machine = engine.getMachine();
        this.rotors = engine.getRotorsFromEngine();
        this.reflectors = engine.getReflectorsFromEngine();
        this.dictionary = engine.getDictionary();
        this.encodedMesage = encodedMsg;
        this.solutionsConsumer = sendSolutions;
        this.tasksQueue = tasks;
        gameIsRunning = true;

    }

    @Override
    public void run() {
        while(gameIsRunning){

            // trying to Poll a new job for 3 seconds
            AgentTask task = null;
            try{
                task =tasksQueue.poll(3000, TimeUnit.MILLISECONDS);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            // Checking if polling from the queue was successful
            if(task == null){
                continue;
            }

            updateMachineSetup(task);




            // Getting ticks -- how many times to iterate on rotor combinations
            long ticks = task.getTicks();
            List<AgentSolutionEntry> solutions = new ArrayList<>();
            Iterator<List<Integer>> rotorsIterator = this.machine.getRotorsIterator();
            machine.setRotorPositions(task.getPositions());


            for(int i=0; i<ticks; i++){
                 // Saving rotor initial position
                List<Integer> savePositions = this.machine.getPositionsIndices();


                Message message = this.machine.processText(this.encodedMesage);

                // success is True when the processed text is a valid sentence
                String candidate = message.getProcessed();
                //System.out.println("candidate " + candidate);
                machine.setRotorPositions(savePositions);
                if(dictionary.checkCorrectness(candidate)){
                    AgentSolutionEntry solutionEntry = new AgentSolutionEntry("omer","blabla",candidate, machine.getCurrentConfiguration());
                    solutions.add(solutionEntry);
                }

                rotorsIterator.next();
            }
            this.solutionsConsumer.accept(solutions);



        }


    }






    private Rotor getRotorByIndex(int id) {
        for (Rotor rotor : this.rotors) {
            if(rotor.getID() == id){
                return rotor;
            }
        }
        return null;
    }

    private void updateMachineSetup(AgentTask task){
        // Getting the reflector from the task
        if(task.getReflector() >= 0){
            Reflector rafi = this.reflectors.get(task.getReflector());
            this.machine.swapReflector(rafi);
        }


        // Getting Rotors from the new task
        List<Integer>  jobRotorsIds = task.getRotorsIds();
        List<Rotor>  newRotorList = new ArrayList<>();
        try {
            for (int id : jobRotorsIds) {
                newRotorList.add(this.getRotorByIndex(id));
            }

            this.machine.setRotors(new Rotors(newRotorList));

        }catch(Exception e){
            System.err.println("Rotor was not found by Agent!!");
        }
    }
}
