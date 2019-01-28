package test;

import java.util.logging.Level;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainFrame extends Application{
	//显示已消去方块数量，由于其他类访问所以被定义为static
	static TextField textArea= new TextField();
	VBox vbox=new VBox(50);
	HBox hbox1=new HBox(5);
	VBox vbox1=new VBox(5);
	
	LLKCanvas canvas=new LLKCanvas(4,2);
	
	BorderPane pane=new BorderPane();
	
	ImageView image = new ImageView("res/bg.jpg");
	private int level=1;
	@Override
	public void start(Stage stage) throws Exception {
		
		Label label= new Label("已消去方块数量：");
		label.setFont(new Font("Times Roman",18));
		
		Label label1 = new Label("难度(1-8)");
		label1.setFont(new Font("Times Roman",18));
		TextField text = new TextField("1");
		text.setPrefColumnCount(2);
		vbox1.getChildren().addAll(label1,text);
		vbox.getChildren().add(vbox1);
		
		
		Button again=new Button("开始游戏");
		again.setFont(new Font("Times Roman",18));
		again.setPrefWidth(150);
		again.setPrefHeight(50);
		Button exit=new Button("退出");
		exit.setPrefWidth(150);
		exit.setPrefHeight(50);
		exit.setFont(new Font("Times Roman",18));
		
		hbox1.setAlignment(Pos.CENTER);
		hbox1.setPadding(new Insets(30,0,0,0));
		hbox1.getChildren().add(label);
		hbox1.getChildren().add(textArea);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(0,30,0,0));
		vbox.getChildren().add(again);
		vbox.getChildren().add(exit);
		
		textArea.setPrefColumnCount(5);
		textArea.setPrefHeight(30);
		textArea.setFont(new Font("Times Roman",18));
		textArea.setEditable(false);
		textArea.setText(Integer.toString(0));//显示已消去方块数量
		
		
		
		//开始游戏
		again.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				level=Integer.parseInt(text.getText());
				//System.out.println(level);
				int gamesize = 0,num=0;
				if(level==1) {
					gamesize=4;num=2;
				}else if(level==2) {
					gamesize=4;num=4;
				}else if(level==3) {
					gamesize=6;num=4;
				}else if(level==4) {
					gamesize=6;num=6;
				}else if(level==5) {
					gamesize=8;num=4;
				}else if(level==6) {
					gamesize=8;num=8;
				}else if(level==7) {
					gamesize=10;num=5;
				}else if(level==8) {
					gamesize=10;num=10;
				}
				
				textArea.setText(Integer.toString(0));
				canvas=new LLKCanvas(gamesize,num);
				pane.setCenter(canvas);
			}
		});
		//退出
		exit.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				System.exit(0);
				
			}
		});
		
	
		pane.getChildren().add(image);
		pane.setTop(hbox1);
		pane.setRight(vbox);
		pane.setCenter(canvas);
		
		
		Scene scene=new Scene(pane,1000,700);
		stage.setTitle("连连看");
		stage.setScene(scene);
		stage.show();
	}

}

















