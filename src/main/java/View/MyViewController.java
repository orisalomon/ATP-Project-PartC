package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import static java.lang.System.exit;

public class MyViewController implements IView, Initializable, Observer {

    public Button Solve;
    public Label playerRow;
    public Label playerCol;
    public Button music;
    public Pane mazePane;
    public ToggleButton volume;
    private Maze maze;
    public int mazeRow;
    public int mazeCol;
    public MazeDisplayer mazeDisplayer;
    private Solution solution;
    private MyViewModel viewModel;
    private Media media; // TODO: the file path should be in fxml ??
    private MediaPlayer mediaPlayer;



    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();


    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }
    public void setUpdatePlayerRow(int row) {
        this.updatePlayerRow.set(""+row);
    }
    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }
    public void setUpdatePlayerCol(int col) {
        this.updatePlayerCol.set(""+col);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);


        mazeDisplayer.widthProperty().bind(mazePane.widthProperty()); // for resizeable maze
        mazeDisplayer.heightProperty().bind(mazePane.heightProperty());

        media = new Media(new File("resources/music/gameMusic.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();

    }

    public void mouseScrolled(ScrollEvent scrollEvent) {
        if(scrollEvent.isControlDown()) {
            double zoomFactor = 1.5;
            if (scrollEvent.getDeltaY() <= 0) {
                // zoom out
                zoomFactor = 1 / zoomFactor;
            }

            zoomIn(mazeDisplayer, zoomFactor);
        }
    }

    public void zoomIn(MazeDisplayer pane, double factor){
        Scale newScale = new Scale();
        newScale.setX(pane.getScaleX() * factor);
        newScale.setY(pane.getScaleY() * factor);
        newScale.setPivotX(pane.getScaleX());
        newScale.setPivotY(pane.getScaleY());
        pane.getTransforms().add(newScale);

    }

    public void generateMaze(ActionEvent actionEvent) {
        mazeRow = viewModel.getRowMaze();
        mazeCol = viewModel.getColMaze();
        viewModel.GenerateMaze(mazeRow, mazeCol);

    }

    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze(maze);
    }

    public void saveMaze(ActionEvent actionEvent) {
        //String mazeFileName = "savedMaze.maze";

        if(maze == null){
            Alert alert = new Alert(Alert.AlertType.ERROR,"No maze created!");alert.show();return;}

        try{
            // save maze to a file
            FileChooser fileChooser = new FileChooser();
            Stage stage = new Stage();
            fileChooser.setTitle("Save Maze");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Maze files", "*.maze")
            );
            File file = fileChooser.showSaveDialog(stage);
            ObjectOutputStream out;
            if (file != null) {
                try {
                    out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
                    out.writeObject(maze);
                    out.flush();
                    out.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void exitMaze(ActionEvent actionEvent) {
        viewModel.stop(); // TODO: every client have his own servers? can multiple clients run parallel?
        exit(0);
    }

    public void loadMaze(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();
        fileChooser.setTitle("Load Maze");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Maze files", "*.maze")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                //read maze from file
                ObjectInputStream inMaze = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
                maze = (Maze) inMaze.readObject();
                inMaze.close();
                mazeDisplayer.setMaze(maze);
                mazeDisplayer.draw();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Solve.setDisable(false);
        }
    }

    public void setProperties(ActionEvent actionEvent) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 450, 450);

        Text sceneTitle = new Text("Properties");
        grid.add(sceneTitle, 0, 0);

        Label rows = new Label("Maze Rows:");
        grid.add(rows, 0, 1);

        TextField userRows = new TextField();
        userRows.setText(String.valueOf(viewModel.getRowMaze()));
        grid.add(userRows, 1, 1);

        Label Columns = new Label("Maze Columns:");
        grid.add(Columns, 0, 2);

        TextField userCols = new TextField();
        userCols.setText(String.valueOf(viewModel.getColMaze()));
        grid.add(userCols, 1, 2);

        Label generateAlgorithm = new Label("Maze generation algorithm:");
        grid.add(generateAlgorithm, 0,3);


        ComboBox<String> comboGenerate = new ComboBox<>();
        comboGenerate.getItems().addAll("Empty","Simple","My");

        String currGen = "";

        try{
            currGen = viewModel.getGenAlg();

        } catch (Exception ignored) {
        }

        //comboGenerate.getSelectionModel().select(currGenText);
        comboGenerate.setValue(currGen);

        grid.add(comboGenerate, 1,3);


        Label solverAlgorithm = new Label("Maze solver algorithm:");
        grid.add(solverAlgorithm, 0,4);

        ComboBox<String> comboSolver = new ComboBox<>();
        comboSolver.getItems().addAll("BFS","DFS","BEST");

        String currSolver = "";
        try{
            currSolver = viewModel.getSolverAlg();
        } catch (Exception e) {
        }

        //comboSolver.getSelectionModel().select(currSolverText);
        comboSolver.setValue(currSolver);

        grid.add(comboSolver, 1,4);

        Button submit = new Button();
        submit.setText("Submit");
        Stage stage = new Stage();
        stage.setTitle("Properties");
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    int userRowsInt = Integer.parseInt(userRows.getText());
                    int userColsInt = Integer.parseInt(userCols.getText());
                    viewModel.updateConfig(userRowsInt,userColsInt,comboGenerate.getValue(),comboSolver.getValue());
                    stage.close();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,"Rows and Cols must be positive integer!").show();
                }
            }});
        grid.add(submit,3,5);

        stage.setScene(scene);
        stage.show();

    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.updateMove(keyEvent);
        keyEvent.consume();
    }

   public void getFocus(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }



    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof MyViewModel) {
            switch ((String) arg) {
                case "Generate" -> {
                    maze = viewModel.getMaze();
                    mazeDisplayer.setMaze(maze);
                    mazeDisplayer.setSolved(false);
                    mazeDisplayer.setPlayerPosition(maze.getStartPosition().getRowIndex(),maze.getStartPosition().getColumnIndex());
                    Solve.setDisable(false);

                    // request focus for the game
                    mazeDisplayer.requestFocus();

                    // update player location (bind)
                    setUpdatePlayerRow(viewModel.getRowChar());
                    setUpdatePlayerCol(viewModel.getColChar());
                    mazeDisplayer.draw();

                }
                case "Solve" -> {
                    solution = viewModel.getSolution();
                    mazeDisplayer.setSolved(true);
                    mazeDisplayer.setSolution(solution);
                    mazeDisplayer.draw();
                    mazeDisplayer.requestFocus();
                }
                case "Location" -> {

                    // update player location (bind)
                    setUpdatePlayerRow(viewModel.getRowChar());
                    setUpdatePlayerCol(viewModel.getColChar());

                    // update player location (real position)
                    mazeDisplayer.setPlayerPosition(viewModel.getRowChar(), viewModel.getColChar());

                }

                case "ErrorConfig" -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Rows and cols must be greater than 2!");
                    alert.setTitle("Error");
                    alert.show();
                }

                case "SetConfig" -> {
                    mazeRow = viewModel.getRowMaze();
                    mazeCol = viewModel.getColMaze();
                }
                case "Finish" -> {
                    drawWin();  // draw new stage with win image

                }


            }

        }
    }

    public void drawWin(){

        // change music
        Media winMedia = new Media(new File("resources/music/winSong.mp3").toURI().toString());
        MediaPlayer mediaPlayerWin = new MediaPlayer(winMedia);
        mediaPlayer.pause(); // stop current music
        mediaPlayerWin.setAutoPlay(true);
        mediaPlayerWin.setVolume(1.25);
        mediaPlayerWin.play(); // play win song


        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        //Setting title to the Stage
        stage.setTitle("Winner!");

        stage.setOnCloseRequest(e -> {
            // disable option to quit the window
            e.consume();

        });

        //Creating an image
        Image winImage = null;
        try {
            winImage = new Image(new FileInputStream(mazeDisplayer.getImageFileNameWin()));
        } catch (FileNotFoundException e) {
            System.out.println("No win image found!");
        }

        //Setting the image view
        ImageView imageView = new ImageView(winImage);

        //Setting the position of the image
//        imageView.setX(50);
//        imageView.setY(25);

        //setting the fit height and width of the image view
//        imageView.setFitHeight(500);
//        imageView.setFitWidth(600);

        //Setting the preserve ratio of the image view
        imageView.setPreserveRatio(true);

        //Creating a Group object
        Group root = new Group(imageView);


        Button playAgain = new Button();
        playAgain.setText("Play Again");
        playAgain.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    // set background music
                    mediaPlayerWin.stop();
                    mediaPlayer.play();
                    generateMaze(e);
                    stage.close();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,"Rows and Cols must be positive integer!").show();
                }
            }});

        //root.getChildren().add(playAgain); // add button to win scene

        Button quit = new Button();
        quit.setText("Quit game");
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { exitMaze(e);}}
        );

        // set buttons inside the grid.
        GridPane grid = new GridPane();
        grid.add(playAgain,0,0);
        grid.add(quit,0,1);
        root.getChildren().add(grid); // add button to win scene


        //Creating a scene object
        Scene scene = new Scene(root, 500, 700);

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

    }

    public void setMusic(ActionEvent actionEvent) {
        switch (music.getText()){
            case "Turn Off Music!":
                mediaPlayer.pause();
                music.setText("Turn On Music!");
                break;
            case "Turn On Music!":
                mediaPlayer.play();
                music.setText("Turn Off Music!");
                break;
        }
        mazeDisplayer.requestFocus();
    }

    public void mouseDragged(MouseEvent mouseEvent) {

        double canvasHeight = mazeDisplayer.getHeight(); // canvasHeight
        double canvasWidth = mazeDisplayer.getWidth();// canvasWidth

        int rows = maze.getRows();
        int cols = maze.getCols();

        double cellHeight = canvasHeight / rows;
        double cellWidth = canvasWidth / cols;

        // calculate current mouse position by cells.
        double mouseX =(int) ((mouseEvent.getX()) / cellWidth);
        double mouseY =(int) ((mouseEvent.getY()) / cellHeight);

        // UP DOWN LEFT RIGHT
        if (mouseY < viewModel.getRowChar() && mouseX == viewModel.getColChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD8","NUMPAD8",KeyCode.NUMPAD8,false,false,false,false));
        }
        if (mouseY > viewModel.getRowChar() && mouseX == viewModel.getColChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD2","NUMPAD2",KeyCode.NUMPAD2,false,false,false,false));
        }
        if (mouseX < viewModel.getColChar() && mouseY == viewModel.getRowChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD4","NUMPAD4",KeyCode.NUMPAD4,false,false,false,false));
        }
        if (mouseX > viewModel.getColChar() && mouseY == viewModel.getRowChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD6","NUMPAD6",KeyCode.NUMPAD6,false,false,false,false));
        }

        // diagonals
        if (mouseY < viewModel.getRowChar() && mouseX > viewModel.getColChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD9","NUMPAD9",KeyCode.NUMPAD9,false,false,false,false));
        }
        if (mouseY > viewModel.getRowChar() && mouseX > viewModel.getColChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD3","NUMPAD3",KeyCode.NUMPAD3,false,false,false,false));
        }
        if (mouseX < viewModel.getColChar() && mouseY > viewModel.getRowChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD1","NUMPAD1",KeyCode.NUMPAD1,false,false,false,false));
        }
        if (mouseX < viewModel.getColChar() && mouseY < viewModel.getRowChar()) {
            viewModel.updateMove(new KeyEvent(KeyEvent.KEY_PRESSED,"NUMPAD7","NUMPAD7",KeyCode.NUMPAD7,false,false,false,false));
        }


    }

}
