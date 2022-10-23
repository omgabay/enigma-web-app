package bruteforce;

import controllers.bruteforce.BruteforceSolution;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * JobResultHandler is responsible for updating the Brute-Force view with the results of the decryption
 * The agents write their Job Results to the Blocking Queue
 */
public class JobResultHandler implements Runnable {
    private final BlockingQueue<JobResult> resultsQueue;

    // Consumers
    private final Consumer<Long> updateProgress;
    private final Consumer<BruteforceSolution> solutionConsumer;
    private static final int QUEUE_SIZE = 100000;

    private long tasksCompleted;

    private boolean canceled;

    private boolean wasPaused;

    private final Object pauseLock;



    public JobResultHandler(Consumer<BruteforceSolution> solutionConsumer , Consumer<Long> updateTaskProgress){
        // Creating the Result Queue where the agents will push their job result objects
        resultsQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

        // Consumer that accepts solutions found by the agents
        this.solutionConsumer = solutionConsumer;

        // Consumer that accepts updates in the progress of the task
        this.updateProgress = updateTaskProgress;

        this.tasksCompleted = 0 ;

        canceled = false;
        pauseLock = new Object();
    }



    @Override
    public void run() {
        while(!canceled || resultsQueue.size() > 0){
            try {
                JobResult result = resultsQueue.poll(1000, TimeUnit.MILLISECONDS);
                if(result == null){
                    continue;
                }
                synchronized (pauseLock){
                    if(wasPaused){
                        pauseLock.wait();
                        System.out.println("JobResultHandler was paused");
                    }


                    if(!result.getSolutionCandidate().isEmpty()){
                        //this.solutionConsumer.accept(result.getSolutionCandidate());
                        System.out.print("handler found interesting string " + result.getSolutionCandidate());
                        System.out.println("\t configuration = " + result.getRotorSetup());
                        System.out.println("\t Rotor Positions = " + result.getRotorsPosition());
                        for (String solution : result.getSolutionCandidate()) {
                            BruteforceSolution bfs = new BruteforceSolution(result.getAgentID(), solution);
                            solutionConsumer.accept(bfs);
                        }
                    }


                    tasksCompleted++;
                    System.out.println("Job Handler received job result number " + tasksCompleted +" Solutions = " + result.getSolutionCandidate());
                    updateProgress.accept(this.tasksCompleted);


                }


            } catch (InterruptedException e) {
                System.out.println("JobResultHandler was interrupted");
                return;
            }catch(NullPointerException e){
                System.out.println("JobResultHandler Null Pointer!");
            }
        }
        System.out.println("Handler is done bye");
    }




    public BlockingQueue<JobResult> getResultsQueue(){
        return this.resultsQueue;
    }

    public void pause(){
        wasPaused = true;
    }

    public void cancelResultHandler(){
        canceled = true;
    }

    public void continueTask(){
        synchronized (this.pauseLock) {
            pauseLock.notifyAll();
            System.out.println("JobResult Handler was continued");
        }
    }



}
