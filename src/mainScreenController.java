//importing the necessary node classes.
//A node is any UI element.
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class mainScreenController implements Initializable {

    /*
    mainScreenController class
    Implements the Initializable Interface.

    Implementing Interface means to have all the classes of that Interface

    By extending class, we can call "super.<methodName>()" to call the actual method of the extending class.
    But in an Interface, there is actually no code inside the declared methods.
    Its just a skeleton.

    You can implement as much Interfaces as you want
    but u can extend only one class.ds 
     */

    //Adding the @FXML annotation for telling the JVM that these nodes
    //actually exist in the original FXML file
    //and that it needs to import them from the FXML file.

    @FXML
    private JFXTextField tmpSearchQueryField;
    @FXML
    private JFXTextField searchQueryField;
    @FXML
    private VBox queryPane;
    @FXML
    private JFXButton searchButton;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private VBox resultsVBox;
    @FXML
    private VBox documentPane;
    @FXML
    private Label headingLabel;
    @FXML
    private Label contentLabel;


    //Create Global variables so that all the methods of the class can access this.
    //However, these variables cannot be accessed from any other classes in the project.
    private String heading;
    private String content;
    private String currentLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.setProperty("jsse.enableSNIExtension", "false");
        queryPane.setVisible(false);
        queryPane.toBack();
        documentPane.setVisible(false);
        documentPane.toBack();
        progressBar.setVisible(false);
    }

    @FXML
    public void tmpSearchQueryFieldClicked()
    {
        searchQueryField.setText(tmpSearchQueryField.getText());
        tmpSearchQueryField.setDisable(true);
        tmpSearchQueryField.setText("");
        queryPane.setVisible(true);
        queryPane.toFront();
    }

    @FXML
    public void searchButtonClicked()
    {
        resultsVBox.getChildren().clear();
        resultsVBox.setDisable(true);
        searchQueryField.setDisable(true);
        searchButton.setDisable(true);
        progressBar.setVisible(true);

        Task<Void> t = new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    Document rawDocument = Jsoup.connect("http://bn.banglapedia.org/index.php?search="+searchQueryField.getText()+"&fulltext=%E0%A6%85%E0%A6%A8%E0%A7%81%E0%A6%B8%E0%A6%A8%E0%A7%8D%E0%A6%A7%E0%A6%BE%E0%A6%A8").get();
                    Elements listOfContent = rawDocument.getElementsByClass("mw-search-results").get(0).getElementsByTag("li");

                    for(Element eachElement : listOfContent)
                    {
                        Element divElement = eachElement.getElementsByClass("mw-search-result-heading").get(0);
                        Element aTag = divElement.getElementsByTag("a").get(0);

                        String heading = aTag.text();
                        String link = aTag.attr("href");


                        Label eachLabel = new Label(heading);
                        eachLabel.setPadding(new Insets(10,10,10,10));
                        eachLabel.setFont(Font.font(17));
                        eachLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                loadDocumentPane(link);
                            }
                        });

                        JFXRippler r = new JFXRippler(eachLabel);
                        r.setPadding(new Insets(10,10,10,10));
                        r.setAlignment(Pos.TOP_LEFT);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                resultsVBox.getChildren().add(r);
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setHeaderText("Failed!");
                            a.setContentText("Please check that you have a valid internet connection\nAlso check whether Bangla Pedia website\nis responding or not ...");
                            a.show();
                        }
                    });
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        resultsVBox.setDisable(false);
                        searchQueryField.setDisable(false);
                        searchButton.setDisable(false);
                        progressBar.setVisible(false);
                    }
                });
                return null;
            }
        };

        new Thread(t).start();


    }

    public void loadDocumentPane(String link)
    {
        Task<Void> loadDocumentPaneTask = new Task<Void>() {
            @Override
            protected Void call(){
                currentLink = link;
                resultsVBox.setDisable(true);
                searchQueryField.setDisable(true);
                searchButton.setDisable(true);
                progressBar.setVisible(true);

                try
                {
                    Document rawDocument = Jsoup.connect(link).get();
                    heading = rawDocument.getElementById("firstHeading").text();
                    Elements pTags = rawDocument.getElementById("mw-content-text").getElementsByTag("p");

                    StringBuilder c = new StringBuilder();
                    for(Element eachPTag : pTags)
                    {
                        c.append(eachPTag.text()+"\n\n");
                    }

                    content = c.toString();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            headingLabel.setText(heading);
                            contentLabel.setText(content);
                            progressBar.setVisible(false);
                            documentPane.setVisible(true);
                            documentPane.toFront();
                            System.gc();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(loadDocumentPaneTask).start();
    }

    @FXML
    public void closeDocumentPane()
    {
        System.gc();
        resultsVBox.setDisable(false);
        searchQueryField.setDisable(false);
        searchButton.setDisable(false);
        documentPane.setVisible(false);
        queryPane.setVisible(true);
        queryPane.toFront();
    }

    @FXML
    public void saveAsButtonClicked()
    {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(Main.ps);

        if (file != null) {
            Task<Void> t= new Task<Void>() {
                @Override
                protected Void call() {
                    try
                    {
                        String toBeWritten;
                        if(System.getProperty("os.name").toLowerCase().contains("win"))
                        {
                            toBeWritten = "Bangla Pedia Extracter\nBy Debayan Sutradhar (github.com/ladiesman6969)\n\nExtracted From : "+currentLink+"\n\n"+heading+"\n\n\n"+content;
                        }
                        else
                        {
                            toBeWritten  = "Bangla Pedia Extracter\r\nBy Debayan Sutradhar (github.com/ladiesman6969)\r\n\r\nExtracted From : "+currentLink+"\r\n\r\n"+heading+"\r\n\r\n\r\n"+content;
                        }
                        Writer out = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(file.toString()), "UTF-8"));
                        try {
                            out.write(toBeWritten);
                        } finally {
                            out.close();
                        }
                        System.out.println("Done!");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert a = new Alert(Alert.AlertType.INFORMATION);
                                a.setHeaderText("Saved!");
                                a.setContentText("Successfully saved to \n"+file.toString());
                                a.show();
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setHeaderText("Failed!");
                                a.setContentText("Could'nt save! Run program from CMD\nand check StackTraceError");
                                a.show();
                                e.printStackTrace();
                            }
                        });
                    }
                    return null;
                }
            };
            new Thread(t).start();
        }
    }
}
