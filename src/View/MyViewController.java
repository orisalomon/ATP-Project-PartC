package View;

import Server.Configurations;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
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
    public Label timer;
    private Maze maze;
    public int row= 20;
    public int col= 20;
    public MazeDisplayer mazeDisplayer;
    private Solution solution;
    private Configurations config = Configurations.getInstance();
    private MyViewModel viewModel;



    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();
    StringProperty updateTimer = new SimpleStringProperty();

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

    public String getUpdateTimer() {
        return updateTimer.get();
    }


    public void setUpdateTimer(String time) {
        this.updateTimer.set(time);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
        timer.textProperty().bind(updateTimer);
    }

    public void generateMaze(ActionEvent actionEvent) {

        viewModel.GenerateMaze(row,col);

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
            File file = fileChooser.showSaveDialog(stage);
            ObjectOutputStream out;
            if (file != null) {
                try {
                    out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()+".maze"));
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
                new FileChooser.ExtensionFilter("All Mazes", "*.maze")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                //read maze from file
                ObjectInputStream inMaze = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
                maze = (Maze) inMaze.readObject();
                inMaze.close();
                mazeDisplayer.drawMaze(maze);
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

        Scene scene = new Scene(grid, 400, 400);

        Text sceneTitle = new Text("Properties");
        grid.add(sceneTitle, 0, 0);

        Label rows = new Label("Maze Rows:");
        grid.add(rows, 0, 1);

        TextField userRows = new TextField();
        userRows.setText(String.valueOf(row));
        grid.add(userRows, 1, 1);

        Label Columns = new Label("Maze Columns:");
        grid.add(Columns, 0, 2);

        TextField userCols = new TextField();
        userCols.setText(String.valueOf(col));
        grid.add(userCols, 1, 2);

        Label generateAlgorithm = new Label("Maze generation algorithm:");
        grid.add(generateAlgorithm, 0,3);


        ComboBox<String> comboGenerate = new ComboBox<>();
        comboGenerate.getItems().addAll("Empty","Simple","My");

        String currGen = "";

        try{
            currGen = config.getGenAlgorithm();

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
            currSolver = config.getSolverAlgorithm();
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
                    if (userRowsInt <= 0){new Alert(Alert.AlertType.ERROR,"Rows must be positive integer!").show();}

                    int userColsInt = Integer.parseInt(userCols.getText());
                    if (userColsInt <= 0){new Alert(Alert.AlertType.ERROR,"Columns must be positive integer!").show();}

                    row = userRowsInt;
                    col = userColsInt;
                    config.setGenAlgorithm(comboGenerate.getValue());
                    config.setSolverAlgorithm(comboSolver.getValue());

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
                    mazeDisplayer.drawMaze(maze);
                    Solve.setDisable(false);
                }
                case "Solve" -> {
                    solution = viewModel.getSolution();
                    mazeDisplayer.drawSolution(solution);
                }
                case "Location" -> {

                    // update player location (bind)
                    setUpdatePlayerRow(viewModel.getRowChar());
                    setUpdatePlayerCol(viewModel.getColChar());

                    // update player location (real position)
                    mazeDisplayer.setPlayerPosition(viewModel.getRowChar(), viewModel.getColChar());

                }
            }

        }
    }
}
