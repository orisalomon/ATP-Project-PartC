package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{

    static public Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
    static public Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    Maze maze;
    int rowChar;
    int colChar;
    Solution solution;

    public MyModel() {
        this.maze = null;
        this.rowChar = 0;
        this.colChar = 0;
        this.solution = null;
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

    public void updateLocation(int direction){
        switch (direction) {
            // UP
            case 1 -> rowChar--;

            // Down
            case 2 -> rowChar++;

            // Left
            case 3 -> colChar--;

            // Right
            case 4 -> colChar++;
        }
        setChanged();
        notifyObservers("Location"); // finished update location
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
            e.printStackTrace(); // alert instead of print
        }

        // change start position
        rowChar = maze.getStartPosition().getRowIndex();
        colChar = maze.getStartPosition().getColumnIndex();

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
