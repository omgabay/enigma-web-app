package logic.bruteforce;

import auxiliary.MachineInfo;
import machine.IEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


// Decryption will be set to daemon thread - runs in the background
public class DM{



    private final LinkedList<SimpleIncrementor> machineIncrementors;
    private final long JOB_SIZE;


    private boolean isPaused;

    IEngine engine;

// Machine related fields
    private final int alphabetSize;
    private final int reflectorCount;

    private boolean winnerFound;





    public DM(int difficulty, long JOB_SIZE, IEngine engine, String secret){



        this.engine = engine;
        this.reflectorCount = engine.getNumOfReflectors();


        this.machineIncrementors = new LinkedList<>();
        this.alphabetSize = engine.getMachineAlphabet().size();

        this.winnerFound = false;
        this.JOB_SIZE = JOB_SIZE;

        createIncrementors(difficulty);



    }

    public List<AgentTask>  fetchTasks(int numberOfTasks){
        if(winnerFound){
            return null;
        }
        List<AgentTask> tasks = new ArrayList<>();

        while(!machineIncrementors.isEmpty() && numberOfTasks > 0){
            SimpleIncrementor incrementor = machineIncrementors.removeFirst();
            numberOfTasks--;
        }



        return tasks;
    }



    public void run() {


                System.out.println("Decryption manger got " +machineIncrementors.size() +" Incrementors in the list");
                System.out.println("Number of tasks is " + this.getNumberOfTasks());


                while (!this.machineIncrementors.isEmpty()) {
                    // Remove Incrementor(Counter) from the list
                    SimpleIncrementor incrementor = machineIncrementors.removeFirst();
                    long currentCount = incrementor.getValue();




                    System.out.println("Incrementor value: " + currentCount);


                    List<AgentTask> agentJobs = incrementor.getNewJobs();
                    if(agentJobs == null){
                        continue;
                    }

                    // Moving the popped incrementor to the end of the list
                    if(!incrementor.finished()) {
                        machineIncrementors.addLast(incrementor);
                    }


                    for (AgentTask job : agentJobs) {
                            System.out.println(job);
                    }

                }


        }




    private void createIncrementorsLevelEasy() {
        MachineInfo info = (MachineInfo) engine.getMachineInfo().getData();
        List<Integer> ids = info.getRotorIDs();
        this.machineIncrementors.add(new SimpleIncrementor(alphabetSize, ids.size(), ids));
    }




    private void createIncrementorsLevelMedium() {
        MachineInfo info = (MachineInfo) engine.getMachineInfo().getData();
        List<Integer> ids = info.getRotorIDs();


        this.machineIncrementors.add(new AdvancedIncrementor(ids,alphabetSize,reflectorCount));
    }

    private void createIncrementorsLevelHard(){
        MachineInfo info = (MachineInfo) engine.getMachineInfo().getData();
        List<Integer> ids = info.getRotorIDs();
        List<List<Integer>> permutations = getAllPermutations(ids);

        System.out.println("permutation: " + permutations);

        System.out.println("Number of permutation is " + permutations.size());

        for (List<Integer> rotorPermutation   : permutations) {
            this.machineIncrementors.add(new AdvancedIncrementor(rotorPermutation,alphabetSize,reflectorCount));
        }
    }


    private void createIncrementorsLevelCrazy() {
        MachineInfo info = (MachineInfo) engine.getMachineInfo().getData();
        int selectedRotors = info.getRotorIDs().size();
        int totalRotors = engine.getTotalAvailableRotors();

        for (List<Integer>  rotors  : subsets(totalRotors , selectedRotors)){
            List<List<Integer>> permutations = getAllPermutations(rotors);
            for (List<Integer> rotorPermutation   : permutations) {
                this.machineIncrementors.add(new AdvancedIncrementor(rotorPermutation,alphabetSize,reflectorCount));
            }
        }

    }



    public static List<List<Integer>> getAllPermutations(List<Integer> list) {

        if (list.size() == 0) {
            List<List<Integer>> result = new ArrayList<List<Integer>>();
            result.add(new ArrayList<Integer>());
            return result;
        }

        List<List<Integer>> returnMe = new ArrayList<List<Integer>>();

        Integer firstElement = list.remove(0);

        List<List<Integer>> recursiveReturn = getAllPermutations(list);
        for (List<Integer> li : recursiveReturn) {

            for (int index = 0; index <= li.size(); index++) {
                List<Integer> temp = new ArrayList<Integer>(li);
                temp.add(index, firstElement);
                returnMe.add(temp);
            }

        }
        return returnMe;
    }




    private void createIncrementors(int difficulty) {
        switch(difficulty){
            case 1:
                createIncrementorsLevelEasy();
                break;
            case 2:
                createIncrementorsLevelMedium();
                break;
            case 3:
                createIncrementorsLevelHard();
                break;
            case 4:
                createIncrementorsLevelCrazy();
                break;
            default:
                System.out.println("Unknown difficulty level");
                break;
        }
    }




    public long getNumberOfTasks(){
        long tasksPerConfiguration = machineIncrementors.get(0).getMaxValue() / JOB_SIZE;
        if(tasksPerConfiguration % JOB_SIZE != 0){
            tasksPerConfiguration += 1;
        }
        return tasksPerConfiguration * machineIncrementors.size();
    }



    private List<List<Integer>> subsets(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(n, k, 1, result, new ArrayList<>());
        return result;
    }

    private void backtrack(int n, int k, int startIndex, List<List<Integer>> result,
                          List<Integer> partialList) {
        if (k == partialList.size()) {
            result.add(new ArrayList<>(partialList));
            return;
        }
        for (int i = startIndex; i <= n; i++) {
            partialList.add(i);
            backtrack(n, k, i + 1, result, partialList);
            partialList.remove(partialList.size() - 1);
        }
    }


}



