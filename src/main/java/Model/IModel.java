package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    Maze getMaze();
    int getColChar();
    int getRowChar();
    Solution getSolution();
    void generateMaze(int row, int col);
    void updateLocation(int direction);
    void solveMaze(Maze mazeToSolve);
    void assignObserver(Observer o);
    void start();
    void stop();
    void updateConfig(int threadSize, int rows, int cols, String generateAlg, String solverAlg);
    String getSolverAlg();
    String getGenerateAlg();
    int getRowMaze();
    int getColMaze();
    boolean checkFinish();

    void writeErrorToLog();

    int getThreadsNum();
}
