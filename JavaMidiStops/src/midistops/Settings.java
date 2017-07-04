package midistops;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Settings {

    private static Stage stage = null;

    private static ObservableList<String> midiReceiverNames;
    private static ComboBox comboBoxMidiInput;
    private static ComboBox comboBoxMidiOutput;
    private static ComboBox comboBoxOrgan;

    private static final int WINDOW_WIDTH = 350;
    private static final int WINDOW_HEIGHT = 200;

    static {
        if (stage == null) {
            stage = new Stage();
        }

        StackPane root = new StackPane();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        Rectangle2D bounds = getScreenBounds();
        stage.setX(bounds.getMinX() + Main.WINDOW_WIDTH / 2 - WINDOW_WIDTH / 2);
        stage.setY(bounds.getMinY() + Main.WINDOW_HEIGHT / 2 - WINDOW_HEIGHT / 2);
        stage.setTitle("Settings");
        stage.setScene(scene);

        MidiManager.detectMidiDevices();

        midiReceiverNames = FXCollections.observableArrayList( MidiManager.getDevices() );
        comboBoxMidiInput = new ComboBox(midiReceiverNames);

        comboBoxMidiInput.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("Selected MIDI out receiver: " + newValue);
                MidiManager.findAndSetReceiver((String)newValue);
            }
        });

        comboBoxMidiOutput = new ComboBox(midiReceiverNames);

        comboBoxMidiOutput.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("Selected MIDI in transmitter: " + newValue);
                MidiManager.findAndSetTransmitter((String)newValue);
            }
        });

        String[] organs = new String[]{"Cracov John Cantius"};
        comboBoxOrgan = new ComboBox(FXCollections.observableArrayList(organs));

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        grid.add(new Label("MIDI Output: "), 0, 0);
        grid.add(comboBoxMidiInput, 1, 0);
        grid.add(new Label("MIDI Input: "), 0, 1);
        grid.add(comboBoxMidiOutput, 1, 1);
        grid.add(new Label("Select Organ: "), 0, 2);
        grid.add(comboBoxOrgan, 1, 2);
//        grid.add(new Label("Priority: "), 2, 0);
//        grid.add(priorityComboBox, 3, 0);
//        grid.add(new Label("Subject: "), 0, 1);
//        grid.add(subject, 1, 1, 3, 1);
//        grid.add(text, 0, 2, 4, 1);
//        grid.add(button, 0, 3);
//        grid.add (notification, 1, 3, 3, 1);

        root.getChildren().add(grid);
    }

    public static void show() {
        stage.show();
    }


    public static Rectangle2D getScreenBounds() {
        int numScreens = Screen.getScreens().size();
        if (numScreens == 2) {
            return Screen.getScreens().get(1).getVisualBounds();
        } else {
            return Screen.getScreens().get(0).getVisualBounds();
        }
    }

}
