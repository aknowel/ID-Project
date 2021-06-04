package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sample.DBStarter;

import java.sql.*;


public class MenuController {
    @FXML
    AnchorPane board;
    @FXML
    Button button;
    Stage stage;
    AnchorPane root;
    Connection conn;
    Label label=new Label("You have to sign in!");
    static boolean status=false;
    static int id=1;
    public void initialize() throws SQLException {
        conn=DBStarter.start();
        if(!status)
        {
            button.setText("Sign in");
        }
        else
        {
            button.setText("Log out");
        }
    }
    public void signIn(ActionEvent event)
    {
        if(!status) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/SignIn.fxml"));
            try {
                root = fxmlLoader.load();
                root.setLayoutX(340);
                root.setLayoutY(160);
                board.getChildren().add(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            status=false;
            button.setText("Sign in");
        }
    }
    public void trips(ActionEvent event) {
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/Trips.fxml"));
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
    public void myTrips(ActionEvent event) {
        if(status)
        {
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/myTrips.fxml"));
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
        else
        {
            board.getChildren().remove(label);
            setLabel(label,Color.RED,289);
            board.getChildren().add(label);
        }
    }
    public void myPayments(ActionEvent event){
        if(status)
        {
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/MyPayments.fxml"));
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
        else
        {
            board.getChildren().remove(label);
            setLabel(label,Color.RED,348);
            board.getChildren().add(label);
        }
    }
    public void myDiscounts(ActionEvent event){
        if(status)
        {
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/MyDiscounts.fxml"));
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
        else
        {
            board.getChildren().remove(label);
            setLabel(label,Color.RED,415);
            board.getChildren().add(label);
        }
    }
    public void myStatuses(ActionEvent event){
        if(status)
        {
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/MyStatuses.fxml"));
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
        else
        {
            board.getChildren().remove(label);
            setLabel(label,Color.RED,478);
            board.getChildren().add(label);
        }
    }
    public void opinions(ActionEvent event) {
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/opinions.fxml"));
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
    public void exit(ActionEvent event){
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    private static void setLabel(Label text, Color color, double y)
    {
        text.setFont(Font.font("Verdana",16));
        text.setTextFill(color);
        text.setStyle("-fx-background-color: lightblue;");
        text.relocate(792, y);
    }
}
