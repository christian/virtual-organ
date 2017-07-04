package midistops;

import javafx.scene.control.ToggleButton;

import javax.sound.midi.Receiver;

class Stop extends ToggleButton {

    private String division;
    private String name;
    private Integer midiEventNum;
    private String color;
    private String type;
    private Integer midiChannel;

    public static final int BUTTON_WIDTH = 175;
    public static final int BUTTON_HEIGHT = 50;

    public Stop(String division, String name, Integer midiEventNum, String color, String type, Integer midiChannel) {
        super();

        if (name.length() < 18) {
            this.name = name;
        } else {
            this.name = name.replaceFirst(" ", "\n");
        }

        this.midiEventNum = midiEventNum;
        this.color = color;
        this.division = division;
        this.type = type;
        this.midiChannel = midiChannel;

        setText(this.name);
        setOnAction(new MidiEvent(this.midiChannel, this.midiEventNum));

        this.setMaxWidth(Double.MAX_VALUE);
        this.setMinWidth(BUTTON_WIDTH);
        // this.setMinWidth(Control.USE_PREF_SIZE);
        this.setMinHeight(BUTTON_HEIGHT);

    }

    public String getDivision() {
        return this.division;
    }

    public String getColor() {
        return this.color;
    }

    @Override
    public void requestFocus() { }

}
