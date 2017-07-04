package midistops;

import javax.sound.midi.*;
import java.util.ArrayList;

public class MidiManager {

    private static Receiver receiver = null;       // for MIDI out
    private static Transmitter transmitter = null; // for MIDI in
    private static MidiDevice deviceReceiver = null;
    private static MidiDevice deviceTransmitter = null;

    private static ArrayList<String> devices = new ArrayList<String>();

    public static ArrayList<String> getDevices() {
        return devices;
    }

    public static Transmitter getTransmitter() {
        return transmitter;
    }

    public static void setTransmitter(Transmitter trns) {
        transmitter = trns;
    }

    public static Receiver getReceiver() {
        return receiver;
    }

    public static void setReceiver(Receiver rcvr) {
        receiver = rcvr;
    }

    public static MidiDevice getDeviceReceiver() {
        return deviceReceiver;
    }


    public static void close() {
        if (receiver != null) {
            System.out.println("Closing receiver");
            receiver.close();
        }
        if (deviceReceiver != null) {
            System.out.println("Closing deviceReceiver");
            deviceReceiver.close();
        }
        if (transmitter != null) {
            System.out.println("Closing transmitter");
            transmitter.close();
        }
    }

    public static void detectMidiDevices() {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            if (!devices.contains((String)info.getName())) {
                devices.add(info.getName());
            }
        }
        System.out.println("Possible devices: " + devices);
    }

    public static void findAndSetTransmitter(String name) {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            if (info.getName().equals(name)) {
                try {
                    deviceTransmitter = MidiSystem.getMidiDevice(info);
                    deviceTransmitter.open();

                    try {
                        Transmitter trns = deviceTransmitter.getTransmitter();
                        System.out.println("Got Transmitter: " + trns + "\n");
                        if (trns instanceof MidiDeviceTransmitter) {
                            MidiDeviceTransmitter devTrns = (MidiDeviceTransmitter) trns;
                            MidiDevice retDev = devTrns.getMidiDevice();
                            if (retDev == deviceTransmitter) {
                                setTransmitter(trns);
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

    public static void findAndSetReceiver(String name) {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            if (info.getName().equals(name)) {
                try {
                    deviceReceiver = MidiSystem.getMidiDevice(info);
                    deviceReceiver.open();

                    try {
                        Receiver recv = deviceReceiver.getReceiver();
                        System.out.println("Got Receiver: " + recv + "\n");
                        if (recv instanceof MidiDeviceReceiver) {
                            MidiDeviceReceiver devRecv = (MidiDeviceReceiver) recv;
                            MidiDevice retDev = devRecv.getMidiDevice();
                            if (retDev == deviceReceiver) {
                                setReceiver(recv);
                            } else {
                                System.out.println("ERROR: getMidiDevice returned incorrect deviceReceiver: " + retDev);
                            }
                        } else {
                            System.out.println("ERROR: not an instance of MidiDeviceReceiver");
                        }
                    } catch (MidiUnavailableException ex) {
                        System.out.println("Receiver: MidiUnavailableException (test NOT failed)");
                    }
                } catch (MidiUnavailableException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
