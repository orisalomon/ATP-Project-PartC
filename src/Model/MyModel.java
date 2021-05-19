package Model;

import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;

public class MyModel implements IModel{

    static public Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
    static public Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());

    public void start(){
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stop(){
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }




}
