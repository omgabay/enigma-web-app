package controllers;
import exceptions.EnigmaException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import machine.IEngine;
import model.MachineModel;

import javax.xml.bind.JAXBException;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class HeaderController {

    @FXML private Label loadStatusLabel;

    private Stage stage;
    private IEngine engine;

    private MainController mainController;



    private BooleanProperty fileWasLoadedProperty;




    public HeaderController(){
        fileWasLoadedProperty = new SimpleBooleanProperty(false);
        System.out.println("header Controller created");
    }






    public void loadFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Enigma XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Enigma XML", "*.xml"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                MachineModel.getInstance().load(file);
                fileWasLoadedProperty.set(true);
                loadStatusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
                loadStatusLabel.setText(file.getName());

            }catch(EnigmaException | FileNotFoundException | JAXBException e){
                loadStatusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
                loadStatusLabel.setText(e.getMessage());
            }
        }
    }

    public void setMainController(MainController appController) {
        System.out.println("Setting main controller for header");
        this.mainController = appController;
        this.stage = appController.getStage();
        this.engine = appController.getEngine();

    }

    @FXML
    public void changeThemeAction(){
      mainController.changeTheme();
    }


    public BooleanProperty getFileLoadProperty(){
        return this.fileWasLoadedProperty;
    }

}
