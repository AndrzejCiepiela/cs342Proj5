import java.io.File;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Main extends Application {

    BorderPane mainLayout, gameLayout;
    Image lobbyBackground, gameBackground;
    Button connect,exit,update,next;
    Button[] answerButton;
    HBox lobbyButtons, gameButtons;
    Stage stage;
    Scene scene1, scene2;
    Clip lobbyClip, tickClip;
    AudioInputStream audioInput1, audioInput2, audioInput3;
    private ClientNetwork conn = createClient("127.0.0.1", 5555);
    private TextArea messages = new TextArea();
    //timer
    private static final Integer STARTTIME = 10;
    private Timeline timeline;
    private Label timerLabel = new Label();
    private Integer seconds = STARTTIME;
    
    // new to display the scores
    Text scores;
    private boolean updateScores = false;
    
    public Parent setupGUI() {
    	//set up time label
    	timerLabel= new Label();
    	timerLabel.setText("Countdown: 10");
    	timerLabel.setFont(Font.font(30));
    	timerLabel.setTextFill(Color.RED);

        messages.setPrefHeight(300);

        messages.getStylesheets().add("textArea.css");
        messages.setMaxSize(650, 200);
        messages.setOpaqueInsets(new Insets(0));
        messages.setBorder(null);

        //lobby background setup
        lobbyBackground = new Image("triviaLobby.png");
        BackgroundImage backGround1 = new BackgroundImage(lobbyBackground,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        //game screen  background setup
        gameBackground = new Image("triviaGameScreen.png");
        BackgroundImage backGround2 = new BackgroundImage(gameBackground,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        //connect button setup
        connect = new Button("connect");
        connect.setMinSize(135, 60);
        connect.getStylesheets().add("lobbyButtons.css");

        //exit button setup
        exit = new Button("exit");
        exit.setMinSize(135, 60);
        exit.getStylesheets().add("lobbyButtons.css");
        lobbyButtons = new HBox(10);
        lobbyButtons.getChildren().addAll(connect,exit);
        
        //next button setup
        next = new Button("NEXT");
        next.setMinSize(100, 30);
        next.getStylesheets().add("lobbyButtons.css");
        
        //timer setup
        //timerLabel.setText(timeSeconds.toString());
        //timerLabel.setTextFill(Color.RED);
        //timerLabel.setStyle("-fx-font-size: 4em;");


        //answer buttons setup
        answerButton = new Button[4];
        gameButtons = new HBox(60);
        for(int i=0; i<4; i++) {
            answerButton[i] = new Button();
            answerButton[i].setMinSize(100, 30);
            answerButton[i].getStylesheets().add("lobbyButtons.css");
            gameButtons.getChildren().add(answerButton[i]);
            //gameButtons.getChildren().add(next);
        }
        gameButtons.getChildren().add(next);
        answerButton[0].setText("A");
        answerButton[1].setText("B");
        answerButton[2].setText("C");
        answerButton[3].setText("D");
        EventHandler<ActionEvent> choice = event -> {

            // disable buttons when choice is made
            answerButton[0].setDisable(true);
            answerButton[1].setDisable(true);
            answerButton[2].setDisable(true);
            answerButton[3].setDisable(true);

            String client = "Your choice was: ";
            messages.clear();
            String message = "";

            if(event.getSource() == answerButton[0])
                message += "A";
            else if(event.getSource() == answerButton[1])
                message += "B";
            else if(event.getSource() == answerButton[2])
                message += "C";
            else if(event.getSource() == answerButton[3])
                message += "D";

            messages.appendText(client + message + "\n");
            try {
                conn.send(message);
            }
            catch(Exception e) {

            }
        };
        for(int i=0; i< 4; i++) {
            answerButton[i].setDisable(true);
            answerButton[i].setOnAction(choice);
        }
        // adding elements to lobby layout
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(25));
        mainLayout.setBottom(lobbyButtons);
        mainLayout.setBackground(new Background(backGround1));
        
        // new create score display
        scores = new Text("SCORES		Player1: 0 Player2: 0 Player3: 0 Player4: 0");
        scores.setFont(Font.font("Verdana", 20));
        scores.setFill(Color.WHITE);
        HBox scoresBox = new HBox(200, scores);
        
        //
        VBox timerr = new VBox(80);
        timerr.getChildren().add(timerLabel);

        //adding elements to the game layout
        gameLayout = new BorderPane();
        gameLayout.setTop(scoresBox);
        gameLayout.setCenter(messages);
        gameLayout.setBottom(gameButtons);
        gameLayout.setRight(timerr);
        gameLayout.setBackground(new Background(backGround2));
        gameLayout.setPadding(new Insets(25));
        
        //scene2 setup
        scene2 = new Scene(gameLayout, 790, 500);

        // event handlers for lobby buttons
        connect.setOnAction(e -> {
            stage.setScene(scene2);
            stoplobbyMusic();
            playTickTock("ticktock.wav");
            doTime();
        });
        
        exit.setOnAction(e -> {
            System.exit(0);
        });
       
       return mainLayout;
    }
    
    //set up timer
    private void doTime() {
    	 timeline= new Timeline();
    	  
    	  
    	  KeyFrame frame= new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>(){

    	   @Override
    	   public void handle(ActionEvent event) {
    	    
    	    
    		seconds--;
    		timerLabel.setText("Countdown: "+seconds.toString());
    	    if(seconds<=0){
    	     timeline.stop();
    	    }
    	     
    	   }
    	   
    	   
    	  }, null);
    	  
    	  timeline.setCycleCount(Timeline.INDEFINITE);
    	  timeline.getKeyFrames().add(frame);
    	  if(timeline!=null){
    	   timeline.stop();
    	  }
    	  timeline.play();
    	  
    	  
    	 }

	   


    // initiates lobbyClip, and begins playing the music
    public void playLobbyMusic(String fileLocation) {
        try {
            File musicPath = new File(fileLocation);

            if (musicPath.exists()) {
                audioInput1 = AudioSystem.getAudioInputStream(musicPath);
                lobbyClip = AudioSystem.getClip();
                lobbyClip.open(audioInput1);
                lobbyClip.start();
            }
            else {
                System.out.println("could not find the music file");
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //stops playing the lobby game music
    public void stoplobbyMusic() {
        lobbyClip.stop();
    }

    //initiates the tick tock sound and begins playing it
    public void playTickTock(String fileLocation) {
        try {
            File musicPath = new File(fileLocation);

            if (musicPath.exists()) {
                audioInput2 = AudioSystem.getAudioInputStream(musicPath);
                tickClip = AudioSystem.getClip();
                tickClip.open(audioInput2);
                tickClip.start();
                tickClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            else {
                System.out.println("could not find the music file");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //stops the tick tock music
    public void stopTickTock() {
        tickClip.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        update = new Button();
        stage = primaryStage;
        stage.setTitle("Trivia");

        update.setOnAction(e -> {
            messages.clear();
            //might need this to update but its useless code tbh
            for(int i = 0; i < answerButton.length; i++) {
                answerButton[i].setText(answerButton[i].getText());
            }
            //primaryStage.setScene(new Scene(createClientContent()));
        });
        //doTime();
        scene1 = new Scene(setupGUI(), 795, 445);
        stage.setScene(scene1);

        playLobbyMusic("lobbyMusic.wav");
        stage.show();
    }

    @Override
    public void init() throws Exception{
        conn.startConn();
    }

    @Override
    public void stop() throws Exception{
        conn.closeConn();
    }
    public static void main(String[] args) {
        launch(args);
    }
    private Client createClient(String address, int port) {
        return new Client(address, port, data -> {
            Platform.runLater(()->{
                messages.appendText(data.toString() + "\n");

                if(data.toString().intern().contains("A: ")) {
                    System.out.println("yup");
                    answerButton[0].setText(data.toString().intern());
                    update.fire();
                }
                if(data.toString().intern().contains("B: ")) {
                    answerButton[1].setText(data.toString().intern());
                    update.fire();
                }
                if(data.toString().intern().contains("C: ")) {
                    answerButton[2].setText(data.toString().intern());
                    update.fire();
                }
                if(data.toString().intern().contains("D: ")) {
                    answerButton[3].setText(data.toString().intern());
                    update.fire();
                }

                // if enough players joined the game room
                if(data.toString().intern() == "Game has started") {
                	
                	messages.clear();
                	
                    // enable game buttons
                    for(int i = 0; i<answerButton.length;i++) {
                        answerButton[i].setDisable(false);
                    }
                }
                
             // check if you need to update scores
                if(updateScores == true) {
                	
                	// new create score display
                	messages.clear();
                    scores = new Text(data.toString().intern());
                    scores.setFont(Font.font("Verdana", 20));
                    scores.setFill(Color.WHITE);
                    HBox scoresBox = new HBox(200, scores);
                    gameLayout.setTop(scoresBox);
                    
                    for(int i = 0; i<answerButton.length;i++) {
                        answerButton[i].setDisable(false);
                    }
                    
                    updateScores = false;
                }
                
                // scores need to be updated
                if(data.toString().intern() == "Begin next round!")
                	updateScores = true;
                
                
                // parses the server message into an array of words
                String serverMessage[] =  data.toString().split(" ");
                ArrayList<String> updatedConnections = new ArrayList<String>();
                for(String word : data.toString().split(" ")) {
                    updatedConnections.add(word);
                }
                
                
                if(data.toString().intern() == "YOU WON" || data.toString().intern() == "YOU LOST") {
                	
                	// disable game buttons
                    for(int i = 0; i<answerButton.length;i++) {
                        answerButton[i].setDisable(true);
                    }
                }
                
                if(data.toString().intern() == "disconnected") {
                	
                	messages.clear();
                	
                	// disable game buttons
                    for(int i = 0; i<answerButton.length;i++) {
                        answerButton[i].setDisable(true);
                    }
                    
                    // tell players one player or more have disconneted
                    messages.appendText("Error: One or more players have disconnected\nNeed at least 4 players to continue playing");
                }
                
				/*
				// if first word of messaage is ID, set this.ID = to the corresponding ID (serverMessage[1])
				if(serverMessage[0].intern() == "ID") {
					this.ID = serverMessage[1];
					idLabel.setText("ID: " + serverMessage[1]);
				}*/


            });
        });
    }
}
