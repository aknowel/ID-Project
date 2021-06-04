package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
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
    ResultSet result;
    Statement statement;
    String query;
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
    public void trips() throws SQLException {
        statement=conn.createStatement();
        query="Select * from trips";
        result=statement.executeQuery(query);
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

        }
    }
    public void myPayments() throws SQLException {
        statement=conn.createStatement();
        query="Select * from trips";
        result=statement.executeQuery(query);
    }
    public void myDiscounts() throws SQLException {
        statement=conn.createStatement();
        query="Select * from client_discounts where id="+id;
        result=statement.executeQuery(query);
    }
    public void myStatuses() throws SQLException {
        statement=conn.createStatement();
        query="Select * from trips";
        result=statement.executeQuery(query);
    }
    public void opinions() throws SQLException {
        statement=conn.createStatement();
        query="Select * from opinions";
        result=statement.executeQuery(query);
    }
    public void exit(ActionEvent event){
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
