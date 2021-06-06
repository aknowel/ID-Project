package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyStatusesController {
    @FXML
    AnchorPane pane;
    Stage stage;
    public void initialize()
    {
        pane.setStyle("-fx-background-color: #00ee00");
        Label sth=new Label();
        sth.setStyle("-fx-font-size: 20");
        sth.setText("You are entitled to receive discounts for:");
        sth.relocate(10, 10);
        pane.getChildren().add(sth);
        try
        {
            Statement stmt= DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery( "select cl.status_id from clients c join client_statuses cl on c.id=cl.client_id"
            + " where cl.client_id=" + MenuController.id + ';');
            int i=0;
            while(rs.next())
            {
                Label l = new Label();
                l.setStyle("-fx-font-size: 20");
                String sname=
                switch (rs.getInt(1))
                {
                    case 1->"-Students";
                    case 2->"-Seniors";
                    case 3->"-Combatants";
                    case 4->"-The Disabled";
                    default -> "-Blood Donors";
                };
                l.relocate(10, 30 + 20*i);
                l.setText(sname);
                i++;
                pane.getChildren().add(l);
            }
        }catch(SQLException e){
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
