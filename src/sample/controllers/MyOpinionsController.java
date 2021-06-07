package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MyOpinionsController {
    @FXML
    AnchorPane pane;
    @FXML
    AnchorPane main;
    AnchorPane root;
    Stage stage;
    Label previous=null;
    List<Button> delList=new ArrayList<>();
    List<Label> labelList=new ArrayList<>();
    Alert alert;
    Statement statement;
    static MyOpinionsController controller;
    public void initialize()
    {
        controller=this;
        pane.setStyle("-fx-background-color: #11ee22");
        try {
            Statement stmt = DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from opinions where client_id=" + MenuController.id + ';');
            int i=0;
            while(rs.next())
            {
                Label op=new Label();
                Button del=new Button();
                del.setId(String.valueOf(i));
                i++;
                labelList.add(op);
                delList.add(del);
                op.setPrefSize(740, 35);
                del.setPrefSize(114, 35);
                op.setText(rs.getString(2));
                op.setAlignment(Pos.CENTER);
                op.setStyle("-fx-font-size: 30; -fx-background-color: #ffff00; -fx-font-style: italic");
                del.setStyle(MyTripsController.style.substring(0, MyTripsController.style.length()-19) + "-fx-font-size: 20\"\";");
                del.setText("delete");
                del.setOnAction((e)-> {
                    try {
                        delete(Integer.parseInt(del.getId()),e);
                    } catch (IOException | SQLException ioException) {
                        ioException.printStackTrace();
                    }
                });
                if(previous==null) {
                    op.relocate(10, 10);
                    del.relocate(760, 10);
                }
                else {
                    op.relocate(10, previous.getBoundsInParent().getMaxY()+55);
                    del.relocate(760, previous.getBoundsInParent().getMaxY()+55);
                }
                pane.getChildren().addAll(op, del);
                previous=op;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(int i,ActionEvent event) throws IOException, SQLException {
        statement=DBStarter.conn.createStatement();
        alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure ?", ButtonType.YES,ButtonType.CANCEL);
        alert.setTitle("Delete");
        alert.setHeaderText("Do you want to delete this opinion?");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.orElse(null).equals(ButtonType.YES))
        {
            String query="Delete from opinions where client_id="+MenuController.id+" and opinion=\'"+labelList.get(i).getText()+"\' ;";
            try {
                statement.executeUpdate(query);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                alert=new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Opinion");
                alert.setHeaderText("Error! Try again!");
                alert.showAndWait();
                return;
            }
            MenuController.builder.append(query).append('\n');
            MenuController.saveDB();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/myOpinions.fxml"));
            try {
                AnchorPane root = fxmlLoader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("My opinions");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            stage.show();
        }
    }
    public void create()
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/fxml/newOpinion.fxml"));
        try {
            root = fxmlLoader.load();
            root.setLayoutX(350);
            root.setLayoutY(200);
            main.getChildren().add(root);
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
