/*
 * Author(s): Keyla Christopher
 * 00/00/00
 * Professor Reed
 * Programming I P72
 * Desc:Templates/Licenses/license-default.txt
 */
package chickendash;

import java.io.*;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author keyla
 */
public class ChickenDash extends Application {
    //global variables for easier access
    Media musicFile;
    MediaPlayer musicPlayer;
    Image image;
    Image[] gameImages;
    ImageView chicken;
    ImageView fox1;
    ImageView fox2;
    ImageView fox3;
    ImageView coop;
    Polygon fox1PathShape;
    Polygon fox2PathShape;
    Polygon fox3PathShape;
    double[] polygonPointsFox1;
    double[] polygonPointsFox2;
    double[] polygonPointsFox3;
    PathTransition fox1Path;
    PathTransition fox2Path;
    PathTransition fox3Path;
    Label lblGameOver = new Label("Game Over");
    Label lblYouWin = new Label("You Win");
    Text instruct = new Text();
    boolean inPlay = false;
    boolean gameOver = false;
    int numFoxes = 0;
    String tgResult;
    Button btnPlay = new Button("Play");
    Button btnReset = new Button("Reset");
    RadioButton easy = new RadioButton("Easy");
    RadioButton medium = new RadioButton("Medium");
    RadioButton hard = new RadioButton("Hard");
    Slider speed = new Slider(1,4,1);
    
    
    @Override
    public void start(Stage primaryStage) {
       //creating the images and music that will be used
        gameImages = populateImageArray();
        lblGameOver.setOpacity(0);
        lblGameOver.setLayoutX(150);
        lblGameOver.setLayoutY(150);
        lblGameOver.setFont(Font.font(20));
        lblYouWin.setOpacity(0);
        lblYouWin.setLayoutX(150);
        lblYouWin.setLayoutY(150);
        lblYouWin.setFont(Font.font(20));
        instruct.setText("Get to the coop and Don't get caught by a fox!");
        instruct.setLayoutX(25);
        instruct.setLayoutY(10);
        createMusic();
        setPolygonPoints();
        musicPlayer.setVolume(0.7);
        musicPlayer.play();
        
        //creating the chicken at starting point
        chicken = new ImageView(returnImage(0));
        chicken.setX(20);
        chicken.setY(320);
        
        //creating coop and setting location
        coop = new ImageView(returnImage(9));
        coop.setX(280);
        //creating fox 1, its path, and animation
        fox1 = new ImageView(returnImage(8));
        fox1PathShape = new Polygon(polygonPointsFox1);
        fox1PathShape.setOpacity(0);
        fox1Path = new PathTransition(Duration.seconds(20), fox1PathShape, fox1);
        fox1Path.setCycleCount(PathTransition.INDEFINITE);
        //creating fox 2, its path, and animation
        fox2 = new ImageView(returnImage(8));
        fox2PathShape = new Polygon(polygonPointsFox2);
        fox2PathShape.setOpacity(0);
        fox2Path = new PathTransition(Duration.seconds(20), fox2PathShape, fox2);
        fox2Path.setCycleCount(PathTransition.INDEFINITE);
        //creating fox 3, its path, and animation
        fox3 = new ImageView(returnImage(8));
        fox3PathShape = new Polygon(polygonPointsFox3);
        fox3PathShape.setOpacity(0);
        fox3Path = new PathTransition(Duration.seconds(20), fox3PathShape, fox3);
        fox3Path.setCycleCount(PathTransition.INDEFINITE);
        fox1.setX(500);fox1.setY(500);
        fox2.setX(500);fox2.setY(500);
        fox3.setX(500);fox3.setY(500);
        speed.valueProperty().addListener((obs, oldValue, newValue) -> {
            int speedSelect = newValue.intValue();
            changeSpeed(speedSelect);
        });
        
        Pane gameSpace = new Pane();
        gameSpace.getChildren().add(chicken);
        gameSpace.getChildren().add(coop);
        gameSpace.getChildren().add(fox1);
        gameSpace.getChildren().add(fox1PathShape);
        gameSpace.getChildren().add(fox2);
        gameSpace.getChildren().add(fox2PathShape);
        gameSpace.getChildren().add(fox3);
        gameSpace.getChildren().add(fox3PathShape);
        gameSpace.getChildren().add(lblGameOver);
        gameSpace.getChildren().add(lblYouWin);
        gameSpace.getChildren().add(instruct);
        
        ChangeListener<Bounds> boundsListener = (ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) -> {
            if (chicken.getBoundsInParent().intersects(coop.getBoundsInParent())) {
                winCondition();
            }
            else if(chicken.getBoundsInParent().intersects(fox1.getBoundsInParent())){
                gameOverPoceedure();
            }
            else if(chicken.getBoundsInParent().intersects(fox2.getBoundsInParent())){
                gameOverPoceedure();
            }
            else if(chicken.getBoundsInParent().intersects(fox1.getBoundsInParent())){
                gameOverPoceedure();
            }
        };
        chicken.boundsInParentProperty().addListener(boundsListener);
        coop.boundsInParentProperty().addListener(boundsListener);
        fox1.boundsInParentProperty().addListener(boundsListener);
        fox2.boundsInParentProperty().addListener(boundsListener);
        fox2.boundsInParentProperty().addListener(boundsListener);
       
        ToggleGroup tg = new ToggleGroup();
        easy.setToggleGroup(tg);
        medium.setToggleGroup(tg);
        hard.setToggleGroup(tg);
        VBox radioBtns = new VBox();
        radioBtns.getChildren().add(easy);
        radioBtns.getChildren().add(medium);
        radioBtns.getChildren().add(hard);
        
        VBox controls = new VBox(3);
        controls.getChildren().add(radioBtns);
        controls.getChildren().add(speed);
        controls.getChildren().add(btnPlay);
        controls.getChildren().add(btnReset);
        controls.setAlignment(Pos.CENTER);
        
        btnReset.setOnAction((ActionEvent event) -> {
            setFTTrue();
            resetChicken();
            resetFoxes();
            chicken.setFocusTraversable(true);
        });
         btnPlay.setOnAction((ActionEvent event) -> {
             setFTFalse();
             whichFoxes();
        });
         easy.setOnMouseClicked(e->{
            tgResult  = "Easy";
            numFoxes = getFoxNum(tgResult);
            
            setFoxes();
        });
        medium.setOnMouseClicked(e->{
            tgResult  = "Medium";
            numFoxes = getFoxNum(tgResult);
            
            setFoxes();
        });
        hard.setOnMouseClicked(e->{
            tgResult  = "Hard";
            numFoxes = getFoxNum(tgResult);
            
            setFoxes();
        });
        speed.setMaxWidth(100);
        speed.setMajorTickUnit(3);
        speed.setMinorTickCount(0);
        speed.setSnapToTicks(true);
        speed.getBlockIncrement();
        speed.setMajorTickUnit(1);
        speed.setShowTickLabels(true);
        speed.setShowTickMarks(true);
        speed.valueProperty().addListener((obs, oldValue, newValue) -> {
            int speedSelect = newValue.intValue();
            changeSpeed(speedSelect);
        });
        
        BorderPane root = new BorderPane();
        root.setCenter(gameSpace);
        root.setRight(controls);
        
        
        
        Scene scene = new Scene(root, 400, 350);
        
        //move the chicken
        scene.setOnKeyPressed(event -> {
            double step = 5; 
            if (null != event.getCode()) switch (event.getCode()) {
                case UP:
                    chicken.setY(chicken.getY() - step);
                    chicken.setImage(returnImage(1));
                    break;
                case DOWN:
                    chicken.setY(chicken.getY() + step);
                    chicken.setImage(returnImage(0));
                    break;
                case LEFT:
                    chicken.setX(chicken.getX() - step);
                    chicken.setImage(returnImage(3));
                    break;
                case RIGHT:
                    chicken.setX(chicken.getX() + step);
                    chicken.setImage(returnImage(2));
                    break;
                default:
                    break;
            }
        });
        
        primaryStage.setTitle("Chicken Dash");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //Contain and return all file paths
    private String getFileName(int num){
        String chickWalkUp = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/ChickenWalk.gif";
        String chickWalkDown = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/ChickenWalkBack.gif";
        String chickWalkRight = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/ChickenSideWalkRight.gif";
        String chickWalkLeft = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/ChickenSideWalkLeft.gif";

        String foxWalkUp = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/FoxWalkUp.gif";
        String foxWalkDown = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/FoxWalkDown.gif";
        String foxWalkRight = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/FoxWalkRight.gif";
        String foxWalkLeft = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/FoxWalkLeft.gif";
        String foxStationary = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/StationaryFox.gif";
        
        String henHouse = "C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/Coop.png";
        String music = "file:///C:/Users/keyla/OneDrive/Desktop/NetBeansProjects/ChickenDash/src/buryAFriend.mp3";        

        String[] fileNames = {chickWalkUp, chickWalkDown, chickWalkRight, 
        chickWalkLeft, foxWalkRight, foxWalkLeft, foxWalkUp, foxWalkDown, foxStationary, henHouse, music};

        return fileNames[num];
    }
    //create an image object
    private Image createImage(int num){
        
        try{
        FileInputStream stream = new FileInputStream(getFileName(num));
        image = new Image(stream);
        }
        catch(FileNotFoundException e){
        System.out.println("Error loading file: " + e.getMessage());
        }
        return image;
    }
    //collect all the images to be used
    private Image[] populateImageArray() {
        Image[] imageList = new Image[10];
        for (int i = 0; i < imageList.length; i++) {
            imageList[i] = createImage(i);
        }
        return imageList;
    }
    //retrieving an image from the array
    private Image returnImage(int num){
        return gameImages[num];
    }
    //create the music for the game
    private void createMusic(){
        try{
        musicFile = new Media(getFileName(10));
        musicPlayer = new MediaPlayer(musicFile);
        }
        catch(MediaException e){
        System.out.println("Error loading file: " + e.getMessage());
        }
    }  
     //Turn of arrow key selection
    private void setFTFalse(){
        easy.setFocusTraversable(false);
        medium.setFocusTraversable(false);
        hard.setFocusTraversable(false);
        btnPlay.setFocusTraversable(false);
        btnReset.setFocusTraversable(false);
        speed.setFocusTraversable(false);
    }
    //Turn on arrow key selection
    private void setFTTrue(){
        easy.setFocusTraversable(true);
        medium.setFocusTraversable(true);
        hard.setFocusTraversable(true);
        btnPlay.setFocusTraversable(true);
        btnReset.setFocusTraversable(true);
        speed.setFocusTraversable(true);
     }
    //Figuring out how many foxes 
    private int getFoxNum(String difficulty){
        int foxNum;
        if(difficulty.matches("Easy")){
            foxNum = 1;
        }
        else if (difficulty.matches("Medium")){
            foxNum = 2;
        }
        else{
            foxNum = 3;
        }
        return foxNum;
    }
    //Selecting which foxes appear.
    private void setFoxes(){
       fox1.setX(500);fox1.setY(500);
        fox2.setX(500);fox2.setY(500);
        fox3.setX(500);fox3.setY(500);
        switch(numFoxes){
            case 1:fox1.setX(197.0);fox1.setY(1.0); break;
            case 2:fox1.setX(197.0);fox1.setY(1.0);
                   fox2.setX(370.0);fox2.setY(63.0);break;
            case 3:fox1.setX(197.0);fox1.setY(1.0);
                   fox2.setX(370.0);fox2.setY(63.0);
                   fox3.setX(300);fox3.setY(300);break;
            default: break;
        }
    }
    //Selecting which foxes move
    private void whichFoxes(){
        switch(numFoxes){
            case 1:fox1Path.play(); break;
            case 2:fox1Path.play();
                   fox2Path.play();break;
            case 3:fox1Path.play();
                   fox2Path.play();
                   fox3Path.play();break;
            default: break;
        }
    }
    private void changeSpeed(int newSpeed){
        switch(newSpeed){
            case 1:fox1Path.setDuration(Duration.seconds(20));
                   fox2Path.setDuration(Duration.seconds(20));
                   fox3Path.setDuration(Duration.seconds(20));break;
            case 2:fox1Path.setDuration(Duration.seconds(15));
                   fox2Path.setDuration(Duration.seconds(15));
                   fox3Path.setDuration(Duration.seconds(15));break;
            case 3:fox1Path.setDuration(Duration.seconds(10));
                   fox2Path.setDuration(Duration.seconds(10));
                   fox3Path.setDuration(Duration.seconds(10));break;
            case 4:fox1Path.setDuration(Duration.seconds(5));
                   fox2Path.setDuration(Duration.seconds(5));
                   fox3Path.setDuration(Duration.seconds(5));break;
            default: break;
        }
    }
    private void winCondition(){
        lblYouWin.setTextFill(Color.GREEN);
        lblYouWin.setOpacity(100);
        chicken.setFocusTraversable(false);
        gameOver = true;
        fox1Path.pause();
        fox2Path.pause();
        fox3Path.pause();
    }
    private void gameOverPoceedure(){
        lblGameOver.setTextFill(Color.RED);
        lblGameOver.setOpacity(100);
        chicken.setFocusTraversable(false);
        gameOver = true;
         fox1Path.pause();
        fox2Path.pause();
        fox3Path.pause();
    }
    //Reset Chicken
    private void resetChicken(){
        chicken.setX(20); chicken.setY(320);
        chicken.setImage(returnImage(0));
    }
    //Reset a fox enemy
    private void resetFox(PathTransition fox){
        fox.pause();
        fox.jumpTo(Duration.ZERO);
        
    }
    //reset for all fox enemies
    private void resetFoxes(){
        switch(numFoxes){
            case 1: resetFox(fox1Path); 
            case 2: resetFox(fox1Path); resetFox(fox2Path);
            case 3: resetFox(fox1Path); resetFox(fox2Path); resetFox(fox3Path);
            default: break;
            
        }
    }
    private void getDirection(ImageView fox){
        String direction;
        if(fox.equals(fox1)){
            double x = fox.getX();
            double y = fox.getY();
            if(x == 10.0 && y == 117.0){
                direction = "right";
            }
        }
    }
    private void turnFox(String direction, ImageView fox){
        switch(direction){
            case "left": fox.setImage(returnImage(5));
                         break;
            case "right":fox.setImage(returnImage(4));
                         break;
            case "up":fox.setImage(returnImage(6));
                         break;
            case "down":fox.setImage(returnImage(7));
                         break;
            default: break;
        }
    } 
    
        
    private void setPolygonPoints(){
        polygonPointsFox1 = new double[] {196.0,1.0,
        197.0,1.0,
        10.0,117.0,
        250.0,235.0,
        380.0,333.0};
        polygonPointsFox2 = new double[]{10.0,20.0,
            380.0,135.0,
            10.0,280.0  
        };
        polygonPointsFox3 = new double[]{380.0,300.0,
           10.0,280.0,
           197.0,1.0,
           380.0,235.0,
           197.0,100.0,    
        };
        }
    
    
         
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
