package jpaint;
/**
 * @author Carter Brainerd
 * @version 1.0.2 10 Apr 2017
 */
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Paint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import static jpaint.util.*;



/**
 * The class with the FXML functionality methods
 */
@SuppressWarnings("JavaDoc") // For custom tags
public class Controller {


    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField brushSize;

    @FXML
    private CheckBox eraser;

    @FXML
    private MenuButton brushSelectButton;

    // For onSaveAs
    private final FileChooser fileChooser = new FileChooser();

    // For onOpen
    private final FileChooser openFileChooser = new FileChooser();

    // For setBrushBrush and setBrushPencil
    private boolean isBrushBrush;


    /**
     * Called automatically by the <code>FXMLLoader</code>.
     * Allows for the actual painting to happen on the <code>Canvas</code>
     * @since 1.0.0
     * @custom.Updated 1.0.2
     */
    public void initialize() {
        GraphicsContext g = canvas.getGraphicsContext2D();

        setBrushBrush();

        // Get screen dimensions and set the canvas accordingly
        Dimension screenSize = getScreenSize();
        double screenWidth = screenSize.getWidth();
        double screenHeight = screenSize.getHeight();
        canvas.setHeight(screenHeight/1.5);
        canvas.setWidth(screenWidth/1.5);

        canvas.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");  //Set the background to be translucent

        canvas.setOnMouseDragged(e -> {
            double size = Double.parseDouble(brushSize.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            if (eraser.isSelected()) {
                g.clearRect(x, y, size, size);
            } else {
                g.setFill(colorPicker.getValue());
                if(isBrushBrush) {
                    g.fillOval(x, y, size, size);
                } else {
                    g.fillRect(x, y, size, size);
                }
            }
        });

        canvas.setOnMouseClicked(e -> {
            double size = Double.parseDouble(brushSize.getText());
            double x = e.getX() - size / 2;
            double y = e.getY() - size / 2;

            if (eraser.isSelected()) {
                g.clearRect(x, y, size, size);
            } else {
                g.setFill(colorPicker.getValue());
                if(isBrushBrush) {
                    g.fillOval(x, y, size, size);
                } else {
                    g.fillRect(x, y, size, size);
                }
            }
        });

    }


    /**
     * Saves a <code>png</code> snapshot of the image (as of 1.0.0, it's <code>paint.png</code>)
     * @since 1.0.0
     */
    public void onSave(){
        try{

            Image snapshot = canvas.snapshot(null, null);
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("paint.png"));
            infoAlert("Image saved to " + new File("paint.png").getAbsolutePath(), "Save successful.");

        } catch (Exception e){
            alertUser("Error", "Unable to save. Error:" + e.getMessage(), "Error saving", Alert.AlertType.ERROR);
        }
    }


    /**
     * Opens a <code>FileChooser</code> window and saves the image as the inputted name.png
     * @see javafx.stage.FileChooser
     * @see javax.imageio.ImageIO
     * @since 1.0.1
     */
    public void onSaveAs(){
        Stage stage = new Stage(StageStyle.UTILITY);
        fileChooser.setTitle("Save Image As");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        try {

            Image snapshot = canvas.snapshot(null, null);
            File file = fileChooser.showSaveDialog(stage);

            // This is just a failsafe
            if (file != null) {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
            } else {
                infoAlert("Please choose a filename.", null);
            }
        } catch (Exception e){
            alertUser(null, "Unable to save. \nError:" + e.getMessage(), "Error saving", Alert.AlertType.ERROR);
        }

    }

    /**
     * Opens a file and displays it on the <code>Canvas</code>
     * @since 1.0.1
     * @custom.Updated 1.0.2
     */
    public void onOpen(){
        GraphicsContext g = canvas.getGraphicsContext2D();

        Stage stage = new Stage(StageStyle.UNDECORATED);
        openFileChooser.setTitle("Open Image");

        // PNG file filter
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG files", "*.png");
        openFileChooser.getExtensionFilters().add(pngFilter);

        // JPEG file filter
        FileChooser.ExtensionFilter jpegFilter = new FileChooser.ExtensionFilter("JPG files", "*.jpeg, *.jpg");
        openFileChooser.getExtensionFilters().add(jpegFilter);
        try{
            File openImageFile = openFileChooser.showOpenDialog(stage);
            InputStream fileStream = new FileInputStream(openImageFile);
            Image openImage = new Image(fileStream);

            if (openImageFile != null){
                g.drawImage(openImage, 0, 0);
            } else {
                infoAlert("Please choose a file.", null);
            }
        } catch (Exception e){
            alertUser(null, "Unable to open file. \nError:" + e.getMessage(), "Error opening", Alert.AlertType.ERROR);
        }

    }

    /**
     * Gets a confirmation, then exits out of the program
     * @since 1.0.0
     */
    public void onExit(){
        boolean result = confirmExit();
        if(result) {
            Platform.exit();
        } else {
            return;
        }
    }


    /**
     * Displays the "about" message using <code>util.alertUser(String, String, String, Alert.AlertType)</code>
     * @since 1.0.0
     */
    public void displayAbout(){
        String s = "Author: Carter Brainerd\n" +
                "JPaint version: 1.0.2\n" +
                "JPaint is a free and open source software written in JavaFX.\n" +
                "See the source here: https://github.com/thecarterb/JPaint\n";
        alertUser("About JPaint", s, "About JPaint", Alert.AlertType.INFORMATION);
    }

    /**
     * Setter for brush type (Changes it to circle)
     * @since 1.0.2
     */
    public void setBrushBrush(){
        isBrushBrush  = true;
        brushSelectButton.setText("Brush");
    }

    /**
     * Setter for brush type (Changes it to square)
     * @since 1.0.2
     */
    public void setBrushPencil(){
        isBrushBrush  = false;
        brushSelectButton.setText("Pencil");
    }

}
