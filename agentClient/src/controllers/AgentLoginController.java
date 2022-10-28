package controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AgentEntry;
import utils.requests.TeamListRefresher;
import utils.Configuration;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;

import static utils.Configuration.*;
import static utils.Constants.REFRESH_RATE;

public class AgentLoginController {

    private Stage stage;
    private Timer timer;

    @FXML TextField userText;
    @FXML TextField taskSizeInput;

    @FXML ToggleGroup workersSelection;

    @FXML ChoiceBox<String> teamSelection;

    @FXML Label errorMessageLabel;

    @FXML public void initialize(){
        TeamListRefresher teamListRefresher = new TeamListRefresher(teamNamesList -> {
            teamSelection.getItems().clear();
            teamSelection.getItems().addAll(teamNamesList);
        });
        timer = new Timer();
        timer.schedule(teamListRefresher,REFRESH_RATE*2,REFRESH_RATE);
    }


    public void setStage(Stage primaryStage){
        this.stage = primaryStage;

    }


    public void loginButtonClicked() {

        if(teamSelection.getSelectionModel().isEmpty()){
            // update error message and return
            this.errorMessageLabel.setText("You cant login without a team, wait if needed");
            return;
        }

        String username = userText.getText();
        String team =  teamSelection.getSelectionModel().getSelectedItem();

        long taskSize = Integer.parseInt(taskSizeInput.getText());
        if(taskSize <= 0){
            taskSize = 900;
        }




        RadioButton radioButton = (RadioButton) workersSelection.getSelectedToggle();
        int workers = Integer.parseInt(radioButton.getText());

        String finalUrl = HttpUrl
                .parse(Configuration.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", username)
                .addQueryParameter("type", "agent")
                .addQueryParameter("team", team)
                .addQueryParameter("workers", String.valueOf(workers))
                .addQueryParameter("taskSize", String.valueOf(taskSize))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String json = response.body().string();
                    AgentEntry agent = GSON_INSTANCE.fromJson(json,AgentEntry.class);
                    Platform.runLater(() ->
                            loadAgentScreen(agent)
                    );
                }
            }
        });


    }

    private void loadAgentScreen(AgentEntry agent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(AGENT_PAGE_FXML_RESOURCE_LOCATION));
        Parent root;
        try {
            AgentMainController controller = new AgentMainController(agent);
            loader.setController(controller);
            root = loader.load();
            controller.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
