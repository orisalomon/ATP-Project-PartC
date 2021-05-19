package View;

import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MazeDisplayer extends Canvas {
    private int[][] maze;
    private Solution solution;

    public void drawMaze(int[][] maze) {
        this.maze = maze;

        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;

            double cellHeight = canvasHeight/rows;
            double cellWidth = canvasWidth/cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            // clear the canvas
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);

            graphicsContext.setFill(Color.BLACK);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if(maze[i][j] == 1){
                        // if it is wall:
                        double x = i*cellHeight;
                        double y = j*cellWidth;
                        graphicsContext.fillRect(y,x,cellWidth,cellHeight);
                    }
                }
            }
        }
    }

    public void drawSolution(Solution sol) {
        this.solution = sol;

        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.length;
            int cols = maze[0].length;

            double cellHeight = canvasHeight/rows;
            double cellWidth = canvasWidth/cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            // clear the canvas

            graphicsContext.setFill(Color.GREEN);

            for (AState state: solution.getSolutionPath()
                 ) {
                double x = ((MazeState)state).getPosition().getRowIndex()*cellHeight;
                double y = ((MazeState)state).getPosition().getColumnIndex()*cellWidth;
                graphicsContext.fillRect(y,x,cellWidth,cellHeight);
            }


        }


    }

    private void draw(){

    }

}
