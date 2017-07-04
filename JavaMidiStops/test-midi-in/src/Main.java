import javax.sound.midi.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("yeah");

        findAndSetTransmitter("IAC Bus 1");
    }


    public static void findAndSetTransmitter(String name) {
        MidiDevice deviceTransmitter = null;
        Transmitter transmitter = null;


        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            if (info.getName().equals(name)) {
                try {
                    System.out.println("Opening " + info.getName());
                    deviceTransmitter = MidiSystem.getMidiDevice(info);
                    deviceTransmitter.open();

                    try {
                        Transmitter trns = deviceTransmitter.getTransmitter();

//                        Sequencer sequencer = MidiSystem.getSequencer();
//                        sequencer.open();
//                        sequencer.setSequence( MidiSystem.getSequence() );
//                        sequencer.start();
//                        sequencer.setLoopCount(LOOP_FOREVER);

                        System.out.println("Got Transmitter: " + trns + "\n");
                        if (trns instanceof MidiDeviceTransmitter) {
                            MidiDeviceTransmitter devTrns = (MidiDeviceTransmitter) trns;
                            MidiDevice retDev = devTrns.getMidiDevice();
                            if (retDev == deviceTransmitter) {
                                transmitter = trns;
                            } else {
                                System.out.println("ERROR: getMidiDevice returned incorrect deviceTransmitter: " + retDev);
                            }
                        } else {
                            System.out.println("ERROR: not an instance of MidiDeviceTransmitter");
                        }
                    } catch (MidiUnavailableException ex) {
                        System.out.println("Transmitter: MidiUnavailableException (test NOT failed)");
                    }
                } catch (MidiUnavailableException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
