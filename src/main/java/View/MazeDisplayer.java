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

public class MazeDisplayer extends Canvas {

    // members for FXML
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNameCharLeft = new SimpleStringProperty();
    StringProperty imageFileNameCharRight = new SimpleStringProperty();
    StringProperty imageFileNameCharFront = new SimpleStringProperty();
    StringProperty imageFileNameCharBack = new SimpleStringProperty();
    StringProperty imageFileNameFinish = new SimpleStringProperty();
    StringProperty imageFileNameCharPath = new SimpleStringProperty();
    StringProperty imageFileNameSolution = new SimpleStringProperty();
    StringProperty imageFileNameWin = new SimpleStringProperty();

    // members for Maze
    private int playerRow;
    private int playerCol;
    private Maze maze = null;
    private Solution solution;
    boolean solved = false;
    String sidePlayer = "front";


    public MazeDisplayer() {
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }


    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }


    public String getImageFileNameWin() {
        return imageFileNameWin.get();
    }


    public void setImageFileNameWin(String imageFileNameWin) {
        this.imageFileNameWin.set(imageFileNameWin);
    }


    public int getPlayerRow() {
        return playerRow;
    }
    
    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row,int col) {
        String side = "";
        if(row<playerRow){sidePlayer = "back";}
        if(row>playerRow){sidePlayer = "front";}
        if(col>playerCol){sidePlayer = "right";}
        if(col<playerCol){sidePlayer = "left";}
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public String getImageFileNameSolution() {
        return imageFileNameSolution.get();
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.imageFileNameSolution.set(imageFileNameSolution);
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


    public void draw() {
        if (maze != null) {
            double canvasHeight = getHeight(); // canvasHeight
            double canvasWidth = getWidth();// canvasWidth

            int rows = maze.getRows();
            int cols = maze.getCols();

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            // clear the canvas
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawFloorAndWalls(graphicsContext, rows, cols, cellWidth, cellHeight); // draw maze
            if (solved) {
                drawSolution(solution);
            }
            drawPlayer(graphicsContext, cellWidth, cellHeight, sidePlayer);
            drawFinish(graphicsContext, cellWidth,cellHeight);  // draw finish line
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellWidth, double cellHeight, String side) {

        String charSide = null;
        Image playerImage = null;
        switch (side) {

            case "front" -> charSide = getClass().getResource(getImageFileNameCharFront()).toExternalForm();
            case "back" -> charSide = getClass().getResource(getImageFileNameCharBack()).toExternalForm();
            case "left" -> charSide = getClass().getResource(getImageFileNameCharLeft()).toExternalForm();
            case "right" -> charSide = getClass().getResource(getImageFileNameCharRight()).toExternalForm();
        }
        if (charSide != null) {
            playerImage = new Image(charSide);
        }

        double x = getPlayerCol()*cellWidth;
        double y = getPlayerRow()*cellHeight;

        if(playerImage == null){
            graphicsContext.setFill(Color.GREEN);
            graphicsContext.fillRect(x,y,cellWidth,cellHeight);
        }
        else{
            graphicsContext.drawImage(playerImage,x,y,cellWidth,cellHeight);
        }
    }

    private void drawFinish(GraphicsContext graphicsContext, double cellWidth, double cellHeight) {
        Image finishImage = null;

        finishImage = new Image(getClass().getResource(getImageFileNameFinish()).toExternalForm());

        if(finishImage != null)
        {
            double x = maze.getGoalPosition().getColumnIndex()*cellWidth;
            double y = maze.getGoalPosition().getRowIndex()*cellHeight;
            graphicsContext.drawImage(finishImage,x,y,cellWidth,cellHeight);
        }
    }

    private void drawFloorAndWalls(GraphicsContext graphicsContext, int rows, int cols, double cellWidth, double cellHeight) {

        Image treeImage = null;

        treeImage = new Image(getClass().getResource(getImageFileNameWall()).toExternalForm());

        Image pathImage = null;

        pathImage = new Image(getClass().getResource(getImageFileNameCharPath()).toExternalForm());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j*cellWidth;
                double y = i*cellHeight;
                if(maze.getMaze()[i][j] == 1){
                    // if it is wall:
                    if(treeImage == null){
                        graphicsContext.setFill(Color.BLACK);
                        graphicsContext.fillRect(x,y,cellWidth,cellHeight);
                    }
                    else {
                        graphicsContext.drawImage(treeImage,x,y,cellWidth,cellHeight);
                    }
                }
                else{
                    graphicsContext.drawImage(pathImage,x,y,cellWidth,cellHeight);

                }
            }
        }
    }



    public void drawSolution(Solution sol) {
        this.solution = sol;

        if(maze != null){

            Image solutionImage = null;

            solutionImage = new Image(getClass().getResource(getImageFileNameSolution()).toExternalForm());

            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getRows();
            int cols = maze.getCols();

            double cellHeight = canvasHeight/rows;
            double cellWidth = canvasWidth/cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            // clear the canvas

            if(solutionImage == null){graphicsContext.setFill(Color.GREEN);}

            for (AState state: solution.getSolutionPath()
                 ) {
                double x = ((MazeState)state).getPosition().getColumnIndex()*cellWidth;
                double y = ((MazeState)state).getPosition().getRowIndex()*cellHeight;

                if(solutionImage == null){
                    graphicsContext.fillRect(x,y,cellWidth,cellHeight);
                }
                else {
                    graphicsContext.drawImage(solutionImage,x,y,cellWidth,cellHeight);
                }
            }
        }
    }

}
