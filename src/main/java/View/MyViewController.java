package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
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
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
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
    public MazeDisplayer mazeDisplayer;
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
        viewModel.GenerateMaze(viewModel.getRowMaze(), viewModel.getColMaze());
    }

    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze(viewModel.getMaze());
    }

    public void saveMaze(ActionEvent actionEvent) {

        if(viewModel.getMaze() == null){
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
                    out.writeObject(viewModel.getMaze());
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
                Maze maze = (Maze) inMaze.readObject();
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

        // threads
        Label threadsNum = new Label("Threads Number:");
        grid.add(threadsNum, 0, 0);
        TextField userThreads = new TextField();
        userThreads.setText(String.valueOf(viewModel.getThreadsNum()));
        grid.add(userThreads, 1, 0);

        // maze rows
        Label rows = new Label("Maze Rows:");
        grid.add(rows, 0, 1);
        TextField userRows = new TextField();
        userRows.setText(String.valueOf(viewModel.getRowMaze()));
        grid.add(userRows, 1, 1);

        // maze columns
        Label Columns = new Label("Maze Columns:");
        grid.add(Columns, 0, 2);
        TextField userCols = new TextField();
        userCols.setText(String.valueOf(viewModel.getColMaze()));
        grid.add(userCols, 1, 2);

        // maze generation algorithm
        Label generateAlgorithm = new Label("Maze generation algorithm:");
        grid.add(generateAlgorithm, 0,3);
        ComboBox<String> comboGenerate = new ComboBox<>();
        comboGenerate.getItems().addAll("Empty","Simple","My");

        String currGen = "";

        try{
            currGen = viewModel.getGenAlg();

        } catch (Exception ignored) {
        }

        comboGenerate.setValue(currGen);

        grid.add(comboGenerate, 1,3);

        // maze solver algorithm
        Label solverAlgorithm = new Label("Maze solver algorithm:");
        grid.add(solverAlgorithm, 0,4);
        ComboBox<String> comboSolver = new ComboBox<>();
        comboSolver.getItems().addAll("BFS","DFS","BEST");

        String currSolver = "";
        try{
            currSolver = viewModel.getSolverAlg();
        } catch (Exception e) {
        }
        comboSolver.setValue(currSolver);
        grid.add(comboSolver, 1,4);

        // submit button
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
                    int userThreadsInt = Integer.parseInt(userThreads.getText());

                    if(userRowsInt < 2 || userColsInt <2){
                        new Alert(Alert.AlertType.ERROR,"Rows and Cols must be positive integers greater than 2!").show();
                        viewModel.writeErrorToLog();
                        return;
                    }

                    if(userThreadsInt < 1){
                        new Alert(Alert.AlertType.ERROR,"Threads number must be a positive integer!").show();
                        viewModel.writeErrorToLog();
                        return;
                    }

                    viewModel.updateConfig(userThreadsInt,userRowsInt,userColsInt,comboGenerate.getValue(),comboSolver.getValue());
                    stage.close();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR,"Threads number, rows and cols must have integers only!").show();
                    viewModel.writeErrorToLog();
                }
            }});
        grid.add(submit,3,5);

        stage.setScene(scene);
        stage.show();

    }

    public void setAbout(ActionEvent actionEvent) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 800, 450);

        //Creating a Text object
        Text text = new Text();
        Text header = new Text();

        //Setting font to the text
        header.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.ITALIC, 30));
        text.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));


        String txtHeader = new String("About");
        String txt = new String("Hello User,\n" +
                "Our names are Ori and Dor, and we are 2nd year students for Information Systems Engineering at BGU.\n" +
                "This game was created at 'Advanced Programming Topics' course.\n" +
                "The algorithms that we used to generate our mazes are:\n" +
                "\t1) Empty - clean maze.\n" +
                "\t2) Simple - random generating walls with a constant solution.\n" +
                "\t3) My - DFS algorithm implementation for creation of mazes.\n" +
                "The algorithms that we used to solve out mazes are:\n" +
                "\t1) BFS algorithm.\n" +
                "\t2) DFS algorithm.\n" +
                "\t3) BestFS algorithm.\n" +
                "Hope you will enjoy!\n" +
                "Ori and Dor.");


        //Setting the text to be added.
        text.setText(txt);
        header.setText(txtHeader);

        //Creating a Group object
        grid.add(header,0,0);
        grid.add(text,0,1);

        //Setting title to the Stage
        Stage stage = new Stage();
        stage.setTitle("About");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
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
                    mazeDisplayer.setMaze(viewModel.getMaze());
                    mazeDisplayer.setSolved(false);
                    mazeDisplayer.setPlayerPosition(viewModel.getMaze().getStartPosition().getRowIndex(),viewModel.getMaze().getStartPosition().getColumnIndex());
                    Solve.setDisable(false);

                    // request focus for the game
                    mazeDisplayer.requestFocus();

                    // update player location (bind)
                    setUpdatePlayerRow(viewModel.getRowChar());
                    setUpdatePlayerCol(viewModel.getColChar());
                    mazeDisplayer.draw();

                }
                case "Solve" -> {
                    mazeDisplayer.setSolved(true);
                    mazeDisplayer.setSolution(viewModel.getSolution());
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
                case "SetConfig" -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,"Settings changed successfully!");
                    alert.setTitle("Settings changed");
                    alert.show();
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
                    music.setText("Turn Off Music!");
                    generateMaze(e);
                    stage.close();
                }
                catch (Exception ignored) {}
            }});


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

        int rows = viewModel.getRowMaze();
        int cols = viewModel.getColMaze();

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

    public void setHelp(ActionEvent actionEvent) {

        // load images
        //Creating an image
        Image imageExit = null;
        Image imageFile = null;
        Image imageFinish = null;
        Image imageProperties = null;
        Image imagePropertiesOptions = null;
        Image imageSolve = null;
        Image imageSolvePath = null;
        Image imageMusic = null;
        Image imagePlayerPosition = null;
        try {
            imageExit = new Image(new FileInputStream("./resources/images/help_ExitGame.jpg"));
            imageFile = new Image(new FileInputStream("./resources/images/help_FileGame.jpg"));
            imageFinish = new Image(new FileInputStream("./resources/images/help_FinishGame.jpg"));
            imageProperties = new Image(new FileInputStream("./resources/images/help_PropGame.jpg"));
            imagePropertiesOptions = new Image(new FileInputStream("./resources/images/help_PropertiesOptionsGame.jpg"));
            imageSolve = new Image(new FileInputStream("./resources/images/help_SolveGame.jpg"));
            imageSolvePath = new Image(new FileInputStream("./resources/images/help_SolvePathGame.jpg"));
            imageMusic = new Image(new FileInputStream("./resources/images/help_turnOnMusicGame.jpg"));
            imagePlayerPosition = new Image(new FileInputStream("./resources/images/help_PlayerPositionGame.jpg"));
        } catch (FileNotFoundException e) {
            System.out.println("Help images not found!");
        }

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(25, 25, 25, 25));


        //Creating a Text objects
        Text header = new Text("Help"); // header
        // all the rest
        Text textExit = new Text("Here you can exit the game.");
        Text textFile = new Text("Here you can start new game, load existing game, or save the current game in your computer.");
        Text textFinish = new Text("That's the goal point for finish the game. GO THERE!");
        Text textProperties = new Text("Here you can change the game options.");
        Text textPropertiesOptions = new Text("here is the options that you can change. the text fields must be integers, rows and cols must be greater than 2. ");
        Text textSolve = new Text("Here you can ask for help to solve the current maze.");
        Text textSolvePath = new Text("That how the path of the solution will looks like.");
        Text textMusic = new Text("Here you can turn on/off the background music.");
        Text playerPosition = new Text("Here you can see the position of the player inside the board.");


        //Setting font to the text
        header.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.ITALIC, 30));
        textExit.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        textFile.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        textFinish.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        textProperties.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        textPropertiesOptions.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        textSolve.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        textSolvePath.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        textMusic.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));
        playerPosition.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 15));


        // add text to the grid.
        grid.add(textFile,1,0);
        grid.add(playerPosition,1,1);
        grid.add(textMusic,1,2);
        grid.add(textFinish,1,3);
        grid.add(textSolve,1,4);
        grid.add(textSolvePath,1,5);
        grid.add(textProperties,1,6);
        grid.add(textPropertiesOptions,1,7);
        grid.add(textExit,1,8);
        GridPane.setHalignment(textFile, HPos.CENTER);
        GridPane.setHalignment(playerPosition, HPos.CENTER);
        GridPane.setHalignment(textMusic, HPos.CENTER);
        GridPane.setHalignment(textFinish, HPos.CENTER);
        GridPane.setHalignment(textSolve, HPos.CENTER);
        GridPane.setHalignment(textSolvePath, HPos.CENTER);
        GridPane.setHalignment(textProperties, HPos.CENTER);
        GridPane.setHalignment(textPropertiesOptions, HPos.CENTER);
        GridPane.setHalignment(textExit, HPos.CENTER);

        // add photos to the grid
        grid.add(new ImageView(imageFile),0,0);
        grid.add(new ImageView(imagePlayerPosition),0,1);
        grid.add(new ImageView(imageMusic),0,2);
        grid.add(new ImageView(imageFinish),0,3);
        grid.add(new ImageView(imageSolve),0,4);
        grid.add(new ImageView(imageSolvePath),0,5);
        grid.add(new ImageView(imageProperties),0,6);
        grid.add(new ImageView(imagePropertiesOptions),0,7);
        grid.add(new ImageView(imageExit),0,8);


        grid.setGridLinesVisible(true);
        Stage stage = new Stage();
        stage.setTitle("Help");

        VBox box = new VBox();
        Scene scene = new Scene(box, 1300, 600);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);
        box.getChildren().addAll(header,scrollPane);

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();

    }
}
