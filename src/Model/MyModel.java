package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import Server.Configurations;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{

    static public Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
    static public Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());

    private Configurations config = Configurations.getInstance();
    Maze maze;
    int rowMaze;
    int colMaze;
    int rowChar;
    int colChar;
    Solution solution;

    public MyModel() {
        this.maze = null;
        this.rowChar = 0;
        this.colChar = 0;
        this.rowMaze = 20; // initial size of maze
        this.colMaze = 20;
        this.solution = null;
    }

    public String getSolverAlg(){
        String solver = "";
        try{
            solver = config.getSolverAlgorithm();
        } catch (Exception ignored) {}

        return solver;
    }

    public String getGenerateAlg(){
        String generate = "";
        try{
            generate = config.getGenAlgorithm();
        } catch (Exception ignored) {}

        return generate;
    }

    public int getRowMaze() {
        return rowMaze;
    }

    public int getColMaze() {
        return colMaze;
    }

    @Override
    public boolean checkFinish() {
        return rowChar == maze.getGoalPosition().getRowIndex() && colChar == maze.getGoalPosition().getColumnIndex();
    }

    public Solution getSolution() {
        return solution;
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

    public void start(){
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stop(){
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    @Override
    public void updateConfig(int rows, int cols, String generateAlg, String solverAlg) {
        if (rows <2 || cols<2){
            setChanged();
            notifyObservers("ErrorConfig"); // error in arguments
            return;
        }
        try { // update changes
            rowMaze = rows;
            colMaze = cols;
            config.setGenAlgorithm(generateAlg);
            config.setSolverAlgorithm(solverAlg);
            setChanged();
            notifyObservers("SetConfig"); // set Config
        } catch (Exception ignored) {} // cannot raise exception- we handle this.

    }

    public void updateLocation(int direction){
        boolean changedLocation = false;
        switch (direction) {
            // UP
            case 1 -> {
                if(rowChar>0 && maze.getMaze()[rowChar-1][colChar] == 0){
                    rowChar--;
                    changedLocation = true;
                }
            }
            // UP RIGHT
            case 2 -> {
                if(rowChar>0 && colChar<colMaze-1 && maze.getMaze()[rowChar-1][colChar+1] == 0 && (maze.getMaze()[rowChar][colChar+1] == 0 || maze.getMaze()[rowChar-1][colChar] == 0)){
                    rowChar--;
                    colChar++;
                    changedLocation = true;
                }
            }

            // Right
            case 3 -> {
                if(colChar< colMaze-1 && maze.getMaze()[rowChar][colChar+1] == 0) {
                    colChar++;
                    changedLocation = true;
                }
            }

            // DOWN RIGHT
            case 4 -> {
                if(rowChar< rowMaze-1 && colChar<colMaze-1 && maze.getMaze()[rowChar+1][colChar+1] == 0 && (maze.getMaze()[rowChar][colChar+1] == 0 || maze.getMaze()[rowChar+1][colChar] == 0)){
                    rowChar++;
                    colChar++;
                    changedLocation = true;
                }
            }

            // Down
            case 5 -> {
                if(rowChar<rowMaze-1 && maze.getMaze()[rowChar+1][colChar] == 0) {
                    rowChar++;
                    changedLocation = true;
                }
            }

            // DOWN LEFT
            case 6 -> {
                if(rowChar< rowMaze-1 && colChar>0 && maze.getMaze()[rowChar+1][colChar-1] == 0 && (maze.getMaze()[rowChar][colChar-1] == 0 || maze.getMaze()[rowChar+1][colChar] == 0)){
                    rowChar++;
                    colChar--;
                    changedLocation = true;
                }
            }

            // Left
            case 7 -> {
                if(colChar>0 && maze.getMaze()[rowChar][colChar-1] == 0) {
                    colChar--;
                    changedLocation = true;
                }
            }

            // UP LEFT
            case 8 -> {
                if(rowChar>0 && colChar>0 && maze.getMaze()[rowChar-1][colChar-1] == 0 && (maze.getMaze()[rowChar][colChar-1] == 0 || maze.getMaze()[rowChar-1][colChar] == 0)){
                    rowChar--;
                    colChar--;
                    changedLocation = true;
                }
            }

        }
        if(changedLocation){
            setChanged();
            if(checkFinish()) {
                notifyObservers("Finish");
            }
            else {
                notifyObservers("Location"); // finished update location
            }
        }
    }


    public void generateMaze(int row, int col) {
        try{
            Client client = new Client(InetAddress.getLocalHost(), 5400, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);

                                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);

                                toServer.flush();
                                int[] mazeDimensions = new int[]{row, col};
                                toServer.writeObject(mazeDimensions); //send maze dimensions to server

                                toServer.flush();
                                byte[] compressedMaze = (byte[])fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server

                                InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                                byte[] decompressedMaze = new byte[mazeDimensions[0]*mazeDimensions[1]+24 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze - with bytes
                                is.read(decompressedMaze); //Fill decompressedMaze

                                maze = new Maze(decompressedMaze);
                            } catch (Exception e) { e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();

        } catch (UnknownHostException e) {
            e.printStackTrace(); // alert instead of print TODO: what to do with the errors?
        }

        // change start position, and maze rows and cols number.
        rowChar = maze.getStartPosition().getRowIndex();
        colChar = maze.getStartPosition().getColumnIndex();
        rowMaze = maze.getRows();
        colMaze = maze.getCols();

        setChanged();
        notifyObservers("Generate"); // finished generate maze
    }

    public void solveMaze(Maze mazeToSolve) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new
                    IClientStrategy() {
                        @Override
                        public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                            try {
                                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);

                                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);

                                toServer.writeObject(mazeToSolve); //send maze to server
                                toServer.flush();
                                solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                            } catch (Exception e) { e.printStackTrace();
                            }
                        }
                    });
            client.communicateWithServer();
            setChanged();
            notifyObservers("Solve");
        } catch (UnknownHostException e) { e.printStackTrace();
        }
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }


}
