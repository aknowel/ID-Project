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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Scanner;


public class MenuController {
    @FXML
    AnchorPane board;
    @FXML
    Button button;
    Stage stage;
    AnchorPane root;
    Label label=new Label("You have to sign in!");
    File file;
    Scanner scanner;
    Statement statement;
    static boolean status=false;
    static int id=1;
    public static StringBuilder builder=new StringBuilder();
    public void initialize() {
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
                stage.setTitle("Trips");
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
                stage.setTitle("My Trips");
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
                stage.setTitle("My discounts");
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
                stage.setTitle("My statuses");
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
                stage.setTitle("Options");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            stage.show();
    }
    public void exit(ActionEvent event) throws IOException, SQLException {
        file = new File("drop.sql");
        if (file.exists()) {
            scanner = new Scanner(file);
            String line;
            statement = DBStarter.conn.createStatement();
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                statement.execute(line);
            }
            scanner.close();
        }
        saveDB();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    public static void saveDB() throws IOException {
        File file=new File("src/resources/other/base.sql");
        File file2=new File("src/resources/other/base2.sql");
        Scanner scanner = new Scanner(file);
        PrintWriter writer = new PrintWriter(file2);
        while (scanner.hasNext()) {
            String line=scanner.nextLine();
            writer.println(line);
        }
        scanner.close();
        if(!builder.toString().equals("")) {
            writer.print(builder.toString());
            builder = new StringBuilder();
        }
        writer.close();
        Files.delete(Path.of("src/resources/other/base.sql"));
        file2.renameTo(file);
    }
    private static void setLabel(Label text, Color color, double y)
    {
        text.setFont(Font.font("Verdana",16));
        text.setTextFill(color);
        text.setStyle("-fx-background-color: lightblue;");
        text.relocate(792, y);
    }
}
