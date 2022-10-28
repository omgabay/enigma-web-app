package bruteforce;

import java.util.ArrayList;
import java.util.List;

public class BruteforceSolutionManager {
    private final List<AgentSolutionEntry> solutionEntryList;


    public BruteforceSolutionManager(){
        solutionEntryList = new ArrayList<>();
    }

    public synchronized void addCandidateSolution(AgentSolutionEntry ase){
        solutionEntryList.add(ase);
    }



    public synchronized List<AgentSolutionEntry> getCandidateSolutionFromIndex(int fromIndex){
        if (fromIndex < 0 || fromIndex > solutionEntryList.size()) {
            return getAllSolutions();
        }
        return solutionEntryList.subList(fromIndex, solutionEntryList.size());

    }

    public List<AgentSolutionEntry> getAllSolutions(){
        return solutionEntryList;
    }




    public int getVersion() {return solutionEntryList.size(); }
}
