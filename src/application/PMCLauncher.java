package application;
	
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class PMCLauncher extends Application {
	@Override
	public void start(Stage stage) throws IOException {
			//Parent root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
			Parent root = FXMLLoader.load(getClass().getResource("views/steps/ImportDataStep.fxml"));
			
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
			stage.setTitle("(PMC) Process Mining Cube");
			stage.setScene(new Scene(root));
			stage.show();			
	}
	
	public static void main(String[] args) {
		Application.launch(PMCLauncher.class,args);
	}
}
