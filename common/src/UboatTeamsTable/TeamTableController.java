package UboatTeamsTable;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import users.AgentEntry;
import users.AllyTeam;
import utils.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

public class TeamTableController implements Closeable {


    @FXML TableView<AllyTeam> teamsTableView;
    @FXML TableColumn<AllyTeam, String> teamNameCol;
    @FXML TableColumn<AllyTeam, Integer> agentCountCol;
    @FXML TableColumn<AllyTeam, Integer> missionSizeCol;
    @FXML
    private Timer timer;






    public void initialize(){
        // Teams Table
        teamNameCol.setCellValueFactory(
                cellData -> cellData.getValue().NameProperty()
        );
        agentCountCol.setCellValueFactory(
                cellData -> cellData.getValue().getAgentCount()
        );
        missionSizeCol.setCellValueFactory(
                cellData -> cellData.getValue().missionSizeProperty()
        );

    }


    public void startTeamRefresher(String uboatName){
        TeamTableRefreshTask refreshTask = new TeamTableRefreshTask(uboatName, this::updateTable, timer);
        timer = new Timer();
        timer.schedule(refreshTask, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }


    public void updateTable(List<AllyTeam> teamList){
        this.teamsTableView.getItems().clear();
        this.teamsTableView.getItems().addAll(teamList);
        // this is a test
        //AllyTeam team = new AllyTeam("omer");
        //team.addAgentToTeam(new AgentEntry("moshe", "omer", 800, 3));
        //this.teamsTableView.getItems().add(team);
    }

    @Override
    public void close() throws IOException {
        this.timer.cancel();
    }
}
