#include <MIDI.h>
#include <midi_Defs.h>
#include <midi_Message.h>
#include <midi_Namespace.h>
#include <midi_Settings.h>
#include <Bounce2.h>
#include <Wire.h>
#include <LCD.h>
#include <LiquidCrystal_I2C.h>


/**
 * Combinations system for Hauptwerk 4. 
 * @Author Cristi Prodan 4-Aug-2016
 * 
 * The combinations have the follwoing layout corresponding to the manuals:
 *                                            
 *  a b c d                    -   +                
 * 
 *  -  +                       -   +
 * 
 *  S         0  1  2  3  4    -   +    5  6  7  8  9     ---  --  ++  +++   C
 * 
 * 
 * MIDI signals:
 * - S (set) - note on/ not off
 * - others  - program change
 * 
 * Button handling inspired by: https://teensyhauptwerk.wordpress.com/2014/01/28/24-buttons-8-leds-2-pins/
 * 
 * 
 * MIDI out -> Print info from Hauptwerk to LCD Screen. 
 * Demonstration sketch for PCF8574T I2C LCD Backpack
 * Uses library from https://bitbucket.org/fmalpartida/new-liquidcrystal/downloads GNU General Public License, version 3 (GPL-3.0)
 * 
 * 
 * To flash HIDUNIO firmare (also checkout PimpMyHiduino: http://ec2-54-175-5-211.compute-1.amazonaws.com/):
 * - short pins as described here (first two pins whe looking at the board with the osb port oriented upper-left): https://www.arduino.cc/en/Hacking/DFUProgramming8U2
 * - sudo dfu-programmer atmega16u2 erase
 * - sudo dfu-programmer atmega16u2 flash hiduino_combi_prodan_firmware.hex / MEGA-dfu_and_usbserial_combined.hex
 * - sudo dfu-programmer atmega16u2 reset 
 * - reset device
 * 
 * When testing with Hailess MIDI, use MIDI_CREATE_CUSTOM_INSTANCE and not MIDI_CREATE_DEFAULT_INSTANCE(). 
 * 
 */

//struct MySettings : public midi::DefaultSettings
//{
//  static const long BaudRate = 57600; //38400; // 57600; // 31250 - official MIDI baud rate, better 57600;
//};
//MIDI_CREATE_CUSTOM_INSTANCE(HardwareSerial, Serial, MIDI, MySettings);
MIDI_CREATE_DEFAULT_INSTANCE();


/*** COMBINATION BUTTONS ***/
#define LED 13     // LED pin on Arduino

const int NUM_BUTTONS = 29;
const int MIDI_CHANNEL = 10;

// Create Bounce objects for each button and switch. The Bounce object
// automatically deals with contact chatter or "bounce", and
// it makes detecting changes very simple.
// 5 = 5 ms debounce time which is appropriate for good quality mechanical push buttons.
// If a button is too "sensitive" to rapid touch, you can increase this time.

//button debounce time
const int DEBOUNCE_TIME = 5;

Bounce buttons[NUM_BUTTONS + 1] =
{
  Bounce(), Bounce(), Bounce(), Bounce(), Bounce(),
  Bounce(), Bounce(), Bounce(), Bounce(), Bounce(),
  Bounce(), Bounce(), Bounce(), Bounce(), Bounce(),
  Bounce(), Bounce(), Bounce(), Bounce(), Bounce(),
  Bounce(), Bounce(), Bounce(), Bounce(), Bounce(),
  Bounce(), Bounce(), Bounce(), Bounce()
};

// low C: 36; middle C: 60; high C: 96
// These are the MIDI PROGRAM CHANGE signals 
// - 46 - combination backward by 1; 48 - combination forward by 1
const int MIDI_MSG[NUM_BUTTONS] = {80, 
                                   36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46,
                                   47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 46,
                                   58, 48, 48, 46, 48, 46};  

// 2 is the SET button, it sends NOTE ON/ NOTE OFF, the other are sending PROGRAM CHANGE.
const int PINS[NUM_BUTTONS]     = {22, 
                                   30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
                                   41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
                                   52, 53, 13, 12, 11, 10}; // 13, 12, 10, 11 - second piston rail


/*** LCD OUTPUTT ***/
LiquidCrystal_I2C  lcd(0x27, 2, 1, 0, 4, 5, 6, 7); // 0x27 is the I2C bus address for an unmodified backpack
const byte ID_HAUPTWERK = 0x7D; //ID for Hauptwerk
const byte MIDI_SYSEX = 7; //code for SysEx command

