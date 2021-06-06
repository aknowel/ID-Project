package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TripsController {
    @FXML
    AnchorPane pane;
    @FXML
    ScrollPane scroll;
    @FXML
    Button left;
    @FXML
    Button right;
    @FXML
    ImageView iLeft;
    @FXML
    ImageView iRight;
    Stage stage;
    Button previous=null;
    boolean setName=false;
    boolean setPrice=false;
    List<Label> labelList=new ArrayList<>();
    List<Button> buttonList=new ArrayList<>();
    public void initialize()
    {
        pane.setStyle("-fx-background-color: #11ff33");
        Label name=new Label();
        Label base_price=new Label();
        name.setPrefSize(600, 100); base_price.setPrefSize(268.5, 100);
        name.relocate(0 ,0); base_price.relocate(600, 0);
        name.setText("Trip Name"); base_price.setText("Base Price");
        name.setAlignment(Pos.CENTER); base_price.setAlignment(Pos.CENTER);
        name.setStyle("-fx-font-size: 40; -fx-background-color: #ffff00; -fx-font-weight: bold");
        base_price.setStyle("-fx-font-size: 40; -fx-font-weight: bold");
        pane.getChildren().addAll(name, base_price);
        try {
            set("select * from trips;");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void sortName()
    {
        if(!setName) {
            try {
                Statement stmt = DBStarter.conn.createStatement();
                ResultSet rs = stmt.executeQuery("select * from trips order by name");
                int i=0;
                while(rs.next())
                {
                    buttonList.get(i).setText(rs.getString(2));
                    labelList.get(i).setText(rs.getString(3));
                    i++;
                }
                setName=true;
                iLeft.setImage(new Image("/resources/images/sort.png"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                Statement stmt = DBStarter.conn.createStatement();
                ResultSet rs = stmt.executeQuery("select * from trips order by name desc");
                int i=0;
                while(rs.next())
                {
                    buttonList.get(i).setText(rs.getString(2));
                    labelList.get(i).setText(rs.getString(3));
                    i++;
                }
                setName=false;
                iLeft.setImage(new Image("/resources/images/sortDesc.png"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void sortPrice()
    {
        if(!setPrice) {
            try {
                Statement stmt = DBStarter.conn.createStatement();
                ResultSet rs = stmt.executeQuery("select * from trips order by base_price");
                int i=0;
                while(rs.next())
                {
                    buttonList.get(i).setText(rs.getString(2));
                    labelList.get(i).setText(rs.getString(3));
                    i++;
                }
                setPrice=true;
                iRight.setImage(new Image("/resources/images/sort.png"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                Statement stmt = DBStarter.conn.createStatement();
                ResultSet rs = stmt.executeQuery("select * from trips order by base_price desc");
                int i=0;
                while(rs.next())
                {
                    buttonList.get(i).setText(rs.getString(2));
                    labelList.get(i).setText(rs.getString(3));
                    i++;
                }
                setPrice=false;
                iRight.setImage(new Image("/resources/images/sortDesc.png"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
    private void set(String query) throws SQLException {
        Statement stmt=DBStarter.conn.createStatement();
        ResultSet rs = stmt.executeQuery( query );
        while(rs.next())
        {
            Button bt = new Button();
            buttonList.add(bt);
            Label p=new Label();
            labelList.add(p);
            bt.setPrefSize(600, 100);
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
            p.setStyle("-fx-font-size: 35;" +
                    " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
            p.setPrefSize(268.5, 100);
            p.setAlignment(Pos.CENTER);
            if(previous==null)
            {
                bt.relocate(0,100);
                p.relocate(600, 100);
            }
            else
            {
                bt.relocate(0, previous.getBoundsInParent().getMaxY()+100);
                p.relocate(600, previous.getBoundsInParent().getMaxY()+100);
            }
            previous=bt;
            bt.setText(rs.getString(2));
            p.setText(rs.getString(3));
            pane.getChildren().add(bt);
            pane.getChildren().add(p);
        }
    }
}
