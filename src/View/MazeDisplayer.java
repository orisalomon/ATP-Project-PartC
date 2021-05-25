package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {

    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameCharLeft = new SimpleStringProperty();
    StringProperty imageFileNameCharRight = new SimpleStringProperty();
    StringProperty imageFileNameCharFront = new SimpleStringProperty();
    StringProperty imageFileNameCharBack = new SimpleStringProperty();
    StringProperty imageFileNameFinish = new SimpleStringProperty();
    StringProperty imageFileNameCharPath = new SimpleStringProperty();

    private int playerRow;
    private int playerCol;

    public int getPlayerRow() {
        return playerRow;
    }
    
    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row,int col) {
        String side = "";
        if(row<playerRow){side = "back";}
        if(row>playerRow){side = "front";}
        if(col>playerCol){side = "right";}
        if(col<playerCol){side = "left";}
        this.playerRow = row;
        this.playerCol = col;
        draw(side);
    }

    private void draw(String side) { // TODO: change (copy of code!)
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        int rows = maze.getRows();
        int cols = maze.getCols();

        double cellHeight = canvasHeight/rows;
        double cellWidth = canvasWidth/cols;

        GraphicsContext graphicsContext = getGraphicsContext2D();
        // clear the canvas
        graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);

        drawFloorAndWalls(graphicsContext,rows,cols,cellHeight,cellWidth); // draw maze
        drawFinish(graphicsContext,rows,cols,cellHeight,cellWidth);  // draw finish line
        drawPlayer(graphicsContext,cellHeight,cellWidth,side);
    }


    public String getImageFileNameCharPath() {
        return imageFileNameCharPath.get();
    }


    public void setImageFileNameCharPath(String imageFileNameCharPath) {
        this.imageFileNameCharPath.set(imageFileNameCharPath);
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameCharLeft(String imageFileNameCharLeft) {
        this.imageFileNameCharLeft.set(imageFileNameCharLeft);
    }

    public void setImageFileNameCharRight(String imageFileNameCharRight) {
        this.imageFileNameCharRight.set(imageFileNameCharRight);
    }

    public void setImageFileNameCharFront(String imageFileNameCharFront) {
        this.imageFileNameCharFront.set(imageFileNameCharFront);
    }

    public void setImageFileNameCharBack(String imageFileNameCharBack) {
        this.imageFileNameCharBack.set(imageFileNameCharBack);
    }

    public void setImageFileNameFinish(String imageFileNameFinish) {
        this.imageFileNameFinish.set(imageFileNameFinish);
    }

    public String getImageFileNameCharLeft() {
        return imageFileNameCharLeft.get();
    }

    public String getImageFileNameCharRight() {
        return imageFileNameCharRight.get();
    }

    public String getImageFileNameCharFront() {
        return imageFileNameCharFront.get();
    }

    public String getImageFileNameCharBack() {
        return imageFileNameCharBack.get();
    }

    public String getImageFileNameFinish() {
        return imageFileNameFinish.get();
    }


    private Maze maze;
    private Solution solution;

    public void drawMaze(Maze maze) {
        this.maze = maze;

        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRows();
            int cols = maze.getCols();

            setPlayerPosition(maze.getStartPosition().getRowIndex(),maze.getStartPosition().getColumnIndex());

            double cellHeight = canvasHeight/rows;
            double cellWidth = canvasWidth/cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            // clear the canvas
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);

            drawFloorAndWalls(graphicsContext,rows,cols,cellHeight,cellWidth); // draw maze
            drawFinish(graphicsContext,rows,cols,cellHeight,cellWidth);  // draw finish line
            drawPlayer(graphicsContext,cellHeight,cellWidth,"front");

        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth, String side) {

        FileInputStream charSide = null;
        Image playerImage = null;
        try {
            switch (side) {
                case "front" -> charSide = new FileInputStream(getImageFileNameCharFront());
                case "back" -> charSide = new FileInputStream(getImageFileNameCharBack());
                case "left" -> charSide = new FileInputStream(getImageFileNameCharLeft());
                case "right" -> charSide = new FileInputStream(getImageFileNameCharRight());
            }
            if (charSide != null) {
                playerImage = new Image(charSide);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No player image found!");
        }

        double x = getPlayerCol()*cellWidth;
        double y = getPlayerRow()*cellHeight;

        if(playerImage == null){
            graphicsContext.setFill(Color.GREEN);
            graphicsContext.fillRect(x,y,cellHeight,cellWidth);
        }
        else{
            graphicsContext.drawImage(playerImage,x,y,cellHeight,cellWidth);
        }


    }

    private void drawFinish(GraphicsContext graphicsContext, int rows, int cols, double cellHeight, double cellWidth) {
        Image finishImage = null;
        try {
            finishImage = new Image(new FileInputStream(getImageFileNameFinish()));
        } catch (FileNotFoundException e) {
            System.out.println("No finish image found!");
        }

        if(finishImage != null)
        {
            double x = maze.getGoalPosition().getColumnIndex()*cellHeight;
            double y = maze.getGoalPosition().getRowIndex()*cellWidth;
            graphicsContext.drawImage(finishImage,x,y,cellHeight,cellWidth);
        }
    }

    private void drawFloorAndWalls(GraphicsContext graphicsContext, int rows, int cols, double cellHeight, double cellWidth) {

        Image treeImage = null;
        try {
            treeImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("No wall image found!");
        }

        Image pathImage = null;
        try {
            pathImage = new Image(new FileInputStream(getImageFileNameCharPath()));
        } catch (FileNotFoundException e) {
            System.out.println("No path image found!");
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j*cellHeight;
                double y = i*cellWidth;
                if(maze.getMaze()[i][j] == 1){
                    // if it is wall:
                    if(treeImage == null){
                        graphicsContext.setFill(Color.BLACK);
                        graphicsContext.fillRect(x,y,cellHeight,cellWidth);
                    }
                    else {
                        graphicsContext.drawImage(treeImage,x,y,cellHeight,cellWidth);
                    }
                }
                else{
                    graphicsContext.drawImage(pathImage,x,y,cellHeight,cellWidth);

                }
            }
        }
    }



    public void drawSolution(Solution sol) {
        this.solution = sol;

        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRows();
            int cols = maze.getCols();

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


}
