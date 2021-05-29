package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    IModel model;
    int rowChar;
    int colChar;
    Maze maze;
    int rowMaze;
    int colMaze;
    String genAlg;
    String solverAlg;
    Solution solution;


    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
        maze = null;
        rowChar = 0;
        colChar = 0;
        solution = null;

        // get data from model + initial data
        rowMaze=model.getRowMaze();
        colMaze=model.getColMaze();
        genAlg = model.getGenerateAlg();
        solverAlg = model.getSolverAlg();


    }

    public int getRowMaze() {
        return rowMaze;
    }

    public int getColMaze() {
        return colMaze;
    }

    public String getGenAlg() {
        return genAlg;
    }

    public String getSolverAlg() {
        return solverAlg;
    }

    public int getRowChar() {
        return rowChar;
    }

    public int getColChar() {
        return colChar;
    }

    public Maze getMaze() {
        return maze;
    }

    public Solution getSolution() {
        return solution;
    }

    @Override
    public void update(Observable o, Object arg) {

        if(o instanceof IModel) {
            switch ((String) arg) {
                case "Generate" -> {
                    maze = model.getMaze();
                    setChanged();
                    notifyObservers("Generate");
                }
                case "Solve" -> {
                    solution = model.getSolution();
                    setChanged();
                    notifyObservers("Solve");
                }
                case "Location" -> {
                    rowChar = model.getRowChar();
                    colChar = model.getColChar();
                    setChanged();
                    notifyObservers("Location");
                }
                case "ErrorConfig" -> {
                    setChanged();
                    notifyObservers("ErrorConfig");
                }
                case "SetConfig" -> {
                    rowMaze = model.getRowMaze();
                    colMaze = model.getColMaze();
                    genAlg = model.getGenerateAlg();
                    solverAlg = model.getSolverAlg();
                    setChanged();
                    notifyObservers("SetConfig");
                }
                case "Finish" -> {
                    setChanged();
                    notifyObservers("Finish");
                }
            }

        }
    }

    public void GenerateMaze(int row, int col){
        model.generateMaze(row,col);
    }

    public void updateMove(KeyEvent keyEvent){
        int direction = -1;
        switch (keyEvent.getCode()) {
            case UP -> direction=1;
            case DOWN -> direction=2;
            case LEFT -> direction=3;
            case RIGHT -> direction=4;
        }
        model.updateLocation(direction);
    }

    public void solveMaze(Maze maze){
        model.solveMaze(maze);
    }


    public void stop() {
        model.stop();
    }

    public void start() {
        model.start();
    }

    public void updateConfig(int rows, int cols, String generateAlg, String solverAlg) {
        model.updateConfig(rows,cols,generateAlg,solverAlg);
    }
}
