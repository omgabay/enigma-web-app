package bruteforce;

import java.util.ArrayList;
import java.util.List;



public class AdvancedIncrementor extends SimpleIncrementor{
    private final int reflectorCount;
    public AdvancedIncrementor(List<Integer> rotors, int base, int reflectorCount){
        super(base, rotors.size(), rotors);
        // Machine Specific variables

        this.reflectorCount = reflectorCount;
    }

    public List<Integer> getRotors() {
        return rotors;
    }


    public List<AgentJob> getNewJobs(){
        List<AgentJob> jobs = new ArrayList<>();
        long taskDuration = this.getTaskDuration();

        if(taskDuration == 0){
            return jobs;
        }

        List<Integer> positions = this.getPositions();
        for (int reflectorID = 0; reflectorID < this.reflectorCount; reflectorID++) {
            // swaping reflectors
            jobs.add(new AgentJob(this.rotors, positions , reflectorID , taskDuration, ++jobCount));

        }
        return jobs;
    }




}
