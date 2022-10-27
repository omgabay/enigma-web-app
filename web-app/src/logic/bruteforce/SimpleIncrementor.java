package logic.bruteforce;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SimpleIncrementor {

    private final int base;
    private final int digits;
    private long currentValue;

    protected final List<Integer> rotors;

    private final long MAX_VALUE;

    private final long JOB_SIZE;

    protected static int jobCount;
    public SimpleIncrementor(int base, int digits,List<Integer> rotors) {
        this.base = base;
        this.currentValue =  0;
        this.digits = digits;
        this.rotors = rotors;
        MAX_VALUE = (long) Math.pow(base,digits);
        JOB_SIZE = (long) Math.pow(base,digits-1);
        jobCount = 0;
    }




    public long getValue(){
        return this.currentValue;
    }


    public boolean finished(){
        return this.currentValue >= this.MAX_VALUE;
    }


    protected long getTaskDuration() {
        if(currentValue >= this.MAX_VALUE){
            return 0;
        }
        long sum = currentValue + JOB_SIZE;
        this.currentValue = sum;
        if( sum > this.MAX_VALUE){
            return this.MAX_VALUE - currentValue;
        }
        return JOB_SIZE;
    }



    // Converts Counter to list of integers in base (rotor-size)
    public List<Integer> getPositions(){
        LinkedList<Integer> positions = new LinkedList<>();
        long x = this.currentValue;

        for(int i=0; i<digits; i++){
            int remainder = (int) x % this.base;
            positions.addLast(remainder);
            x = x / base;
        }
        return positions;

    }




    public List<AgentTask> getNewJobs(){
        List<Integer> rotorPositions = this.getPositions();
        long taskDuration = this.getTaskDuration();
        // Incrementor is done and need to get off the list
        if(taskDuration == 0){
            return null;
        }
        List<AgentTask> result = new ArrayList<>();
        result.add(new AgentTask(this.rotors, rotorPositions, -1, taskDuration, ++jobCount));
        return result;

    }


    public long getMaxValue() {
        return this.MAX_VALUE;
    }
}
