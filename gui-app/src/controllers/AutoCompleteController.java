package controllers;

import auxiliary.Alphabet;
import auxiliary.Dictionary;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.EventHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AutoCompleteController{
    private Dictionary dictionary;

    private List<Button> buttons;


    @FXML Button autoComplete1;
    @FXML Button autoComplete2;
    @FXML Button autoComplete3;
    @FXML Button autoComplete4;
    @FXML Button autoComplete5;
    @FXML TextField text;

    public AutoCompleteController(){
        String text = "\t\t\tencapsulation german poland kombat waffele whom? leg character terms else camel rabbit fire text if they" +
                " element sky item robot! past dune dolphine then it am quality eye moon system folder light letter number# notch allies dog word hash why? gun pink" +
                " what? yellow sea see noon file top does ear can't table foot hand a strike black i electricity pistol kill boat democracy off battle" +
                " single fly component blue watch reflector future machine progress atom under inferno voice rotor tonight mouce door won't data use" +
                " doom sign screen do bomb red later whale white hello? than which england morning! dirt tree thrown water trash ranch she sand squad" +
                " desk present where? magenta game code enigma plural death vegtable fruit for their privacy back house fox not hair napkin and street cat now of iteration class trike fight mortal live how? on patrol" +
                " keyboard midnight or green umbrella wheel saw orange airplane window front he\n";

        String exclude = "!'#?";

        Alphabet abc = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ !?'");
        this.dictionary = new Dictionary(text,exclude, abc);
        buttons = new ArrayList<>();

    }

    @FXML private void initialize(){
        buttons.add(autoComplete1);
        buttons.add(autoComplete2);
        buttons.add(autoComplete3);
        buttons.add(autoComplete4);
        buttons.add(autoComplete5);
        // action event
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                Button b = (Button) e.getSource();

                text.setText(b.getText());
            }
        };        for (Button button : buttons) {
            button.setOnAction(event);
        }


        // Adding listener to the textfield textProperty
        this.text.textProperty().addListener((observable, oldValue, newValue) -> {
            List<String> suggestions = dictionary.getWordSuggestions(newValue);
            System.out.println(suggestions);
            Iterator<String> it = suggestions.iterator();
            for (Button button : buttons) {
                if (it.hasNext()) {
                    button.setText(it.next());
                } else {
                    button.setText("");
                }
            }

        });
        System.out.println("In init");


    }








}
