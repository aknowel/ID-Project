package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OpinionsController {
    @FXML
    AnchorPane pane;
    Stage stage;
    Alert alert;
    Label previous=null;
    public void initialize()
    {
        pane.setStyle("-fx-background-color: #11ee22");
        try {
            Statement stmt = DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from opinions");
            while(rs.next())
            {
                Label op=new Label();
                op.setPrefSize(864, 42);
                op.setText(rs.getString(2));
                op.setAlignment(Pos.CENTER);
                op.setStyle("-fx-font-size: 30; -fx-background-color: #ffff00; -fx-font-style: italic");
                if(previous==null)
                    op.relocate(10, 10);
                else {
                    op.relocate(10, previous.getBoundsInParent().getMaxY()+65);
                }
                pane.getChildren().add(op);
                previous=op;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void myOpinions(ActionEvent event)
    {
        if(MenuController.status) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/myOpinions.fxml"));
            try {
                AnchorPane root = fxmlLoader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("My Opinions");
            } catch (Exception e) {
                e.printStackTrace();
            }
            stage.show();
        }
        else
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Opinions");
            alert.setHeaderText("Error! You have to sign in!");
            alert.showAndWait();
        }
    }
    public void returnMenu(ActionEvent event)
    {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/fxml/menu.fxml"));
        try {
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Menu");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        stage.show();
    }
}
