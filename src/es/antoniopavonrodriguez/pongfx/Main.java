package es.antoniopavonrodriguez.pongfx;



import java.util.Random;
import java.util.prefs.Preferences;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
    
    private Preferences prefs;
    
    int ballCenterX = 10;
    int ballCurrentSpeedX = 6;
    int ballCenterY = 30;
    int ballCurrentSpeedY = 6; 
    final int SCENE_TAM_X =600;
    final int SCENE_TAM_Y =400;
    final int STICK_WIDTH = 7;
    final int STICK_HEIGHT = 50;
    int stickPosY =(SCENE_TAM_Y - STICK_HEIGHT) / 2;
    int stickCurrentSpeed = 0;
    final int TEXT_SIZE = 24;
    int score;
    int highScore;
    Text textScore;
    Pane root;
    Shape shapeColision;

    @Override
    public void start(Stage primaryStage) {
        prefs = Preferences.userRoot().node(this.getClass().getName()); 
        highScore = prefs.getInt("Puntuacion_maxima", score); 
        System.out.println(highScore);
        
        root = new Pane();
        Scene scene = new Scene(root, SCENE_TAM_X, SCENE_TAM_Y);
        scene.setFill(Color.BLACK);
        primaryStage.setTitle("PONG PAVITA");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Circle circleBall = new Circle(ballCenterX, ballCenterY, 7, Color.WHITE);
        circleBall.setCenterX(10);
        circleBall.setCenterY(30);
        circleBall.setRadius(7);
        root.getChildren().add(circleBall);
        circleBall.setFill(Color.WHITE);
        
        Rectangle rectStick = new Rectangle(SCENE_TAM_X*0.9, stickPosY, STICK_WIDTH, STICK_HEIGHT);
        rectStick.setFill(Color.WHITE);
        root.getChildren().add(rectStick);
        //creacion de la red
        drawNet(10,4,30);
        //Layout Principal
        HBox paneScores = new HBox();
        paneScores.setTranslateY(20);
        paneScores.setMinWidth(SCENE_TAM_X);
        paneScores.setAlignment(Pos.CENTER);
        paneScores.setSpacing(100);
        root.getChildren().add(paneScores);  
        //Puntuacion Actual
        HBox paneCurrentScore = new HBox();
        paneCurrentScore.setSpacing(10);
        paneScores.getChildren().add(paneCurrentScore); 
        //Puntuacion Maxima
        HBox paneHighScore =new HBox();
        paneHighScore.setSpacing(10);
        paneScores.getChildren().add(paneHighScore);
        //Texto para la puntuacion
        Text textTitleScore = new Text ("Score:");
        textTitleScore.setFont(Font.font(TEXT_SIZE));
        textTitleScore.setFill(Color.WHITE);
        //Texto Puntuacion
        textScore = new Text("0");
        textScore.setFont(Font.font(TEXT_SIZE));
        textScore.setFill(Color.WHITE);
        //Texto Para puntuacion maxima
        Text textTitleHighScore = new Text ("Max.Score:");
        textTitleHighScore.setFont(Font.font(TEXT_SIZE));
        textTitleHighScore.setFill (Color.WHITE);
        //Text para la puntuacion max
        Text textHighScore= new Text (String.valueOf(highScore));
        textHighScore.setFont(Font.font(TEXT_SIZE));
        textHighScore.setFill(Color.WHITE);
        //Añadir los textos a los layoouts 
        paneCurrentScore.getChildren().add(textTitleScore);
        paneCurrentScore.getChildren().add(textScore);
        paneHighScore.getChildren().add(textTitleHighScore);
        paneHighScore.getChildren().add(textHighScore);
        
        resetGame();
        
        AnimationTimer animationBall;
        animationBall = new AnimationTimer(){
            @Override
            public void handle(long now){
                circleBall.setCenterX(ballCenterX);
                ballCenterX+= ballCurrentSpeedX;
                if(ballCenterX >=SCENE_TAM_X){
                    if (score>highScore){
                        highScore = score;
                        textHighScore.setText(String.valueOf(highScore));
                        prefs.putInt("Puntuacion_maxima", highScore);
                    }
                  //Reiniciar Partida
                  score = 0;
                  textScore.setText(String.valueOf(score));
                  ballCenterX = 10;
                  ballCurrentSpeedY = 6;
                  resetGame();
                }
                if(ballCenterX <=0){
                    ballCurrentSpeedX = 6;
                }  
                circleBall.setCenterY(ballCenterY);
                ballCenterY+= ballCurrentSpeedY;
                if(ballCenterY >=SCENE_TAM_Y){
                    ballCurrentSpeedY = -6;
                }
                if(ballCenterY <= 0){
                    ballCurrentSpeedY = 6;
                }
                
                stickPosY += stickCurrentSpeed;
                if (stickPosY < 0){
                    //No sobrepasar el borde superior
                    stickPosY = 0;
                } else{
                    //No sobrepasar inferior
                    if(stickPosY > SCENE_TAM_Y - STICK_HEIGHT) {
                        stickPosY = SCENE_TAM_Y - STICK_HEIGHT;
                    }
                }
                rectStick.setY(stickPosY);

               shapeColision = Shape.intersect(circleBall, rectStick);
               boolean colisionVacia = shapeColision.getBoundsInLocal().isEmpty();
              
               if (colisionVacia == false && ballCurrentSpeedX >0){
                   //Colision detectada
                   ballCurrentSpeedX = -6;
                   //Incremento de Puntuacion
                   score++;
                   textScore.setText(String.valueOf(score));
               }  
            };
            int collisionZone = getStickCollisionZone(circleBall, rectStick);     
        };
        animationBall.start();
        
        scene.setOnKeyPressed ((KeyEvent event) -> {
            switch(event.getCode()){
                case W:
                    //Direccion Arriba
                    stickCurrentSpeed = -6;
                    break;
                case S:
                    //Direccion Abajo
                    stickCurrentSpeed = 6;
                    break;}
        });  
        scene.setOnKeyReleased((KeyEvent event) -> {
            switch(event.getCode()){
                case W:
                    //Detener la bola arriba
                    stickCurrentSpeed = 0;
                    break;
                case S:
                    //Detener la bola Abajo
                    stickCurrentSpeed = 0;
                    break;}
        });
    }
    private void resetGame(){
        score = 0;
        textScore.setText(String.valueOf(score));
        ballCenterX= 10;
        ballCurrentSpeedY = 6;
        //Posicion inicial aleatoria
        Random random = new Random();
        ballCenterY = random.nextInt(SCENE_TAM_Y);
    }
    private void drawNet(int portionHeight, int portionWidth, int portionSpacing){
        for(int i=0; i <SCENE_TAM_Y; i+=portionSpacing){
        Line line = new Line (SCENE_TAM_X/2, i, SCENE_TAM_X/2, i+portionHeight);
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(portionWidth);
        root.getChildren().add(line);
        }
    }
    private int getStickCollisionZone (Circle ball, Rectangle stick){
        if (Shape.intersect(ball,stick).getBoundsInLocal().isEmpty()){
            return 0;
        } else {
            double offsetBallStick = ball.getCenterY()-stick.getY();
            if(offsetBallStick < stick.getHeight()*0.1){
                return 1;
            }else if (offsetBallStick < stick.getHeight()/2){
                return 2;
            }else if (offsetBallStick >= stick.getHeight()/2 &&
                    offsetBallStick < stick.getHeight()* 0.9){
                return 3;
            }else {
                return 4;
            }
        }
    }
    private void calculateBallSpeed(int collisionZone){
        switch (collisionZone){
            case 0:
                //sin colision
                break;
            case 1:
                //esquina superior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = -6;
                break;
            case 2:
                //lado superior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = -3;
                break;
            case 3:
                //lado inferior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = 3;
                break;
            case 4:
                //esquina inferior
                ballCurrentSpeedX = -3;
                ballCurrentSpeedY = 6;
                break;
        }
    }
}
