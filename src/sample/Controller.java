package sample;


import genetic.Chart2D;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private TextField functionInput, rangeInput, stepsInput;

    @FXML
    private HBox box;

    final JavaFXChartFactory factory = new JavaFXChartFactory();

    @FXML
    private BorderPane body;
    @FXML
    private void initialize() { Platform.runLater( () -> body.requestFocus() ); }

    @FXML
    private Label funcLabel, rangeLabel, stepsLabel;

    private final String FERROR = "✖ Empty Function";
    private final String RERROR = "✖ Empty Range";
    private final String SERROR = "✖ Empty Steps";
    private final String WEBPAGE = "/home/kosmos/Desktop/testtt/src/web/javaapis/javaApis.html";
    private ImageView imageView = null;


    @FXML
    private void onHover() {

    }

    @FXML
    public void calculate() {
        box.getChildren().clear();
        if (checkEmptyFields()) {
            if(checkValidation()) {
                return;
            } else {
                functionInput.getStyleClass().remove("error");
                rangeInput.getStyleClass().remove("error");
                stepsInput.getStyleClass().remove("error");
                box.getChildren().clear();
                AWTChart chart = getChart(factory, "offscreen", functionInput.getText(), rangeInput.getText(), stepsInput.getText());
                Color c = new Color(255, 250, 240);
                chart.getView().setBackgroundColor(c);
                imageView = factory.bindImageView(chart);
                imageView.fitHeightProperty();
                imageView.fitWidthProperty();

                //imageView.fitWidthProperty().bind(box.widthProperty());
                //imageView.fitHeightProperty().bind(box.widthProperty());
                imageView.setFitHeight(462);
                imageView.setFitWidth(526);
                //img = imageView;

                box.getChildren().add(imageView);
            }
        }

    }

    private boolean checkValidation() {
        final String INVALID_RANGE = "✖ Invalid range";
        String range = this.rangeInput.getText();
        String[] edges = range.split(" ");
        if (!edges[0].isEmpty() && !edges[0].isEmpty()) {
            if (Integer.valueOf(edges[0].toString()) > Integer.valueOf(edges[1].toString())) {
                Toolkit.getDefaultToolkit().beep();
                this.rangeLabel.setText(INVALID_RANGE);
                return true;
            }
        }
        return false;

    }

    private boolean checkEmptyFields() {

            boolean test = true;
        if (functionInput.getText().isEmpty() && rangeInput.getText().isEmpty() && stepsInput.getText().isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            this.funcLabel.setText(FERROR);
            this.rangeLabel.setText(RERROR);
            this.stepsLabel.setText(SERROR);
            functionInput.getStyleClass().add("error");
            rangeInput.getStyleClass().add("error");
            stepsInput.getStyleClass().add("error");
            return false;
        } else {
            if (functionInput.getText().isEmpty()) {
                this.funcLabel.setText(FERROR);
                Toolkit.getDefaultToolkit().beep();
                test = false;
            } else {
                this.funcLabel.setText(null);
            }
            if (rangeInput.getText().isEmpty()) {
                this.rangeLabel.setText(RERROR);
                Toolkit.getDefaultToolkit().beep();
                test = false;
            } else {
                this.rangeLabel.setText(null);
            }
            if (stepsInput.getText().isEmpty()) {
                this.stepsLabel.setText(SERROR);
                Toolkit.getDefaultToolkit().beep();
                test = false;
            } else {
                this.stepsLabel.setText(null);
            }
        }

        return test;
    }



    private AWTChart getChart(JavaFXChartFactory factory, String toolkit, String dataFunction, String rang, String step) {
        // -------------------------------
        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                Function function = new Function("At(x,y) = " + dataFunction);

                Expression e1 = new Expression("At("+x+","+y+")",function);

                return e1.calculate();
            }
        };

        // Define range and precision for the function to plot

        String[] edges = rang.split(" ");
        /*double s = Double.valueOf(step);
        Range range = new Range(Integer.valueOf(edges[0]), Integer.valueOf(edges[1]));
        int steps = 100 - (int)(s * 100);*/
        Range range = new Range(Integer.valueOf(edges[0]), Integer.valueOf(edges[1]));
        int steps = 100 - ((int) (Double.valueOf(step) * 100));

        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(mapper, range, steps);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(),new Color(1, 1, 1, .8f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(Color.BLACK);

        Quality quality = Quality.Advanced;
        quality.setSmoothPolygon(true);
        quality.setAnimated(true);

        AWTChart chart = (AWTChart) factory.newChart(quality, toolkit);
        chart.getScene().getGraph().add(surface);

        return chart;
    }
    @FXML
    private void exit() {
        Platform.exit();
    }

    @FXML
    private void clearFields() {
        functionInput.setText("");
        rangeInput.setText("");
        stepsInput.setText("");
        box.getChildren().clear();
        this.imageView = null;
    }

    @FXML
    private void saveAsImage() throws IOException{
        String a;
        if(imageView == null) {
            if (!box.getChildren().isEmpty()) {
                //System.out.println("sasasa");
                String s = saveAsPng();
                infoDialog(s, "Saved!");
                //System.out.println("saved");
            } else {
                Toolkit.getDefaultToolkit().beep();
                errorDialog("No image Detected.", "Error");
                return;
            }
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(),
                            null), "png", file);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
            a = file.getAbsolutePath();
            infoDialog(a, "Saved!");
            return;
        }


    }

    private void errorDialog(String infoMessage, String titleBar) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    private void infoDialog(String infoMessage, String titleBar) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText("Saved at : " + infoMessage);
        alert.showAndWait();
    }


    @FXML
    private void openWeb() {
        try {
            File f = new File(WEBPAGE);
            Desktop.getDesktop().open(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void optimize() {

        if (checkEmptyFields()) {
            if(checkValidation()) {
                return;
            } else {
                String[] edges = rangeInput.getText().split(" ");
                Double d1 = new Double(edges[0]);
                Double d2 = new Double(edges[1]);
                box.getChildren().clear();
                box.getChildren().add(new Chart2D(functionInput.getText(), d1, d2).getMyChart());
            }
        }

    }

    public String saveAsPng() {
        WritableImage image = this.box.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save LineChart");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image,
                        null), "png", file);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            System.out.println(e);
        }

        return file.getAbsolutePath();
    }

}
