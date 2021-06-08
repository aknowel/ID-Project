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


public class MyTripsController {
    @FXML
    AnchorPane main;
    @FXML
    AnchorPane pane;
    Stage stage;
    Button previous=null;
    AnchorPane root;
    List<Button> buttonList=new ArrayList<>();
    List<Button> resignButtonList=new ArrayList<>();
    List<Label> labelList=new ArrayList<>();
    List<Label> priceList=new ArrayList<>();
    List<Label> dateList=new ArrayList<>();
    List<Integer> iList=new ArrayList<>();
    List<Integer> idList=new ArrayList<>();
    List<Button> tripList=new ArrayList<>();
    Statement statement;
    Alert alert;
    static boolean check=false;
    static MyTripsController controller;
    static int button;
    static String style="""
                        -fx-background-color:
                                linear-gradient(#ffd65b, #e68400),
                                linear-gradient(#ffef84, #f2ba44),
                                linear-gradient(#ffea6a, #efaa22),
                                linear-gradient(#ffe657 0%, #f8c202 50%, #eea10b 100%),
                                linear-gradient(from 0% 0% to 15% 50%, rgba(255,255,255,0.9), rgba(255,255,255,0));
                            -fx-background-insets: 0,1,2,3,0;
                            -fx-text-fill: #654b00;
                            -fx-font-weight: bold;
                            -fx-padding: 10 20 10 20;
                            -fx-font-size: 40""";
    int getTid()
    {
        return idList.get(button);
    }
    public void initialize()
    {
        controller=this;
        pane.setStyle("-fx-background-color: #00ee00");
        Label name=new Label();
        Label price=new Label();
        Label paid=new Label();
        Label date=new Label();
        name.setPrefSize(600, 100); price.setPrefSize(268.5, 100); paid.setPrefSize(268.5, 100); date.setPrefSize(500, 100);
        name.relocate(0 ,0); price.relocate(600, 0); paid.relocate(868.5, 0); date.relocate(1137, 0);
        name.setText("Trip Name"); price.setText("Price"); paid.setText("You Paid"); date.setText("Date");
        name.setAlignment(Pos.CENTER); price.setAlignment(Pos.CENTER); paid.setAlignment(Pos.CENTER); date.setAlignment(Pos.CENTER);
        name.setStyle("-fx-font-size: 40; -fx-font-weight: bold");
        price.setStyle("-fx-font-size: 40; -fx-background-color: #ffff00; -fx-font-weight: bold");
        paid.setStyle("-fx-font-size: 40; -fx-font-weight: bold");
        date.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-background-color: #ffff00");
        pane.getChildren().addAll(name, price, paid, date);
        try {
            Statement stmt= DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from payments where client_id=" + MenuController.id + ';' );
            int i=0;
            while(rs.next())
            {
                Button bt = new Button();
                bt.setId(String.valueOf(i));
                Label p=new Label();
                Label pd=new Label();
                Label d=new Label();
                Button pay= new Button();
                Button resign= new Button();
                pay.setId(String.valueOf(i));
                resign.setId(String.valueOf(i));
                bt.setId(String.valueOf(i));
                buttonList.add(pay);
                resignButtonList.add(resign);
                labelList.add(pd);
                priceList.add(p);
                dateList.add(d);
                tripList.add(bt);
                i++;
                bt.setPrefSize(600, 100);
                pay.setPrefSize(150, 50);
                resign.setPrefSize(150, 50);
                bt.setStyle(style);
                pay.setStyle(style.substring(0, style.length()-19) + "-fx-font-size: 20\"\";");
                resign.setStyle(style.substring(0, style.length()-19) + "-fx-font-size: 20\"\";");

                p.setStyle("-fx-font-size: 35;" +
                        " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
                pd.setStyle("-fx-font-size: 35;" +
                        " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
                d.setStyle("-fx-font-size: 35;" +
                        " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
                p.setPrefSize(268.5, 100);
                pd.setPrefSize(268.5, 100);
                d.setPrefSize(500, 100);
                p.setAlignment(Pos.CENTER);
                pd.setAlignment(Pos.CENTER);
                d.setAlignment(Pos.CENTER);
                if(previous==null)
                {
                    bt.relocate(0,100);
                    p.relocate(600, 100);
                    pd.relocate(868.5, 100);
                    d.relocate(1137, 100);
                    pay.relocate(1637, 100);
                    resign.relocate(1637, 150);
                }
                else
                {
                    bt.relocate(0, previous.getBoundsInParent().getMaxY()+100);
                    p.relocate(600, previous.getBoundsInParent().getMaxY()+100);
                    pd.relocate(868.5, previous.getBoundsInParent().getMaxY()+100);
                    d.relocate(1137, previous.getBoundsInParent().getMaxY()+100);
                    pay.relocate(1637, previous.getBoundsInParent().getMaxY()+100);
                    resign.relocate(1637, previous.getBoundsInParent().getMaxY()+150);
                }
                previous=bt;
                pay.setOnAction((e)->pay(Integer.parseInt(pay.getId())));
                resign.setOnAction((e)-> {
                    try {
                        resign(Integer.parseInt(resign.getId()),e);
                    } catch (IOException | SQLException ioException) {
                        ioException.printStackTrace();
                    }
                });
                bt.setOnAction((e)->tripProgram(Integer.parseInt(bt.getId()),e));
                bt.setText(rs.getString(4));
                p.setText(rs.getString(6));
                pd.setText(rs.getString(5));
                d.setText(rs.getString(7));
                Statement statement2=DBStarter.conn.createStatement();
                ResultSet result2;
                try
                {
                   result2=statement2.executeQuery("Select id from trips where name=\'"+bt.getText()+"\';");
                   if(result2.next())
                   {
                       idList.add(result2.getInt(1));
                   }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                iList.add(rs.getInt(8));
                pay.setText("Pay");
                resign.setText("Resign");
                pane.getChildren().addAll(bt, p, pd, d, pay, resign);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void tripProgram(int i,ActionEvent event)
    {
        button=i;
        check=true;
        TripsController.check=false;
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/fxml/Plan.fxml"));
        try {
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(buttonList.get(i).getText());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        stage.show();
    }
    public void pay(int i)
    {
        button=i;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/resources/fxml/pay.fxml"));
        try {
            root = fxmlLoader.load();
            root.setLayoutX(350);
            root.setLayoutY(200);
            main.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void resign(int i,ActionEvent event) throws IOException, SQLException {
        statement=DBStarter.conn.createStatement();
        double r=0;
        try {
            ResultSet result=statement.executeQuery("Select trip_id from client_trips where id="+iList.get(i)+" ;");
            if(result.next()) {
                ResultSet result2 = statement.executeQuery("Select cash_back("+MenuController.id+','+result.getInt(1)+") ;");
                if(result2.next())
                {
                    r=result2.getDouble(1);
                }
            }
        }
        catch(Exception e)
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pay");
            alert.setHeaderText("Error! Try again!");
            alert.showAndWait();
            return;
        }
        alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure ?", ButtonType.YES,ButtonType.CANCEL);
        alert.setTitle("Resign");
        alert.setHeaderText("You will receive "+ Math.floor(r * 100) / 100+" return");
        Optional<ButtonType> result=alert.showAndWait();
        if(result.orElse(null).equals(ButtonType.YES))
        {
            String query="Delete from client_trips where id="+iList.get(i)+" ;";
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
            MenuController.builder.append(query).append('\n');
            MenuController.saveDB();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader=new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/myTrips.fxml"));
            try {
                AnchorPane root = fxmlLoader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("My trips");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            stage.show();
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
