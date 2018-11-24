import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class mainScreenController implements Initializable {
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

    HashMap<String,String> results = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.setProperty("jsse.enableSNIExtension", "false");
        queryPane.setTranslateY(475);
        documentPane.setTranslateY(475);
        progressBar.setVisible(false);
    }

    @FXML
    public void tmpSearchQueryFieldClicked()
    {
        searchQueryField.setText(tmpSearchQueryField.getText());
        tmpSearchQueryField.setDisable(true);
        tmpSearchQueryField.setText("");
        KeyFrame f1 = new KeyFrame(Duration.millis(300),new KeyValue(queryPane.translateYProperty(),0,Interpolator.EASE_IN));
        queryPane.toFront();
        new Timeline(f1).play();
    }

    public void searchButtonClicked()
    {
        resultsVBox.setDisable(true);
        searchQueryField.setDisable(true);
        searchButton.setDisable(true);
        progressBar.setVisible(true);

        Task<Void> t = new Task<Void>() {
            @Override
            protected Void call(){
                try
                {
                    //String htmlToProcess = getHttpResponse("http://bn.banglapedia.org/index.php?search="+searchQueryField.getText()+"&fulltext=%E0%A6%85%E0%A6%A8%E0%A7%81%E0%A6%B8%E0%A6%A8%E0%A7%8D%E0%A6%A7%E0%A6%BE%E0%A6%A8");
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

                    resultsVBox.setDisable(false);
                    searchQueryField.setDisable(false);
                    searchButton.setDisable(false);
                    progressBar.setVisible(false);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
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
                resultsVBox.setDisable(true);
                searchQueryField.setDisable(true);
                searchButton.setDisable(true);
                progressBar.setVisible(true);

                try
                {
                    Document rawDocument = Jsoup.connect(link).get();
                    String heading = rawDocument.getElementById("firstHeading").text();
                    Elements pTags = rawDocument.getElementById("mw-content-text").getElementsByTag("p");

                    StringBuilder c = new StringBuilder();
                    for(Element eachPTag : pTags)
                    {
                        c.append(eachPTag.text()+"\n\n");
                    }

                    String content = c.toString();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            headingLabel.setText(heading);
                            contentLabel.setText(content);
                            try
                            {
                                Thread.sleep(500);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            KeyFrame f1 = new KeyFrame(Duration.millis(500),new KeyValue(documentPane.translateYProperty(),0,Interpolator.EASE_IN));
                            documentPane.toFront();
                            new Timeline(f1).play();
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
}
