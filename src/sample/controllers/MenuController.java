package sample.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class MenuController {
    Stage stage;
    public void exit(ActionEvent event)  {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
