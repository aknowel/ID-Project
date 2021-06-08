package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sample.DBStarter;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProgramController {
    @FXML
    AnchorPane pane;
    Stage stage;
    Button previous=null;
    Alert alert;
    List<Integer> tripIdList=new ArrayList<>();
    public void initialize()
    {
        int tripId=(TripsController.check) ? TripsController.controller.getTid() : MyTripsController.controller.getTid();
        pane.setStyle("-fx-background-color: #11ff33");
        Label date=new Label();
        date.setPrefSize(600, 100);
        date.relocate(0, 0);
        date.setStyle("-fx-font-size: 40; -fx-background-color: #ffff00; -fx-font-weight: bold");
        date.setText("Date");
        date.setAlignment(Pos.CENTER);
        pane.getChildren().add(date);
        String query="select starting_date, td.id from trips t join trip_dates td on t.id=td.trip_id where t.id=" + tripId +" order by 1;";
        try{
            Statement stmt= DBStarter.conn.createStatement();
            ResultSet rs = stmt.executeQuery( query );
            int ii=0;
            while(rs.next())
            {
                Label d=new Label();
                Button s=new Button();
                s.setId(String.valueOf(ii));
                ii++;
                tripIdList.add(rs.getInt(2));
                d.setPrefSize(600, 100);
                s.setPrefSize(268.5, 100);
                d.setStyle("-fx-font-size: 35;" +
                        " -fx-text-fill: #ee2211; -fx-font-style: italic; -fx-background-color: #eeeeee; -fx-border-color: #22aa33");
                s.setStyle(MyTripsController.style);
                s.setOnAction((e)-> {
                    try {
                        sigUp(Integer.parseInt(s.getId()));
                    } catch (SQLException | IOException throwable) {
                        throwable.printStackTrace();
                    }
                });
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
                    + tripId + ';');
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
            + tripId + " order by 3;");
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
    public void back(ActionEvent event) {
        if (MyTripsController.check) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/myTrips.fxml"));
            try {
                AnchorPane root = fxmlLoader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("My trips");
            } catch (Exception e) {
                e.printStackTrace();
            }
            stage.show();
        }
        else
        {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/resources/fxml/Trips.fxml"));
            try {
                AnchorPane root = fxmlLoader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Trips");
            } catch (Exception e) {
                e.printStackTrace();
            }
            stage.show();
        }
    }
    public void sigUp(int i) throws SQLException, IOException {
        if(MenuController.status) {
            String query = "INSERT INTO client_trips(trip_id,client_id,paid_amount) VALUES(?,?, ?)";
            PreparedStatement pst = DBStarter.conn.prepareStatement(query);
            pst.setInt(1, tripIdList.get(i));
            pst.setInt(2, MenuController.id);
            pst.setInt(3, 0);
            try {
                pst.executeUpdate();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                    alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Trips");
                    alert.setHeaderText("Trip has already had max amount of people");
                    alert.showAndWait();
                    return;
            }
            MenuController.builder.append(pst.toString()).append(';').append('\n');
            MenuController.saveDB();
            alert=new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Trips");
            alert.setHeaderText("Successfully added!");
            alert.showAndWait();
        }
        else
        {
            alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Trips");
            alert.setHeaderText("Error! You have to sign in!");
            alert.showAndWait();
        }
    }
}
