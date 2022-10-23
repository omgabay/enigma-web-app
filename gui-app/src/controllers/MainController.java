package controllers;

import controllers.bruteforce.BruteForceController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import machine.Engine;
import machine.IEngine;
import model.MachineModel;
import controllers.setup.SetupController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainController {


    private Stage stage;
    private final IEngine myEngine;

    private final MachineModel machineModel;


    @FXML HeaderController headerController;
    @FXML SetupController setupController;

    @FXML ScrollPane rootPane;
    @FXML BorderPane mainDisplayBP;


    @FXML TabPane tabs;

    @FXML private Tab operatorTab;
    @FXML private Tab bruteforceTab;

    ArrayList<String> backgroundColors;
    Iterator<String> colorIterator;



    public MainController(){
        // Setting up environment
        myEngine = new Engine();
        machineModel = MachineModel.getInstance();
        machineModel.setEngine(myEngine);

        backgroundColors = new ArrayList<>();
        backgroundColors.add("#6495ED");
        backgroundColors.add("#40E0D0");
        backgroundColors.add("#8E44AD");
        backgroundColors.add("#DC143C");
        try {
            backgroundColors.addAll(MainController.allColors());
        } catch (Exception e) {
            e.printStackTrace();
        }
        colorIterator = backgroundColors.iterator();



    }

    @FXML
    public void initialize() {

        // fixing scrollpane problem causing blurriness

        rootPane.setCache(false);
        for (Node n : rootPane.getChildrenUnmodifiable()) {
            n.setCache(false);
        }
        BorderPane.setAlignment(mainDisplayBP,Pos.CENTER);



        headerController.setMainController(this);
        setupController.setMainController(this);
        headerController.getFileLoadProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(!newValue) return;
                    machineModel.updateAfterLoad();
                    setupController.createMachineSetupView();
                    headerController.getFileLoadProperty().set(false);
                }
        );


        this.setupController.bindWithModelProperties();

        operatorTab.setDisable(true);
        bruteforceTab.setDisable(true);

    }


    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage window){ this.stage = window; }


    public IEngine getEngine() {
        return this.myEngine;

    }


    public void updateUIAfterSetup() {
        this.operatorTab.setDisable(false);
        this.bruteforceTab.setDisable(false);
    }


    public void changeTheme() {


        if(!colorIterator.hasNext()){
            colorIterator = this.backgroundColors.iterator();
        }
        String style_rule = "-fx-background-color:" + colorIterator.next();


        mainDisplayBP.setStyle(style_rule);

    }


    private static List<String> allColors() throws ClassNotFoundException, IllegalAccessException {
        List<String> colors = new ArrayList<>();
        Class<?> clazz = Class.forName("javafx.scene.paint.Color");
            Field[] field = clazz.getFields();

        for (int i = 0; i < field.length; i++) {
            Field f = field[i];
            Object obj = f.get(null);
            if(obj instanceof Color){
                colors.add(f.getName());
            }

        }
        return colors;
    }


}
