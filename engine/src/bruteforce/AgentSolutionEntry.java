package bruteforce;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class AgentSolutionEntry {

    private final String machineCode;
    private final String agentName;

    private final String candidate;



    public AgentSolutionEntry(String agentName, String candidate, List<Integer> rotors, List<Character> positions, String reflector){
        this.candidate = candidate;
        this.agentName = agentName;
        this.machineCode = buildMachineCodeString(rotors,positions,reflector);
    }


    private String buildMachineCodeString(List<Integer> rotors,List<Character> positions, String reflector){
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < rotors.size(); i++) {
            if(i == 0){
                sb.append("<").append(rotors.get(i));
            }else{
                sb.append(",").append(rotors.get(i));
            }
        }
        sb.append(">");
        for (int i = 0; i < positions.size(); i++) {
            if(i == 0){
                sb.append("<").append(positions.get(i));
            }else{
                sb.append(",").append(positions.get(i));
            }
        }
        sb.append(">");
        sb.append("<").append(reflector).append(">");

        return sb.toString();

    }

    public String getAgentName(){
        return this.agentName;
    }


    public String getCandidate(){
        return candidate;
    }

}
