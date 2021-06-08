package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;


public class CreateOpinionController {
    @FXML
    AnchorPane root;
    @FXML
    TextArea area;
    Alert alert;
    Stage stage;
    Statement statement;
    public void add(ActionEvent event) throws SQLException {
        if(area.getText().length()>1000)
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Opinion");
            alert.setHeaderText("Error! Your opinion is too long! Try again!");
            alert.showAndWait();
            return;
        }
        else
        {
            boolean check=false;
            statement= DBStarter.conn.createStatement();
            alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure ?", ButtonType.YES,ButtonType.NO,ButtonType.CANCEL);
            alert.setTitle("Opinion");
            alert.setHeaderText("Do you want to be anonymous?");
            String text1="Insert into opinions(opinion) values(\'"+area.getText()+"\');";
            String text2="Insert into opinions(client_id,opinion) values("+MenuController.id+",\'"+area.getText()+"\');";
            Optional<ButtonType> result=alert.showAndWait();
            try
            {
                if(result.orElse(null).equals(ButtonType.YES)) {
                    statement.executeUpdate(text1);
                    MenuController.builder.append(text1).append('\n');
                }
                else if(result.orElse(null).equals(ButtonType.NO))
                {
                    statement.executeUpdate(text2);
                    MenuController.builder.append(text2).append('\n');
                }
                MenuController.saveDB();
            }
            catch (Exception e)
            {
                alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Opinion");
                alert.setHeaderText("Error! Try again!");
                alert.showAndWait();
                return;
            }
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/myOpinions.fxml"));
            try {
                AnchorPane root = fxmlLoader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("My trips");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            stage.show();
        }
    }
    public void back()
    {
        MyOpinionsController.controller.main.getChildren().remove(root);
    }
}
