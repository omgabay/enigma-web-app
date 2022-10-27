package main;

import controllers.AlliesController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Configuration;
import utils.http.HttpClientUtil;

import java.io.IOException;

import static utils.Configuration.MAIN_PAGE_FXML_RESOURCE_LOCATION;

public class AlliesClient extends Application {

    private final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/AllyLogin.fxml";
    private final StringProperty errorMessageProperty = new SimpleStringProperty("");

    private Stage stage;

    @FXML TextField usernameInput;
    @FXML Label errorMessageLabel;



    public void initialize(){
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            loader.setController(this);
            stage = primaryStage;
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
    }


    @FXML
    private void registerAlly(ActionEvent actionEvent){

        String userName = usernameInput.getText();

        if(userName.isEmpty()){
            errorMessageProperty.set("Name field is empty, you cannot login with no name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Configuration.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("type", "ally")
                .build()
                .toString();
        System.out.println("New request is launched for: " + finalUrl);
        errorMessageProperty.set("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Platform.runLater(() ->
                            loadAllyScreen()
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("error - " + e.getMessage())
                );
            }
        });


    }

    private void loadAllyScreen(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION));
//        AlliesController allyController = new AlliesController();
//        loader.setController(allyController);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
