package de.elite.games.dilbert;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;

import static de.elite.games.dilbert.DilbertPageUtil.getUrlDateSuffix;
import static de.elite.games.dilbert.DilbertPageUtil.retrieveImageInfo;

public class App extends Application {

    private static final int PADDING = 20;
    private static final int ERROR_MSG_X = 150;
    private static final int ERROR_MSG_Y = 100;
    private static final String PREV = "<-";
    private static final String NEXT = "->";
    private static final String WINDOW_TITLE = "Dilbert WebComic Viewer";
    private final DatePicker datePicker = new DatePicker();
    private final Canvas canvas = new Canvas();
    private final Color BACKGROUND = Color.rgb(244, 244, 244);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(WINDOW_TITLE);

        datePicker.setOnAction(event -> drawSelected(getUrlDateSuffix(datePicker.getValue())));
        datePicker.setValue(LocalDate.now());
        datePicker.fireEvent(new ActionEvent());

        Button nextDay = new Button(NEXT);
        nextDay.setOnAction(event -> changeDate(true));

        Button prevDay = new Button(PREV);
        prevDay.setOnAction(event -> changeDate(false));

        HBox hBox = new HBox();
        hBox.getChildren().addAll(prevDay, datePicker, nextDay);
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
        border.setTop(hBox);
        ScrollPane scroller = new ScrollPane(canvas);
        border.setCenter(scroller);
        primaryStage.setScene(new Scene(border));
        primaryStage.show();
    }

    private void changeDate(final boolean isForwardInTime) {
        LocalDate selectedDate = datePicker.getValue();
        LocalDate now = LocalDate.now();
        if (isForwardInTime) {
            if (!selectedDate.plusDays(1).isAfter(now)) {//no comics of future
                datePicker.setValue(selectedDate.plusDays(1));
            }
        } else {
            datePicker.setValue(selectedDate.minusDays(1));
        }
    }

    private void drawSelected(final String urlDateSuffix) {
        Runnable r = () -> {
            ImageInfo imageInfo = retrieveImageInfo(urlDateSuffix);
            updateImage(imageInfo);
        };
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();

    }

    private void updateImage(ImageInfo imageInfo) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        wipeContext(gc);
        if (imageInfo != null) {
            canvas.setWidth(imageInfo.getWidth() + 2 * PADDING);
            canvas.setHeight(imageInfo.getHeight() + 2 * PADDING);
            drawImage(gc, imageInfo);
        } else {
            drawError(gc, "Error fetching image info");
        }
    }

    private void wipeContext(GraphicsContext gc) {
        gc.setFill(BACKGROUND);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawImage(GraphicsContext gc, ImageInfo imageInfo) {
        try {
            gc.drawImage(
                    new Image(new URL(imageInfo.getSrc()).openConnection().getInputStream()),
                    PADDING,
                    PADDING,
                    imageInfo.getWidth() + PADDING,
                    imageInfo.getHeight() + PADDING);
        } catch (IOException e) {
            drawError(gc, "Error fetching image", e.toString());
        }
    }

    private void drawError(GraphicsContext gc, String... errors) {
        gc.setStroke(Color.RED);
        gc.strokeText(Arrays.toString(errors), ERROR_MSG_X, ERROR_MSG_Y);
    }

}
