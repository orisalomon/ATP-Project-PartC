package View;

import Model.MyModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static MyModel model = new MyModel();
    @Override
    public void start(Stage primaryStage) throws Exception{
        model.start();
        Parent root = FXMLLoader.load(getClass().getResource("MyView.fxml"));
        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show(); // TODO: set the right configuration file.
    }


    public static void main(String[] args) {
        launch(args);
    }
}
