package midistops;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class Panel {

    private ArrayList<Stop> stops;
    private GridPane wrapper = new GridPane();

    private final int MAX_ROWS = 13;
    private final int MAX_COLS = 4;

    public Panel(ArrayList<Stop> stops) {
        this.stops = stops;

        wrapper.getStylesheets().add(getClass().getResource("/resources/style.css").toExternalForm());
//        wrapper.setPadding(new Insets(10, 10, 10, 10));
        wrapper.setVgap(10.0);
        wrapper.setHgap(10.0);

        String lastDivision = "";

        for (int i = 0; i < stops.size(); i++) {
            stops.get(i).setStyle("-fx-text-fill: " + stops.get(i).getColor() + ";");

            if (!stops.get(i).getDivision().equals(lastDivision)) {
                lastDivision = stops.get(i).getDivision();

                Label divLabel = new Label(lastDivision);
                divLabel.getStyleClass().add("division-label");

                wrapper.add(divLabel, i / MAX_ROWS, i % MAX_ROWS);
                wrapper.add(stops.get(i), i / MAX_ROWS, i % MAX_ROWS + 1); // col, row
            } else {
                wrapper.add(stops.get(i), i / MAX_ROWS, i % MAX_ROWS); // col, row
            }

        }

    }

    public GridPane getWrapper() {
        return this.wrapper;
    }

}
