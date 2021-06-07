package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyOpinionsController {
    @FXML
    AnchorPane pane;
    @FXML
    AnchorPane main;
    AnchorPane root;
    Stage stage;
    Label previous=null;
    static MyOpinionsController controller;
    public void initialize()
    {
        controller=this;
        pane.setStyle("-fx-background-color: #11ee22");
        try {
            Statement stmt = DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from opinions where client_id=" + MenuController.id + ';');
            while(rs.next())
            {
                Label op=new Label();
                Button del=new Button();
                op.setPrefSize(740, 35);
                del.setPrefSize(114, 35);
                op.setText(rs.getString(2));
                op.setAlignment(Pos.CENTER);
                op.setStyle("-fx-font-size: 30; -fx-background-color: #ffff00; -fx-font-style: italic");
                del.setStyle(MyTripsController.style.substring(0, MyTripsController.style.length()-19) + "-fx-font-size: 20\"\";");
                del.setText("delete");
                if(previous==null) {
                    op.relocate(10, 10);
                    del.relocate(760, 10);
                }
                else {
                    op.relocate(10, previous.getBoundsInParent().getMaxY()+55);
                    del.relocate(760, previous.getBoundsInParent().getMaxY()+55);
                }
                pane.getChildren().addAll(op, del);
                previous=op;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void create()
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/fxml/newOpinion.fxml"));
        try {
            root = fxmlLoader.load();
            root.setLayoutX(350);
            root.setLayoutY(200);
            main.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
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
