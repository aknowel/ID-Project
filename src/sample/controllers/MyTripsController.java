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


public class MyTripsController {
    @FXML
    AnchorPane pane;
    Stage stage;
    Button previous=null;
    public void initialize()
    {
        pane.setStyle("-fx-background-color: #00ee00");
        Label name=new Label();
        Label price=new Label();
        Label paid=new Label();
        name.setPrefSize(600, 100); price.setPrefSize(268.5, 100); paid.setPrefSize(268.5, 100);
        name.relocate(0 ,0); price.relocate(600, 0); paid.relocate(868.5, 0);
        name.setText("Trip Name"); price.setText("Price"); paid.setText("You Paid");
        name.setAlignment(Pos.CENTER); price.setAlignment(Pos.CENTER); paid.setAlignment(Pos.CENTER);
        name.setStyle("-fx-font-size: 40; -fx-background-color: #ffff00; -fx-font-weight: bold");
        price.setStyle("-fx-font-size: 40; -fx-font-weight: bold");
        paid.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-background-color: #ffff00" );
        pane.getChildren().addAll(name, price, paid);
        try {
            Statement stmt= DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from payments where id=" + MenuController.id + ';' );
            while(rs.next())
            {
                Button bt = new Button();
                Label p=new Label();
                Label pd=new Label();
                Button pay= new Button();
                bt.setPrefSize(600, 100);
                pay.setPrefSize(150, 100);
                bt.setStyle("""
                        -fx-background-color:
                                linear-gradient(#ffd65b, #e68400),
                                linear-gradient(#ffef84, #f2ba44),
                                linear-gradient(#ffea6a, #efaa22),
                                linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),
                                linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));
                            -fx-background-insets: 0,1,2,3,0;
                            -fx-text-fill: #654b00;
                            -fx-font-weight: bold;
                            -fx-font-size: 14px;
                            -fx-padding: 10 20 10 20;
                            -fx-font-size: 40""");
                pay.setStyle("""
                        -fx-background-color:
                                linear-gradient(#ffd65b, #e68400),
                                linear-gradient(#ffef84, #f2ba44),
                                linear-gradient(#ffea6a, #efaa22),
                                linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),
                                linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));
                            -fx-background-insets: 0,1,2,3,0;
                            -fx-text-fill: #654b00;
                            -fx-font-weight: bold;
                            -fx-font-size: 14px;
                            -fx-padding: 10 20 10 20;
                            -fx-font-size: 40""");
                p.setStyle("-fx-font-size: 35;" +
                        " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
                pd.setStyle("-fx-font-size: 35;" +
                        " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
                p.setPrefSize(268.5, 100);
                pd.setPrefSize(268.5, 100);
                p.setAlignment(Pos.CENTER);
                pd.setAlignment(Pos.CENTER);
                if(previous==null)
                {
                    bt.relocate(0,100);
                    p.relocate(600, 100);
                    pd.relocate(868.5, 100);
                    pay.relocate(1137, 100);
                }
                else
                {
                    bt.relocate(0, previous.getBoundsInParent().getMaxY()+100);
                    p.relocate(600, previous.getBoundsInParent().getMaxY()+100);
                    pd.relocate(868.5, previous.getBoundsInParent().getMaxY()+100);
                    pay.relocate(1137, previous.getBoundsInParent().getMaxY()+100);
                }
                previous=bt;
                bt.setText(rs.getString(4));
                p.setText(rs.getString(6));
                pd.setText(rs.getString(5));
                pay.setText("Pay");
                pane.getChildren().addAll(bt, p, pd, pay);
            }
        } catch (SQLException e) {
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
