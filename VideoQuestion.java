//import java.awt.BasicStroke;
import java.io.File;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.shape.Circle;

import javafx.scene.shape.Rectangle;

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Label;

import javafx.scene.layout.StackPane;
import javafx.scene.media.*;
import javafx.geometry.Pos;
//import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.event.*;
import javafx.util.Duration;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import javafx.scene.text.FontWeight;
import java.util.Random;
import java.util.ArrayList;

public class VideoQuestion extends Application {

        MediaPlayer player;
        MediaView viewer;

        Label questionText;
        private String pm4filename = "";
        private final String txtfile = "question.txt";
        private final String textstyle ="-fx-stroke: white;-fx-stroke-width: 1;-fx-background-color:rgba(0, 0, 0, 0.5);-fx-background-radius: 10;";
        Label [] answerText = new Label[4];
        private int correctIndex = -1;
        private Button seeAnswerButton = new Button("参考答案");
        private Button returnButton = new Button("返回");


        private double [] time0 = new double[10];//{0.0,8.5,29.0,39.5,50.0,70.0,76.5,87.0,95.0,102.0};

        private double [] dt    = new double[10];//{4.0,4.0,3.0 ,3.0 ,4.0 ,6.0 ,3.0 ,4.0 ,4.0 ,4.0  };

        private String [] questions = new String[10];/*{

                "1. 请问这种照明器是模仿昆虫的哪种结构制成的？",

                "2. 画面上这个蓝色的方块的作用是什么？",

                "3. Ti2上的成像镜变粗了，这样做有什么好处呢？",

                "4. Ti2的视野变大了一倍，这是如何做到的呢？",

                "5. 请问图中展示的操作叫什么名称？",

                "6. 这其实是一束看不见的红外光，有什么用呢？",

                "7. 这个马蹄形的部件是用来作什么的呢？",

                "8. 这是perfect focus systm的缩写，中文叫啥名字？",

                "9. Ti2机身上有一个辅助相机，用它看激光光斑有什么好处？",

                "10. 此处的光环是真的么？有什么用呢?"

        };*/
        private String [] answers = new String[10];

