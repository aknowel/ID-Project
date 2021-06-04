package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class SignInController {
    Stage stage;
    AnchorPane root;
    Scene scene;
    Scanner scanner;
    File file;
    Label label=new Label("Wrong login or password!");
    @FXML
    TextField text1;
    @FXML
    TextField text2;
    @FXML
    AnchorPane board;
    static SignInController controller;
    public void initialize()
    {
        controller=this;
    }
    public void signIn(ActionEvent event) throws FileNotFoundException {
        file=new File("src/resources/other/Data.txt");
        if(file.exists())
        {
            scanner=new Scanner(file);
            String login="";
            String password="";
            int id=1;
            boolean success=false;
            while(scanner.hasNext())
            {
                login=scanner.next();
                password=scanner.next();
                if(login.equals(text1.getText()) && password.equals(text2.getText()))
                {
                    MenuController.status=true;
                    MenuController.id=id;
                    success=true;
                    break;
                }
                id++;
            }
            scanner.close();
            if(!success) {
                board.getChildren().remove(label);
                setLabel(label, Color.RED, 226);
                board.getChildren().add(label);
            }
            else
            {
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                FXMLLoader fxmlLoader=new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/resources/fxml/menu.fxml"));
                try {
                    root = fxmlLoader.load();
                    scene = new Scene(root);
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
    }
    public void signUp()
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/fxml/SignUp.fxml"));
        try {
            root = fxmlLoader.load();
            root.setLayoutX(0);
            root.setLayoutY(0);
            board.getChildren().add(root);
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
            root = fxmlLoader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Menu");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        stage.show();
    }
    private static void setLabel(Label text, Color color, double y)
    {
        text.setFont(Font.font("Verdana",16));
        text.setTextFill(color);
        text.setStyle("-fx-background-color: lightblue;");
        text.relocate(210, y);
    }
}
