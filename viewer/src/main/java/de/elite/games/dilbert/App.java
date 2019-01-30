package de.elite.games.dilbert;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
    private static final int CANVAS_WIDTH = 900 + 2 * PADDING;
    private static final int CANVAS_HEIGHT = 480 + 2 * PADDING;
    private static final int ERROR_MSG_X = 150;
    private static final int ERROR_MSG_Y = 100;
    private static final String PREV = "<-";
    private static final String NEXT = "->";
    private static final String WINDOW_TITLE = "Dilbert WebComic Viewer";
    private final DatePicker datePicker = new DatePicker();
    private final Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

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
        border.setCenter(canvas);
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
        ImageInfo imageInfo = retrieveImageInfo(urlDateSuffix);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        wipeContext(gc);
        if (imageInfo != null) {
            updateSize(canvas, imageInfo.getHeight(), imageInfo.getWidth());
            drawImage(gc, imageInfo);
        } else {
            drawError(gc, "Error fetching image info");
        }
    }

    private void wipeContext(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
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

    private void updateSize(Canvas canvas, int height, int width) {
        if (canvas.getHeight() < height + 2 * PADDING) {
            canvas.setHeight(height + 2 * PADDING);
        }
        if (canvas.getWidth() < width + 2 * PADDING) {
            canvas.setWidth(width + 2 * PADDING);
        }
    }

    private void drawError(GraphicsContext gc, String... errors) {
        gc.setStroke(Color.RED);
        gc.strokeText(Arrays.toString(errors), ERROR_MSG_X, ERROR_MSG_Y);
    }

}
