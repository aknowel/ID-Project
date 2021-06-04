package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.*;
import java.util.Scanner;

public class SignUpController {
    File file;
    File file2;
    @FXML
    TextField login;
    @FXML
    TextField pass;
    @FXML
    TextField rpass;
    Alert alert;
    public void back()
    {
        SignInController.controller.board.getChildren().remove(SignInController.controller.root);
    }
    public void signUp() throws IOException {
        file=new File("src/resources/other/Data.txt");
        file2=new File("src/resources/other/Data2.txt");
        boolean check=true;
        if(pass.getText().equals(rpass.getText()) && !pass.getText().equals("")) {
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                PrintWriter writer = new PrintWriter(file2);
                while (scanner.hasNext()) {
                    String log=scanner.next();
                    String passw=scanner.next();
                    if(log.equals(login.getText()) && passw.equals(pass.getText())) {
                        check=false;
                    }
                    writer.println(log+" "+passw);
                }
                scanner.close();
                if(!check) {
                    alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sign up");
                    alert.setHeaderText("Error! Try again!");
                    alert.showAndWait();
                }
                else
                {
                    writer.write(login.getText() + " " + rpass.getText());
                    alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sign up");
                    alert.setHeaderText("Successfully added!");
                    alert.showAndWait();
                }
                writer.close();
                file.delete();
                file2.renameTo(file);
            }
            else
            {
                file.createNewFile();
                PrintWriter writer = new PrintWriter(file);
                writer.write(login.getText() + " " + rpass.getText());
                writer.close();
            }
        }
        else
        {
            alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sign up");
            alert.setHeaderText("New password and reenter password are not equal!");
            alert.showAndWait();
        }
    }
}
