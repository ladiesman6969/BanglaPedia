import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        queryPane.setTranslateY(475);
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

    public String getHttpResponse(String urlPassed) throws Exception
    {
        URL url = new URL(urlPassed);
        InputStream is = url.openStream();
        int ptr = 0;
        StringBuilder s = new StringBuilder();
        while ((ptr = is.read()) != -1) {
            s.append((char)ptr);
        }
        return s.toString();
    }

    public void searchButtonClicked()
    {
        searchQueryField.setDisable(true);
        searchButton.setDisable(true);
        progressBar.setVisible(true);

        try
        {
            String htmlToProcess = getHttpResponse("")
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        searchQueryField.setDisable(false);
        searchButton.setDisable(false);
        progressBar.setVisible(false);
    }
}
