package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this);
    }

    public int getRowMaze() {
        return model.getRowMaze();
    }

    public int getColMaze() {
        return model.getColMaze();
    }

    public String getGenAlg() {
        return model.getGenerateAlg();
    }

    public String getSolverAlg() {
        return model.getSolverAlg();
    }

    public int getRowChar() {
        return model.getRowChar();
    }

    public int getColChar() {
        return model.getColChar();
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public Solution getSolution() {
        return model.getSolution();
    }

    @Override
    public void update(Observable o, Object arg) {

        if(o instanceof IModel) {
            setChanged();
            notifyObservers(arg);
        }
    }

    public void GenerateMaze(int row, int col){
        model.generateMaze(row,col);
    }

    public void updateMove(KeyEvent keyEvent){
        int direction = -1;
        switch (keyEvent.getCode()) {
            case NUMPAD8 -> direction=1; // UP
            case NUMPAD9 -> direction=2; // UP RIGHT
            case NUMPAD6 -> direction=3; // RIGHT
            case NUMPAD3 -> direction=4; // DOWN RIGHT
            case NUMPAD2 -> direction=5; // DOWN
            case NUMPAD1 -> direction=6; // DOWN LEFT
            case NUMPAD4 -> direction=7; // LEFT
            case NUMPAD7 -> direction=8; // UP LEFT
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

    public void updateConfig(int threadSize, int rows, int cols, String generateAlg, String solverAlg) {
        model.updateConfig(threadSize,rows,cols,generateAlg,solverAlg);
    }

    public void writeErrorToLog() {
        model.writeErrorToLog();
    }

    public int getThreadsNum() {
        return model.getThreadsNum();
    }
}
