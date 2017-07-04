package midistops;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author
 *
 * cd ~/Play/Hauptwerk/midistops/out/production/hauptwerk-midi-stops
 * jar cf midistops.jar com/prodan/
 * java -jar ~/Downloads/packr.jar --platform mac --jdk /Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/ --executable midistops --classpath midistops.jar --mainclass midistops.Main --minimizejre soft --output out-mac --resources resources/
 *
 *
 */
public class Main extends Application {

    private static final Integer MIDI_CHANNEL = 12;
    public static final int WINDOW_HEIGHT = 1000;
    public static final int WINDOW_WIDTH = 760;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        Rectangle2D bounds = Settings.getScreenBounds();

        ArrayList<Stop> stops = loadStops();
        ArrayList<Stop> stopsPanel1 = stops.stream()
                // .filter(st -> st.getDivision().equals("Pedal") || st.getDivision().equals("Manual I")  || st.getDivision().equals("Manual II"))
                .collect(Collectors.toCollection(ArrayList::new));

//        ArrayList<Stop> stopsPanel2 = stops.stream()
//                .filter(st -> st.getDivision().equals("Manual III"))
//                .collect(Collectors.toCollection(ArrayList::new));

        Panel panel1 = new Panel(stopsPanel1);
//        Panel panel2 = new Panel(stopsPanel2);

        setUserAgentStylesheet(STYLESHEET_CASPIAN);

        StackPane root1 = new StackPane();
        Scene scene1 = new Scene(root1, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setTitle("Cracov John Cantius");
        primaryStage.setScene(scene1);

        Label title = new Label("Cracov John Cantius");

        Button btnSettings = new Button();
        btnSettings.setText("Settings");
        btnSettings.setOnAction(new SettingsBtnEventListener());
        btnSettings.setMinHeight(Stop.BUTTON_HEIGHT);
        btnSettings.setMinWidth(Stop.BUTTON_WIDTH / 2);
        btnSettings.setPadding(new Insets(10, 10, 10, 10));

        HBox footer = new HBox();
        footer.setAlignment(Pos.BOTTOM_RIGHT);
        footer.getChildren().add(btnSettings);

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: ivory;");
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(title, 0, 0);
        grid.add(panel1.getWrapper(), 0, 1);
        grid.add(footer, 0, 2);

        root1.getChildren().add(grid);

        // root1.requestFocus();
        primaryStage.show();


//        Stage secondaryStage = new Stage();
//
//        StackPane root2 = new StackPane();
//        Scene scene2 = new Scene(root2, 600, 800);
//
//        secondaryStage.setTitle("Cracov John Cantius");
//        secondaryStage.setScene(scene2);

//        root2.getChildren().add(panel2.getWrapper());
//        secondaryStage.show();
    }


    private ArrayList<Stop> loadStops() {
        String stopsFileName = getClass().getResource("/resources/cracovjohncantius.txt").getFile();
        ArrayList<Stop> stops = null;

        try (Stream<String> stream = Files.lines(Paths.get(stopsFileName))) {

            stops = stream.map(line -> {
                // TODO empty lines ?
                String[] ln = line.split(Pattern.quote("|"));
                String division = ln[0];
                String name = ln[1];
                Integer midiEventNum = Integer.valueOf(ln[2]);
                String color = ln[3];
                String type = ln[4];
                return new Stop(division, name, midiEventNum, color, type, MIDI_CHANNEL);

            }
            ).collect(Collectors.toCollection(ArrayList::new));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stops;
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        MidiManager.close();
    }
}

