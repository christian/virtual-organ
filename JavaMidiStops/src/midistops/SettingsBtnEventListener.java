package midistops;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class SettingsBtnEventListener implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
        System.out.println("Invoking settings");

        Settings.show();
    }

}
