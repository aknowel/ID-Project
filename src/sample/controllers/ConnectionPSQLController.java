package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ConnectionPSQLController {
    @FXML
    TextField login;
    @FXML
    TextField pass;
    @FXML
    TextField url;
    File file;
    Scanner scanner;
    Statement statement;
    Stage stage;
    Connection conn;
    Alert alert;
    public void initialize()
    {
        login.setText(DBStarter.user);
        pass.setText(DBStarter.password);
        url.setText(DBStarter.jdbcUrl);
    }
    public void connect(ActionEvent event) throws FileNotFoundException, SQLException {
        DBStarter.user=login.getText();
        DBStarter.password=pass.getText();
        DBStarter.jdbcUrl=url.getText();
        try {
            conn = DBStarter.start();
        }
        catch(Exception e)
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection");
            alert.setHeaderText("Connection failed!");
            alert.showAndWait();
            return;
        }
        file = new File("drop.sql");
        if (file.exists()) {
            scanner = new Scanner(file);
            String line;
            statement = conn.createStatement();
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                statement.execute(line);
            }
            scanner.close();
        }
        file = new File("travel_agency.sql");
        if (file.exists()) {
            scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            statement = conn.createStatement();
            while (scanner.hasNext()) {
                builder.append('\n').append(scanner.nextLine());
            }
            statement.execute(builder.toString());
            scanner.close();
        }
        file = new File("in.sql");
        if (file.exists()) {
            scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            statement = conn.createStatement();
            while (scanner.hasNext()) {
                builder.append('\n').append(scanner.nextLine());
            }
            statement.executeUpdate(builder.toString());
        }
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/fxml/menu.fxml"));
        try {
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setX(400);
            stage.setY(100);
            stage.setScene(scene);
            stage.setTitle("Menu");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        stage.show();
    }
    public void exit(ActionEvent event) throws FileNotFoundException, SQLException {
        if(conn!=null) {
            file = new File("drop.sql");
            if (file.exists()) {
                scanner = new Scanner(file);
                String line;
                statement = conn.createStatement();
                while (scanner.hasNext()) {
                    line = scanner.nextLine();
                    statement.execute(line);
                }
                scanner.close();
            }
        }
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
