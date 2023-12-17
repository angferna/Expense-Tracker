package application;
	
import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
			Scene scene = new Scene(root,600,400);
			primaryStage.setTitle("Login");
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		String[] files = {"users.txt", "transactions.txt"};

        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println(fileName + " file created successfully.");
            } else {
                System.out.println(fileName + " already exists in the project root directory.");
            }
        }
		launch(args);
	}
}
