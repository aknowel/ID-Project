package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sample.DBStarter;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import static java.lang.Character.isDigit;

public class SignUpController {
    Statement statement;
    ResultSet result;
    File file;
    File file2;
    @FXML
    TextField name;
    @FXML
    TextField surname;
    @FXML
    TextField pesel;
    @FXML
    TextField account;
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
    public void signUp() throws IOException, SQLException {
        if(pesel.getText().length()!=11)
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign up");
            alert.setHeaderText("Wrong pesel!");
            alert.showAndWait();
            return;
        }
        else
        {
            for(int i=0;i<11;i++)
            {
                if(!isDigit(pesel.getText().charAt(i)))
                {
                    alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Sign up");
                    alert.setHeaderText("Wrong pesel!");
                    alert.showAndWait();
                    return;
                }
            }
        }
        for(int i=0;i<account.getText().length();i++)
        {
            if(!isDigit(account.getText().charAt(i)))
            {
                alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sign up");
                alert.setHeaderText("Wrong account number!");
                alert.showAndWait();
                return;
            }
        }
        file=new File("src/resources/other/Data.txt");
        file2=new File("src/resources/other/Data2.txt");
        boolean check=true;
        if(pass.getText().equals(rpass.getText()) && !pass.getText().equals("")) {
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                PrintWriter writer = new PrintWriter(file2);
                while (scanner.hasNext()) {
                    int id=Integer.parseInt(scanner.next());
                    String log=scanner.next();
                    String passw=scanner.next();
                    if(log.equals(login.getText()) && passw.equals(pass.getText())) {
                        check=false;
                    }
                    writer.println(id+" "+log+" "+passw);
                }
                scanner.close();
                if(!check) {
                    alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Sign up");
                    alert.setHeaderText("Error! Try again!");
                    alert.showAndWait();
                }
                else
                {
                    statement = DBStarter.conn.createStatement();
                    result=statement.executeQuery("Select id from clients where id>=10000 order by id desc;");
                    int id=0;
                    if(result.next()) {
                        id=result.getInt("id");
                    }
                    String query = "INSERT INTO clients VALUES(?,?, ?, ?, ?)";
                    PreparedStatement pst = DBStarter.conn.prepareStatement(query);
                    pst.setInt(1,id+1);
                    pst.setString(2, name.getText());
                    pst.setString(3, surname.getText());
                    pst.setString(4, pesel.getText());
                    pst.setString(5, account.getText());
                    boolean insert=true;
                    try {
                        pst.executeUpdate();
                    }
                    catch (Exception e)
                    {
                        if(e.getMessage().equals("ERROR: Niepoprawny PESEL\n" +
                                "  Gdzie: PL/pgSQL function pesel_check() line 19 at RAISE"))
                        {
                            alert=new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Sign up");
                            alert.setHeaderText("Wrong pesel!");
                            alert.showAndWait();
                            return;
                        }
                        else if("ERROR: duplicate key value violates unique constraint \"clients_pesel_account_number_key\"".equals(e.getMessage().substring(0,88)))
                        {
                            statement = DBStarter.conn.createStatement();
                            result=statement.executeQuery("Select id from clients where pesel=\'"+pesel.getText()+"\' and account_number=\'"+account.getText()+"\' order by id desc;");
                            if(result.next())
                            {
                                id=result.getInt("id")-1;
                            }
                            insert=false;
                        }
                    }
                    writer.write((id+1)+" "+login.getText() + " " + rpass.getText());
                    if(insert) {
                        MenuController.builder.append(pst.toString()).append(';').append('\n');
                    }
                    alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Sign up");
                    alert.setHeaderText("Successfully added!");
                    alert.showAndWait();
                }
                writer.close();
                file.delete();
                file2.renameTo(file);
                MenuController.saveDB();
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
