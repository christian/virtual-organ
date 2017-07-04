package midistops;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

class MidiEvent implements EventHandler<ActionEvent> {

    private Integer value;
    private Integer channel;

    MidiEvent(Integer channel, Integer value) {
        super();
        this.channel = channel;
        this.value = value;
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            ShortMessage msg = new ShortMessage();
            System.out.println("Sending program change " + this.value + " on channel " + this.channel);
            msg.setMessage(ShortMessage.PROGRAM_CHANGE, this.channel, this.value, 127);

            MidiManager.getReceiver().send(msg, -1);
        } catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
        }
    }

}