        private String Dir = System.getProperty("user.dir");
        public static void main(String[] args) throws Exception {
            launch(args);
        }
        private void readtxtfile() {
            String line = null;
            try (BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(txtfile)))) {
                pm4filename = input.readLine();//first line is mp4 filename
                String timeStr = input.readLine();//2nd line is time points
                String []sarray = timeStr.split(",");//parameters are seperated by ","
                for (int i=0;i<sarray.length;i++) {
                    time0[i]=Double.parseDouble(sarray[i]);
                    //System.out.println(""+time0[i]);
                }
                String dtStr = input.readLine();//3rd line is duration array
                sarray = dtStr.split(",");
                for (int i=0;i<sarray.length;i++) {
                    dt[i]=Double.parseDouble(sarray[i]);
                    //System.out.println(""+dt[i]);
                }
                for (int i=0;i<sarray.length;i++) {//next 10 lines are questions
                    questions[i]=input.readLine();
                }
                for (int i=0;i<sarray.length;i++) {//next 10 lines are answers
                    answers[i]=input.readLine();
                }

            } catch (Exception ex) {
                System.err.format("IOException: %s%n", ex);
            }
        }

        private class MyEventHandler implements EventHandler<ActionEvent> {
                @Override
                public void handle(ActionEvent evt) {
                    //System.out.println(((Button)evt.getSource()).getId());

                    ((Button)evt.getSource()).setDisable(true);//setVisible(false);

                    int id = Integer.parseInt(((Button)evt.getSource()).getId());

                    questionText.setText(questions[id-1]);
                    String []sarray = answers[id-1].split(",");
                    ArrayList<String> list = new ArrayList<String>();
                    for (String s : sarray)list.add(s);

                    Random rand = new Random();
                    for (int i=0;i<answerText.length;i++) {
                        int idx = rand.nextInt(list.size());
                        answerText[i].setText((char)('A'+i)+") "+list.get(idx));
                        answerText[i].setTextFill(Color.YELLOW);
                        if (list.get(idx).equals(sarray[0]))correctIndex=i;//mark the correct answer
                        list.remove(idx);
                    }

                    player.stop();

                    seeAnswerButton.setVisible(true);
                    try {

                        File f = new File(Dir, pm4filename);

                        //Converts media to string URL

                        player = new MediaPlayer(new Media(f.toURI().toURL().toString()));

                        viewer.setMediaPlayer(player);

                        player.setStartTime(new Duration(1000*time0[id-1]));

                        player.setStopTime(new Duration(1000*(time0[id-1]+dt[id-1])));

                        //player.seek(new Duration(1000*10*id));

                        player.setCycleCount( MediaPlayer.INDEFINITE );

                        player.play();

                    } catch (Exception e) {



                    }
                }
        }

        private class NaviButtonHandler implements EventHandler<ActionEvent> {
                @Override
                public void handle(ActionEvent evt) {

                    if (((Button)evt.getSource()).getId().equals("see answer")) {
                        answerText[correctIndex].setTextFill(Color.RED);
                        returnButton.setVisible(true);
                    }
                    if (((Button)evt.getSource()).getId().equals("return")) {
                        answerText[correctIndex].setTextFill(Color.YELLOW);

                        for (int i=0;i<answerText.length;i++)answerText[i].setText("");
                        questionText.setText("");
                        returnButton.setVisible(false);
                        seeAnswerButton.setVisible(false);
                        player.stop();

                        try {

                            File f = new File(Dir, pm4filename);

                            //Converts media to string URL

                            player = new MediaPlayer(new Media(f.toURI().toURL().toString()));

                            viewer.setMediaPlayer(player);

                            player.setCycleCount( MediaPlayer.INDEFINITE );

                            player.play();

                        } catch (Exception e) {



                        }
                    }
                }
        }

        @Override
        public void start(Stage stage) throws Exception {

            readtxtfile();
            //goes to user Directory
            File f = new File(Dir, pm4filename);


            //Converts media to string URL
            player = new MediaPlayer(new Media(f.toURI().toURL().toString()));
            viewer = new MediaView(player);

            //change width and height to fit video
            DoubleProperty width = viewer.fitWidthProperty();
            DoubleProperty height = viewer.fitHeightProperty();
            width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(viewer.sceneProperty(), "height"));
            viewer.setPreserveRatio(true);



            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: #FFFFFF;");
            root.setAlignment(Pos.CENTER_LEFT);
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                        if (e.getClickCount() == 1) {
                                //questionText.setOpacity(0.5);
                        } else if (e.getClickCount() > 1) {
                                stage.setFullScreen(true);
                            }
                        }
                    }
                });

            root.getChildren().add(viewer);
            //root.getChildren().add(circ);
            Group g = new Group();



            questionText = new Label("");

            questionText.setTextFill(Color.YELLOW);
            questionText.setFont(new Font(40));

            questionText.setStyle(textstyle);

            seeAnswerButton.setOnAction(new NaviButtonHandler());
            seeAnswerButton.setId("see answer");
            returnButton.setOnAction(new NaviButtonHandler());
            returnButton.setId("return");
            g.getChildren().add(seeAnswerButton);
            g.getChildren().add(returnButton);

            Button []buttons = new Button[10];
            for (int i=0;i<10;i++) {
                buttons[i]=new Button(""+(i+1));

                buttons[i].setId(""+(i+1));
                buttons[i].setOnAction(new MyEventHandler());
                g.getChildren().add(buttons[i]);
                buttons[i].setTranslateX(i*50.0);
            }
            seeAnswerButton.setTranslateX(11*50.0);
            returnButton.setTranslateX(15*50.0);
            returnButton.setVisible(false);
            seeAnswerButton.setVisible(false);

            //set the Scene
            root.getChildren().add(g);

            root.getChildren().add(questionText);
            for (int i=0;i<answerText.length;i++) {
                answerText[i] = new Label("");
                answerText[i].setTextFill(Color.YELLOW);
                answerText[i].setStyle(textstyle);
                answerText[i].setFont(Font.font("STHeiti", FontWeight.BOLD, 40));
                answerText[i].setTextAlignment(TextAlignment.LEFT);
                //answerText[i].setOpacity(0.7);
                root.getChildren().add(answerText[i]);
            }

            Scene scenes = new Scene(root, 500, 500, Color.BEIGE);
            stage.setScene(scenes);
            stage.setTitle("Riddle Game");
            stage.setFullScreen(true);


            stage.show();
            g.setTranslateY(viewer.getFitHeight()/2-20);

            questionText.setTranslateY(-viewer.getFitHeight()/2+viewer.getFitHeight()/8);
            questionText.setTranslateX(10);
            for (int i=0;i<answerText.length;i++) {
                int y = i/2;
                int x = i%2;
                //answerText[i].setTranslateX(20 + viewer.getFitWidth()/2*x);
                answerText[i].setTranslateX(20);
                answerText[i].setTranslateY(-viewer.getFitHeight()/4+viewer.getFitHeight()/8*i);
                //answerText[i].setTranslateY( 10+viewer.getFitHeight()/10*y);

                }

            player.setCycleCount( MediaPlayer.INDEFINITE );


            player.play();
        }

}