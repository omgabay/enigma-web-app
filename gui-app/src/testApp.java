
import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



public class testApp extends Application{

    public static void main(String[] args) {
        launch();
    }

        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/enigma-fxml/main.fxml"));
            stage.getIcons().add(new Image("/images/turing_icon.png"));
            Parent root = loader.load();
            MainController myController = loader.getController();
            myController.setStage(stage);
            stage.setTitle("Enigma - Omer Gabay & Bar Norani");
            stage.setScene(new Scene(root));
            stage.show();

        }
    }

