import utils.http.HttpClientUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/login_ex3.fxml"));
        Parent root = loader.load();
        stage.setTitle("Battlefield Login");
        stage.setScene(new Scene(root));
        stage.show();

    }


    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
    }
}
