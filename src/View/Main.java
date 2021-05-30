package View;

import Model.IModel;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /*
    int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
    int screenHeight = (int) Screen.getPrimary().getBounds().getHeight();
    */

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("./MyView.fxml"));
        Parent root = fxmlLoader.load();
        /*
        // Responsive Design
        int sceneWidth = 0;
        int sceneHeight = 0;
        if (screenWidth <= 800 && screenHeight <= 600) {
            sceneWidth = 600;
            sceneHeight = 350;
        } else if (screenWidth <= 1280 && screenHeight <= 768) {
            sceneWidth = 800;
            sceneHeight = 450;
        } else if (screenWidth <= 1920 && screenHeight <= 1080) {
            sceneWidth = 1000;
            sceneHeight = 650;
        }
        */
        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(new Scene(root, 1000, 650));
        primaryStage.show();

        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        viewModel.start();
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
