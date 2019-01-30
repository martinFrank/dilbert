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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

import static de.elite.games.dilbert.DilberPageUtil.retrieveImageInfo;

/**
 * Hello world!
 */
public class App extends Application {

    //    private String date;
    private final DatePicker datePicker = new DatePicker();
    private final Canvas canvas = new Canvas(400, 200);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");

        datePicker.setOnAction(event -> {
            LocalDate localDate = datePicker.getValue();
            String date = getDate(localDate.getYear(),
                    localDate.getMonth().getValue(),
                    localDate.getDayOfMonth());
            updateImage(date);
        });

        datePicker.setValue(LocalDate.now());
        datePicker.fireEvent(new ActionEvent());

        Button nextDay = new Button("->");
        nextDay.setOnAction(event -> changeDate(true));

        Button prevDay = new Button("<-");
        prevDay.setOnAction(event -> changeDate(false));

        HBox hBox = new HBox();
        hBox.getChildren().addAll(prevDay, datePicker, nextDay);
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(20, 20, 20, 20));
        border.setTop(hBox);

        border.setCenter(canvas);

        primaryStage.setScene(new Scene(border));
        primaryStage.show();
    }

    private void changeDate(boolean isForwardInTime) {
        LocalDate localDate = datePicker.getValue();
        if (isForwardInTime) {
            datePicker.setValue(localDate.plusDays(1));
        } else {
            datePicker.setValue(localDate.minusDays(1));
        }
    }

    private void updateImage(String date) {
        ImageInfo imageInfo = retrieveImageInfo(date);
        if (imageInfo != null) {
            System.out.println(imageInfo);

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.strokeText("Hello Canvas", 150, 100);

            int height = imageInfo.getHeight();
            int width = imageInfo.getWidth();
            canvas.setHeight(height);
            canvas.setWidth(width);

            try {
                Image image = new Image(new URL(imageInfo.getSrc()).openConnection().getInputStream());
                gc.drawImage(image, 0, 0, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private String getDate(int year, int month, int day) {
        return "" + year + "-" +
                (month <= 9 ? "0" : "") + month + "-" +
                (day <= 9 ? "0" : "") + day;
    }
}
