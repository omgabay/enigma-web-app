package candidateTable;

import bruteforce.AgentSolutionEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import utils.Constants;

import java.util.List;
import java.util.Timer;

public class CandidateTableController {

    @FXML TableView<AgentSolutionEntry>  candidateTable;
    @FXML TableColumn<AgentSolutionEntry,String> candidateCol;
    @FXML TableColumn<AgentSolutionEntry, String> agentNameCol;
    @FXML TableColumn<AgentSolutionEntry,String> teamNameCol;
    @FXML TableColumn<AgentSolutionEntry,String> machineCodeCol;

    private Timer timer;

    private IntegerProperty version = new SimpleIntegerProperty(0);


    public void initialize(){
        candidateCol.setCellValueFactory(
                cellData -> cellData.getValue().CandidateProperty()
        );

        agentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().AgentNameProperty()
        );

        teamNameCol.setCellValueFactory(
                cellData -> cellData.getValue().TeamNameProperty()
        );

        machineCodeCol.setCellValueFactory(
                cellData -> cellData.getValue().MachineCodeProperty()
        );


        AgentSolutionEntry ase = new AgentSolutionEntry("omer", "rotem", "secret", "<A<B<C>");
        candidateTable.getItems().add(ase);
        startRefreshTask("omerg");

    }


    public void addAgentSolutions(List<AgentSolutionEntry> solutionEntryList){
        int newVal = version.get() + solutionEntryList.size();
        version.set(newVal);
        candidateTable.getItems().addAll(solutionEntryList);
    }


    public void startRefreshTask(String uboatName){
        RefreshCandidateTableTask task = new RefreshCandidateTableTask(uboatName,this::addAgentSolutions,version);
        timer = new Timer();
        timer.schedule(task,1000, Constants.REFRESH_RATE);
    }





}
