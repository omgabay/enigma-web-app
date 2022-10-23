package controllers.bruteforce;
import bruteforce.DecryptionManger;
import bruteforce.JobResultHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import machine.IEngine;
import model.MachineModel;


public class BruteforceTask extends Task<Boolean> {

    ObservableList<String> solutions;
    private UIAdapter adapter;
    private DecryptionManger DM;
    private Thread dmThread;



    long taskCompleted;
    long totalTasks;


    // Task information:
    private final int agents;
    private final long taskSize;
    private int difficulty;
    private String secretMessage;


    private boolean isPaused;



    public BruteforceTask(BruteforceUserInput input,UIAdapter adapter){

        this.agents = input.getAgents();
        this.taskSize = input.getTaskSize();
        this.difficulty = input.getDifficulty();
        this.secretMessage = input.getSecretMessage();
        solutions = FXCollections.observableArrayList();


        this.adapter = adapter;

        this.totalTasks = 100;
        this.taskCompleted = 0;

        this.isPaused = false;

    }



    @Override
    protected Boolean call() throws Exception {

        // Creating Result Handler
        JobResultHandler handler = new JobResultHandler(this::foundNewSolution, this::updateProgress);

        IEngine engine = MachineModel.getInstance().getEngine();
        this.DM = new DecryptionManger(this.agents,this.difficulty,this.taskSize ,engine,handler,secretMessage);
        this.totalTasks = DM.getNumberOfTasks();
        System.out.println("Number of tasks is " + this.totalTasks);

        startDM();
        updateMessage("Starting Decryption Manager");


        while(this.taskCompleted < this.totalTasks && dmThread.isAlive()){
            Thread.sleep(5000);

            if(isPaused){
                this.DM.pauseBruteForce();
            }
            System.out.println("Javafx got solutions " + solutions + " Task Completed: " + this.taskCompleted);
        }

        System.out.println("JavaFX task is saying bye bye");
        System.out.println("Javafx got solutions " + solutions);

        return Boolean.TRUE;
    }




    public void foundNewSolution(BruteforceSolution possibleSolution){
        solutions.add(possibleSolution.getSolutionCandidate());
        adapter.solutionFound(possibleSolution);

    }


    public void updateProgress(Long completed) {
        if(totalTasks == 0){
            return;
        }

        this.updateMessage("Completed " + completed + " tasks out of " + this.totalTasks +".");
        this.taskCompleted = completed;
        this.updateProgress(completed,this.totalTasks);
    }

    public void pauseTask() {
        this.DM.pauseBruteForce();

    }

    public void resumeTask() {
        this.DM.resumeBruteForce();
    }
    private void startDM() {
        this.dmThread = new Thread(DM);
        dmThread.setDaemon(true);
        dmThread.start();
    }


}
