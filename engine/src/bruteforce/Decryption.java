package bruteforce;

import auxiliary.MachineInfo;
import machine.IEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class Decryption {

    private LinkedList<Incrementor> incrementorList;
    enum Difficulty {EASY, MEDIUM, HARD, INSANE};



    private final int reflectorCount;
    private final int rotorCount;
    private final int alphabetSize;

    private final List<Integer> machineRotors;

    private long TASK_SIZE = 900;


    private IEngine engine;




    public Decryption(IEngine engine, Difficulty difficulty, long taskSize){
        this.reflectorCount = engine.getNumOfReflectors();
        this.rotorCount = engine.getTotalAvailableRotors();
        this.alphabetSize = engine.getMachineAlphabet().size();
        this.machineRotors = engine.getMachine().getRotorIDs();
        this.incrementorList = new LinkedList<>();
        createIncrementors(difficulty);
        Incrementor.setJobSize(taskSize);
    }


    public List<AgentTask> fetchAgentTasks(long numOfTasks){
        List<AgentTask> agentTasks = new ArrayList<>();
        while(agentTasks.size() < numOfTasks || incrementorList.isEmpty()){

            if(incrementorList.isEmpty()){
                break;
            }
            Incrementor incrementor = incrementorList.removeFirst();
            agentTasks.addAll(incrementor.getNewJobs());
            if(!incrementor.finished()){
                incrementorList.addLast(incrementor);
            }

        }
        return agentTasks;

    }

    private void createIncrementors(Difficulty difficulty) {
        switch(difficulty){
            case EASY: default:
                createIncrementorsLevelEasy();
                break;
            case MEDIUM:
                createIncrementorsLevelMedium();
                break;
            case HARD:
                createIncrementorsLevelHard();
                break;
            case INSANE:
                createIncrementorsLevelCrazy();
                break;
        }
    }


    private void createIncrementorsLevelEasy() {
        this.incrementorList.add(new Incrementor(alphabetSize,machineRotors.size(), machineRotors));
    }




    private void createIncrementorsLevelMedium() {
        MachineInfo info = (MachineInfo) engine.getMachineInfo().getData();
        List<Integer> ids = info.getRotorIDs();


        this.incrementorList.add(new AdvancedIncrementor(ids,alphabetSize,reflectorCount));
    }

    private void createIncrementorsLevelHard(){
        MachineInfo info = (MachineInfo) engine.getMachineInfo().getData();
        List<Integer> ids = info.getRotorIDs();
        List<List<Integer>> permutations = getAllPermutations(ids);

        System.out.println("permutation: " + permutations);

        System.out.println("Number of permutation is " + permutations.size());

        for (List<Integer> rotorPermutation   : permutations) {
            this.incrementorList.add(new AdvancedIncrementor(rotorPermutation,alphabetSize,reflectorCount));
        }
    }


    private void createIncrementorsLevelCrazy() {
        MachineInfo info = (MachineInfo) engine.getMachineInfo().getData();
        int selectedRotors = info.getRotorIDs().size();
        int totalRotors = engine.getTotalAvailableRotors();

        for (List<Integer>  rotors  : subsets(totalRotors , selectedRotors)){
            List<List<Integer>> permutations = getAllPermutations(rotors);
            for (List<Integer> rotorPermutation   : permutations) {
                this.incrementorList.add(new AdvancedIncrementor(rotorPermutation,alphabetSize,reflectorCount));
            }
        }
    }


    private static List<List<Integer>> getAllPermutations(List<Integer> list) {

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
    private List<List<Integer>> subsets(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(n, k, 1, result, new ArrayList<>());
        return result;
    }

    private void backtrack(int n, int k, int startIndex, List<List<Integer>> result, List<Integer> partialList) {
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
