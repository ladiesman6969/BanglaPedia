/*
BanglaPedia
By Debayan Sutradhar.
Version 1.2

Open Source is Love.
 */

//importing the necessary node classes.
//A node is any UI element.
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.ArrayList;
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
    but u can extend only one class...
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
    private VBox contentVBox;


    //Create Global variables so that all the methods of the class can access this.
    //However, these variables cannot be accessed from any other classes in the project.
    private String heading;
    private String content;
    private String currentLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //This method is called as soon as this class is loaded.

        //Disables TLS to Bangla Pedia Website because the web site itself is not HTTPS secured.
        //If TLS is enabled from client end, it wont be able to connect to Bangla Pedia
        //because the website will be deemed "unsafe" as its not HTTPS.
        System.setProperty("jsse.enableSNIExtension", "false");


        //UI enhancements...

        //Dont show the pane where the results are shown
        queryPane.setVisible(false);
        //Send it back so that it doesnt obstruct the textview.
        queryPane.toBack();
        //Dont show the pane where the crawled HTML will be shown.
        documentPane.setVisible(false);
        //Send it back so that it doesnt obsctruct the input text and other UI elements.
        documentPane.toBack();
        //Dont show the progressbar as no crawling work is being done.
        progressBar.setVisible(false);
    }

    @FXML
    public void tmpSearchQueryFieldClicked()
    {
        //Enter the text into the main Query Field. (Look at the FXML document for better info)
        searchQueryField.setText(tmpSearchQueryField.getText());
        //Dont allow the user to enter any further text into the temporary field. (INCASE THE CLIENT COMPUTER IS TOO SLOW)
        tmpSearchQueryField.setDisable(true);
        //Remove any text from the temporary field so that the user doesnt have to remove the entire text after his/her's query is
        //completed.
        tmpSearchQueryField.setText("");
        //Show the Query Pane where the results will be shown.
        queryPane.setVisible(true);
        //Bring the query pane to front, so that the other ui elements in the same location do not obstruct the element.
        queryPane.toFront();
    }

    @FXML
    public void searchButtonClicked()
    {
        //Method called when the "অনুসন্ধান" button is clicked.

        //Remove all the previous results from query box (IF ANY)
        resultsVBox.getChildren().clear();
        //Disable it so that user doesnt accidentally clicks while loading (May cause stability issues)
        resultsVBox.setDisable(true);
        //Disable search query field so that user cannot change his/her's query while its being
        //processed.
        searchQueryField.setDisable(true);
        //Disable search button so that user is not able to search for the same thing twice (UI ENHANCEMENT)
        searchButton.setDisable(true);
        //Show the progress bar has the application will now start crawling...
        progressBar.setVisible(true);

        //Asynchronous Thread Task, executed so that the main work is not done in the UI Thread.
        //So that UI thread doesnt freeze.
        Task<Void> t = new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    //Connect to banglapedia with the results.
                    Document rawDocument = Jsoup.connect("http://bn.banglapedia.org/index.php?search="+searchQueryField.getText()+"&fulltext=%E0%A6%85%E0%A6%A8%E0%A7%81%E0%A6%B8%E0%A6%A8%E0%A7%8D%E0%A6%A7%E0%A6%BE%E0%A6%A8").get();
                    //get the results..
                    Elements listOfContent = rawDocument.getElementsByClass("mw-search-results").get(0).getElementsByTag("li");

                    for(Element eachElement : listOfContent)
                    {
                        //Parse every result link and puts it into the resultsVBox.

                        //Get each div element with the result title and link.
                        Element divElement = eachElement.getElementsByClass("mw-search-result-heading").get(0);
                        //Get the <a> tag, containing link to the data page.
                        Element aTag = divElement.getElementsByTag("a").get(0);

                        //Store the heading into a "heading" String variable.
                        String heading = aTag.text();
                        //Store the URL into a "link" String variable.
                        String link = aTag.attr("href");


                        //Create a Label Node with the text : heading.
                        Label eachLabel = new Label(heading);


                        //Set UI Enhancements to the label...

                        //Padding , so that UI doesnt look too cluttered.
                        eachLabel.setPadding(new Insets(10,10,10,10));
                        //Set appropriate font size.
                        eachLabel.setFont(Font.font(17));

                        //set OnMouseClicked Handler.
                        eachLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                //Called when the user clicks a result label.
                                loadDocumentPane(link);
                            }
                        });

                        //Add Rippler, as Material Design UI Enhancement.
                        JFXRippler r = new JFXRippler(eachLabel);
                        //UI Enhancements like padding and Node Alignment.
                        r.setPadding(new Insets(10,10,10,10));
                        r.setAlignment(Pos.TOP_LEFT);

                        //runLater method used to run the following method in UI thread.
                        //As JavaFX doesnt allow accessing UI Elements from Other Background Threads.
                        //UI Elements can only be accessed from the UI Thread.
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //Adding the rippler, with the label inside the resultVBox.
                                resultsVBox.getChildren().add(r);
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    //Called when something goes wrong
                    //Like Internet Connection,etc,etc.
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //Show the alert, that crawling has failed.
                            //Probably due to Internet Connection problem.
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setHeaderText("Failed!");
                            a.setContentText("Please check that you have a valid internet connection\nAlso check whether Bangla Pedia website\nis responding or not ...");
                            a.show();
                        }
                    });
                    e.printStackTrace();
                }

                //Running the below code in UI Thread.
                //Reason already given in Line 189-190
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //Reverse all the UI stuff.
                        //So that user can again send a query...
                        resultsVBox.setDisable(false);
                        searchQueryField.setDisable(false);
                        searchButton.setDisable(false);
                        progressBar.setVisible(false);
                    }
                });

                //This task object is actually an anonymous instance of the Task Class.
                //With the "run" method which always returns null.
                return null;
            }
        };

        //Start the Asynchronous Thread.
        new Thread(t).start();


    }

    public void loadDocumentPane(String link)
    {
        //Loaded when the user clicks on a Label inside the results vbox.

        //Again, an Asynchronous task for crawling the main data.

        Task<Void> loadDocumentPaneTask = new Task<Void>() {
            @Override
            protected Void call(){
                //Setting current link to be proccessed.
                currentLink = link;

                //UI Enhancements,
                //Reason in lines 123-132
                resultsVBox.setDisable(true);
                searchQueryField.setDisable(true);
                searchButton.setDisable(true);
                progressBar.setVisible(true);

                try
                {
                    //Connecting to Bangla Pedia to get crawlable website.
                    Document rawDocument = Jsoup.connect(link).get();
                    //Stores the current Document's Heading inside heading Variable.
                    heading = rawDocument.getElementById("firstHeading").text();

                    //Get all the <p> tag elements (Each <p> tag contains a paragraph of the crawled
                    //data.
                    Elements pTags = rawDocument.getElementById("mw-content-text").getElementsByTag("p");

                    //ps an ArrayList to store all the parragraphs (Now stored in label nodes)
                    ArrayList<Label> ps = new ArrayList<>();
                    for(Element eachPTag : pTags)
                    {
                        //Parse each <p> into text and store into label
                        Label p = new Label(eachPTag.text());
                        //Set Larger Font Size (UI ENHANCEMENT)
                        p.setFont(Font.font(15));
                        //Set Wrap Text
                        p.setWrapText(true);
                        //add the label in the array List
                        ps.add(p);
                    }

                    //UI Enhancements done through UI Thread.
                    //Reason given in Line 190,191
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //Setting the heading Label text as the current heading
                            headingLabel.setText(heading);
                            //Add the new Labels (Parragraph) to the contentVBox.
                            contentVBox.getChildren().addAll(ps);
                            //Show the progressBar
                            progressBar.setVisible(false);
                            //Show the document pane to the user
                            documentPane.setVisible(true);
                            //Bring it to the front to avoid UI obstacles (IF ANY)
                            documentPane.toFront();
                            //Call Garbage Collector to remove all the unused elements
                            //And Free Up JVM Memory.
                            System.gc();
                        }
                    });
                }
                catch (Exception e)
                {
                    //Called when something goes wrong
                    //Like Internet Connection,etc,etc.
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //Show the alert, that crawling has failed.
                            //Probably due to Internet Connection problem.
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setHeaderText("Failed!");
                            a.setContentText("Please check that you have a valid internet connection\nAlso check whether Bangla Pedia website\nis responding or not ...");
                            a.show();
                        }
                    });
                    e.printStackTrace();
                }
                return null;
            }
        };

        //Start the Task in an Asynchronous Thread.
        new Thread(loadDocumentPaneTask).start();
    }

    @FXML
    public void closeDocumentPane()
    {
        //If the "X" button is called, this method will be called.

        //Call Garbage Controller to discard all unused stuff.
        System.gc();
        //Clear all the previous Text Parragraphs (If user has already crawled something before)
        contentVBox.getChildren().clear();
        //Enable resultsVBOX so that user can again click on various links.
        resultsVBox.setDisable(false);
        //Enable Search Query Field so user can ask queries again
        searchQueryField.setDisable(false);
        //Enable Search Button so that user can search again
        searchButton.setDisable(false);
        //Make documentPane invisible to prevent UI Obstacles.
        documentPane.setVisible(false);
        //Make Query Pane visible which contains the resultsVBOX and search field.
        queryPane.setVisible(true);
        //Bring Query Pane to front to prevent UI Obstacles.
        queryPane.toFront();
    }

    @FXML
    public void saveAsButtonClicked()
    {
        //Called when Save As Button is clicked..

        //Show a file chooser.
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
                        ObservableList<Node> ps = documentPane.getChildren();
                        for(Node pNode : ps)
                        {
                            //Casting every pNode to a Label
                            Label l = (Label) pNode;

                            //Appending text to the content variable ...
                            content +=  l.getText()+"\n\n\n";
                        }

                        String toBeWritten;

                        //Due to Character set differences,
                        //Windows uses \n as New line
                        //Linux uses \r\n as New line
                        if(System.getProperty("os.name").toLowerCase().contains("win"))
                        {
                            toBeWritten = "Bangla Pedia Extracter\nBy Debayan Sutradhar (github.com/ladiesman6969)\n\nExtracted From : "+currentLink+"\n\n"+heading+"\n\n\n"+content;
                        }
                        else
                        {
                            toBeWritten  = "Bangla Pedia Extracter\r\nBy Debayan Sutradhar (github.com/ladiesman6969)\r\n\r\nExtracted From : "+currentLink+"\r\n\r\n"+heading+"\r\n\r\n\r\n"+content;
                        }

                        //Write the file content.
                        Writer out = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(file.toString()), "UTF-8"));
                        try {
                            out.write(toBeWritten);
                        } finally {
                            out.close();
                        }

                        //Accessing UI thread
                        //ReasoLine 190,191
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //Show alert that file has been successfully saved...
                                Alert a = new Alert(Alert.AlertType.INFORMATION);
                                a.setHeaderText("Saved!");
                                a.setContentText("Successfully saved to \n"+file.toString());
                                a.show();
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        //Show alert if something goes wrong.
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


            //Start the Task Instance in an Asynchronous Thread.
            new Thread(t).start();
        }
    }
}
