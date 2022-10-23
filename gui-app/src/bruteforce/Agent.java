package bruteforce;

import auxiliary.Dictionary;
import auxiliary.Message;
import machine.Enigma;
import machine.IEngine;
import machine.parts.Reflector;
import machine.parts.Rotor;
import machine.parts.Rotors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class Agent implements Runnable {


    private final Enigma machine;
    private final List<Rotor> rotors;
    private final List<Reflector> reflectors;
    private final BlockingQueue<AgentJob> jobsQueue;
    private final BlockingQueue<JobResult> resultsQueue;

    private final String agentName;
    private final int agentID;
    private static int agentCount = 0;


    private final String secretMessage;

    private final Dictionary dictionary;


    private boolean wasCanceled;
    private boolean paused;
    private final Object pauseLock;



    // Idea -- to pause the consumers (aka Agents) the producer(DM) will stop creating new Jobs. The DM will not read solutions

    public Agent(String name, IEngine engine, BlockingQueue<AgentJob> queue, BlockingQueue<JobResult> resultsQueue , String secretMessage){

        // Getting copy of the machine for agent
        machine = engine.getMachine();

        // Getting copy of rotors list
        this.rotors = engine.getRotorsFromEngine();

        // Getting reflectors
        this.reflectors = engine.getReflectorsFromEngine();


        // Blocking queues for agent
        jobsQueue = queue;
        this.resultsQueue = resultsQueue;

        this.dictionary =  engine.getDictionary();


        // setting agent identifiers
        agentName = name;
        this.agentID = ++agentCount;
        this.secretMessage = secretMessage;
        wasCanceled = false;
        paused = false;
        pauseLock = new Object();
    }


    @Override
    public void run() {


        while(!this.wasCanceled || !jobsQueue.isEmpty()) {

            //Fetching new Job from the queue
            AgentJob job  = null;


            try {
                synchronized (pauseLock) {
                    if (paused) {
                        pauseLock.wait();
                    }
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }


                // trying to Poll new job for 3 seconds
            try {
                job = jobsQueue.poll(3000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // Checking if polling from the queue was successful
            if(job == null){
                continue;
            }


            // Getting the reflector from the job
            if(job.getReflector() >= 0){
                Reflector rafi = this.reflectors.get(job.getReflector());
                this.machine.swapReflector(rafi);
            }


            // Setting Rotors
            List<Integer>  jobRotorsIds = job.getRotorsIds();
            List<Rotor>  newRotorList = new ArrayList<>();

            try {
                for (int id : jobRotorsIds) {
                newRotorList.add(this.getRotorByIndex(id));
                }

                this.machine.setRotors(new Rotors(newRotorList));

            }catch(Exception e){
                System.err.println("Rotor was not found by Agent!!");
            }


            // Getting ticks
            long ticks = job.getTicks();


            System.out.println("Agent "+ agentName +" received Job number " + job.getJobSequenceNumber());
            System.out.println("Agent is moving rotors to the positions "  + job.getPositions());

            boolean logging = false;
            if(logging) {
                System.out.println("Agent " + agentName + " is about to start a new job");
                System.out.println("Job number " + job.getJobSequenceNumber());

                System.out.println("moving rotors to positions: " + job.getPositions());
                System.out.println("Rotors setup is: " + job.getRotorsIds());
                System.out.println("Running for " + ticks + " ticks");
            }
            Iterator<List<Integer>>  rotorsIterator = this.machine.getRotorsIterator();
            machine.setRotorPositions(job.getPositions());

            List<String> solutions = new ArrayList<>();
            boolean success = false;
            for(int i=0; i<ticks; i++){

                // Processing secret message - let's be optimistic
                List<Integer> savePositions = this.machine.getPositionsIndices();


                Message message = this.machine.processText(secretMessage);

                // success is True when the processed text is a valid sentence
                if(dictionary.checkCorrectness(message.getProcessed())){
                    solutions.add(message.getProcessed());
                    success = true;
                }

                // Rotating the rotors one step - iterable interface
                this.machine.setRotorPositions(savePositions);
                rotorsIterator.next();
            }

            createAgentReply(solutions,job,success);
        }

            System.out.println(this.agentName + " is saying bye bye!");
    }

    private Rotor getRotorByIndex(int id) {
        for (Rotor rotor : this.rotors) {
            if(rotor.getID() == id){
                return rotor;
            }
        }
        return null;
    }


    private void createAgentReply(List<String> solutions, AgentJob job, boolean success) {
        JobResult result = new JobResult(this.agentName, solutions ,job, success);
        try {
            this.resultsQueue.put(result);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }




    public void cancelAgent(){
        wasCanceled = true;
    }


    public void pauseAgent(){
        this.paused = true;
    }

    public void unpauseAgent(){
        synchronized (this.pauseLock){
                pauseLock.notifyAll();
        }
    }




}