String hwline1 = "";
String hwline2 = "";

int midiType = 0;
int data1 = 0;
int data2 = 0;


void setup()
{
  pinMode(LED, OUTPUT);
  MIDI.begin(MIDI_CHANNEL);   // Launch MIDI and listen to channel 10

  setupLCD();
  setupButtons();
}

/**
 * Setup LCD screen. 
 */
void setupLCD() 
{
  // activate LCD module
  lcd.begin(16, 2); // for 16 x 2 LCD module
  lcd.setBacklightPin(3, POSITIVE);
  lcd.setBacklight(HIGH);

  hwline1 = "Hauptwerk";
  hwline2 = "Initializing ...";
  updateDisplay();
}

/**
 * Setup Combination buttons.
 */
void setupButtons()
{
  // Configure the pins for input mode with pullup resistors.
  // The buttons/switch connect from each pin to ground.  When
  // the button is pressed/on, the pin reads LOW because the button
  // shorts it to ground.  When released/off, the pin reads HIGH
  // because the pullup resistor connects to +5 volts inside
  // the chip.  LOW for "on", and HIGH for "off" may seem
  // backwards, but using the on-chip pullup resistors is very
  // convenient.  The scheme is called "active low", and it's
  // very commonly used in electronics... so much that the chip
  // has built-in pullup resistors!

  for (int i = 0; i < NUM_BUTTONS; i++)
  {
    pinMode(PINS[i], INPUT_PULLUP);
    buttons[i].attach(PINS[i]);
    buttons[i].interval(5);
  }
}

void loop()
{
  processCombinationButtons();
  processIncomingMIDIforLCD();
}

// For the set button
int buttonPushCounter = 0;   // counter for the number of button presses
int buttonState = 0;         // current state of the button
int lastButtonState = 0;     // previous state of the button

void processCombinationButtons()
{
  // Update all the buttons/switch. There should not be any long
  // delays in loop(), so this runs repetitively at a rate
  // faster than the buttons could be pressed and released.
  for (int i = 0; i < NUM_BUTTONS + 1; i++) {
    buttons[i].update();
  }

  // PINS[0] - SET Button
  // read the pushbutton input pin:
  buttonState = digitalRead(PINS[0]);
  if (buttonState != lastButtonState) {
    if (buttonState == HIGH) {
      MIDI.sendNoteOn(MIDI_MSG[0], 127, MIDI_CHANNEL);
    } else {
      MIDI.sendNoteOff(MIDI_MSG[0], 0, MIDI_CHANNEL);
    }
    delay(50);
  }
  lastButtonState = buttonState;

  // Check the status of each push button
  // i starts from 1: ignores the SET button
  for (int i = 1; i < NUM_BUTTONS; i++) {
    // Check each button for "falling" edge.
    // Falling = high (not pressed - voltage from pullup resistor) to low (pressed - button connects pin to ground)
    if (buttons[i].fallingEdge()) {
      MIDI.sendProgramChange(MIDI_MSG[i], MIDI_CHANNEL);
    } 
  }
}

void processIncomingMIDIforLCD() 
{
  // Process incoming MIDI traffic for Sysex (no event handler)
  if (MIDI.read()) {
    midiType = MIDI.getType();
    data1 = MIDI.getData1();
    data2 = MIDI.getData2();
    // We are interested in SysEx messages
    if ((midiType == midi::SystemExclusive) && (data1 < 80)) {
      // This is a SysEx message and will fit in our array
      const byte *sysexmessage = MIDI.getSysExArray();
      hwline1 = "";
      hwline2 = "";
      if ((sysexmessage[1] == ID_HAUPTWERK) && (data1 > 24)) {
        // This is from Hauptwerk

        for (int x = 6; x < (22); x++) {
          // Step through sysex message
          byte c = sysexmessage[x];
          hwline1 = hwline1 + char(c);
        }

        for (int x = 22; x < (37); x++) {
          // Step through sysex message
          byte c = sysexmessage[x];
          hwline2 = hwline2 + char(c);
        }

        updateDisplay();
      }
    } 
  } 
}

void updateDisplay() {
  lcd.home(); // set cursor to 0,0
  lcd.print(hwline1);
  lcd.setCursor(0, 1);
  lcd.print(hwline2);
}


