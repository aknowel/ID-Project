package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProgramController {
    @FXML
    AnchorPane pane;
    Stage stage;
    Button previous=null;
    public void initialize()
    {
        pane.setStyle("-fx-background-color: #11ff33");
        Label date=new Label();
        date.setPrefSize(600, 100);
        date.relocate(0, 0);
        date.setStyle("-fx-font-size: 40; -fx-background-color: #ffff00; -fx-font-weight: bold");
        date.setText("Date");
        date.setAlignment(Pos.CENTER);
        pane.getChildren().add(date);
        String query="select starting_date from trips t join trip_dates td on t.id=td.trip_id where t.id=" + TripsController.controller.getTid() +';';
        try{
            Statement stmt= DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery( query );
            while(rs.next())
            {
                Label d=new Label();
                Button s=new Button();
                d.setPrefSize(600, 100);
                s.setPrefSize(268.5, 100);
                d.setStyle("-fx-font-size: 35;" +
                        " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
                s.setStyle(MyTripsController.style);
                d.setAlignment(Pos.CENTER);
                if(previous==null)
                {
                    d.relocate(0, 100);
                    s.relocate(600, 100);
                }
                else
                {
                    d.relocate(0, previous.getBoundsInParent().getMaxY()+100);
                    s.relocate(600, previous.getBoundsInParent().getMaxY()+100);
                }
                d.setText(rs.getString(1));
                s.setText("Sign up");
                previous=s;
                pane.getChildren().addAll(d, s);
            }
            ResultSet r= stmt.executeQuery("select a.name from trip_attractions ta join trips t on ta.trip_id=t.id join attractions a on ta.attraction_id=a.id where t.id="
                    + TripsController.controller.getTid() + ';');
            Label h=new Label();
            h.setStyle("-fx-font-size: 20");
            h.relocate(0, previous.getBoundsInParent().getMaxY()+100);
            h.setText("Visited attractions:");
            pane.getChildren().add(h);
            int i=1;
            while(r.next())
            {
                Label a=new Label();
                a.setStyle("-fx-font-size: 20");
                a.relocate(0, previous.getBoundsInParent().getMaxY()+100 + 22*i );
                a.setText('-' + r.getString(1));
                pane.getChildren().add(a);
                i++;
            }
            Label nh=new Label();
            nh.setStyle("-fx-font-size: 20");
            double z=previous.getBoundsInParent().getMaxY()+100+22*i;
            nh.relocate(0 , z);
            nh.setText("Route:");
            pane.getChildren().add(nh);
            int j=1;
            ResultSet rr=stmt.executeQuery("select city_of_id(t.from_city), city_of_id(t.to_city), tr.on_day, t.kind from trip_routes tr join travels t on tr.travel_id=t.id where tr.trip_id="
            + TripsController.controller.getTid() + " order by 3;");
            while(rr.next())
            {
                Label o=new Label();
                o.setStyle("-fx-font-size: 20");
                o.relocate(0, z+22*j);
                j++;
                o.setText('-' + rr.getString(1) + " -> " + rr.getString(2) + " on day " + rr.getInt(3) + " by " + rr.getString(4));
                pane.getChildren().add(o);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void back(ActionEvent event)
    {
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
