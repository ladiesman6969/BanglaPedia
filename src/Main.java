//Importing the necessary libraries for initializing JavaFX windows, etc.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /*
    The Main Class of the Project
    The JVM always searches for the Main Class, and runs the "main" function/method.
    Here, the Main class extends to the Application Class, which has some
    essential functions like the "start" method and the "launch" method
    which are required for the JavaFX platform to be configured correctly.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        //called after launch(args) method has been called.

        //root is an instance of the Parent Class, the base class of JavaFX,
        //where the FXML (UI) file is expanded.
        Parent root = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));

        //Stage is the base of every JavaFX scene,
        //it consists of all the nodes and UI children.

        //Setting the title of the window
        primaryStage.setTitle("BanglaPedia");

        //Setting the width and height of the scene, which is then passed
        //to the setScene method of the Stage Instance.
        primaryStage.setScene(new Scene(root, 798.0,527.0));
        //The user wont be able to resize the window
        //meaning that the Maximize/Minimize button wont appear.
        primaryStage.setResizable(false);
        //Finally show the application window to the user
        primaryStage.show();

        //We might later change some properties of the window later in the project
        //So we are saving the primaryStage instance to another Public Stage Object,
        //named as "ps"
        ps = primaryStage;
    }

    //Static as we cant create another instance of this exact Stage class,
    //We cannot import non static variables of a class to another class.
    static Stage ps;

    public static void main(String[] args) {
        //called when the program is started...
        launch(args);
    }
}
