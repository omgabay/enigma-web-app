package UboatTeamsTable;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

public class main extends Application {

    public static final String path = "/fxml/contestTeamsTable.fxml";
    private TeamTableController tableController;
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource(path);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(url);
            Parent root = loader.load();
            tableController = loader.getController();
            tableController.startTeamRefresher("moshe");
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
        this.tableController.close();
    }
}
