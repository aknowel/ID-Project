package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import sample.DBStarter;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import static java.lang.Math.round;


public class PayController {
    @FXML
    AnchorPane root;
    @FXML
    TextField field;
    Alert alert;
    Statement statement;
    public void pay() throws SQLException, IOException {
        statement= DBStarter.conn.createStatement();
        double l;
        double i;
        try {
            l = Double.parseDouble(MyTripsController.controller.labelList.get(MyTripsController.button).getText());
            i=l+Double.parseDouble(field.getText());
        }
        catch (Exception e)
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pay");
            alert.setHeaderText("Wrong amount! Try again!");
            alert.showAndWait();
            return;
        }
        if(l<0 || i>Double.parseDouble(MyTripsController.controller.priceList.get(MyTripsController.button).getText()))
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pay");
            alert.setHeaderText("Wrong amount! Try again!");
            alert.showAndWait();
            return;
        }
        String query="Update client_trips set paid_amount="+i+" where client_id="+MenuController.id+" and paid_amount="+l+" ;";
        try {
            statement.executeUpdate(query);
        }
        catch(Exception e)
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pay");
            alert.setHeaderText("Error! Try again!");
            alert.showAndWait();
            return;
        }
        MyTripsController.controller.labelList.get(MyTripsController.button).setText(String.valueOf(Math.floor(i * 100) / 100));
        MenuController.builder.append(query).append('\n');
        MenuController.saveDB();
        back();
    }
    public void back()
    {
        MyTripsController.controller.main.getChildren().remove(root);
    }
}
