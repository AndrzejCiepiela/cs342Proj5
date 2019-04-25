package sample;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    BorderPane mainLayout, gameLayout;
    Image lobbyBackground, gameBackground;
    Button connect, exit,update;
    Button[] answerButton;
    HBox lobbyButtons, gameButtons;
    Stage stage;
    Scene scene1, scene2;
    private ClientNetwork conn = createClient("127.0.0.1", 5555);
    private TextArea messages = new TextArea();
    public Parent setupGUI() {

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


        //answer buttons setup
        answerButton = new Button[4];
        gameButtons = new HBox(80);
        for(int i=0; i<4; i++) {
            answerButton[i] = new Button();
            answerButton[i].setMinSize(125, 45);
            answerButton[i].getStylesheets().add("lobbyButtons.css");
            gameButtons.getChildren().add(answerButton[i]);
        }
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


        //adding elements to the game layout
        gameLayout = new BorderPane();
        gameLayout.setPadding(new Insets(25));
        gameLayout.setCenter(messages);
        gameLayout.setBottom(gameButtons);
        gameLayout.setBackground(new Background(backGround2));

        //scene2 setup
        scene2 = new Scene(gameLayout, 790, 440);

        // event handlers for lobby buttons
        connect.setOnAction(e -> {
            stage.setScene(scene2);
        });
        return mainLayout;
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

        scene1 = new Scene(setupGUI(), 795, 445);
        stage.setScene(scene1);



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

                // if opponent joined the game room
                if(data.toString().intern() == "Game has started") {
                    // enable game buttons
                    for(int i = 0; i<answerButton.length;i++) {
                        answerButton[i].setDisable(false);
                    }


                }


                // parses the server message into an array of words
                String serverMessage[] =  data.toString().split(" ");
                ArrayList<String> updatedConnections = new ArrayList<String>();
                for(String word : data.toString().split(" ")) {
                    updatedConnections.add(word);
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
