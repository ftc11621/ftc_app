package Library;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public class JewelServo {
    final double BEAM_RAISE     = 0.33;
    final double BEAM_LOWER     = 0.92;

    private boolean isJewelDetected     = false;
    private boolean isJewelRed          = true;
    public int  readRed, readBlue;

    private Servo flickerbeam;
    private REVColorDistance Colordistance = null;

    ElapsedTime flicker_elapsetime = new ElapsedTime();

    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        flickerbeam = hardwareMap.get(Servo.class, "JewelServoBeam");
        Colordistance = new REVColorDistance(hardwareMap);
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
    }

    // =============== Beam methods ==================

    public void Initial() {
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
    }

    /*
    private void RaiseBeam() {
        RotateBeam(BEAM_LOWER, BEAM_RAISE);
    }
    */

    private void LowerBeam(){
        RotateBeam(BEAM_RAISE, BEAM_LOWER);
    }

    private void RotateBeam(double init_position, double final_location){
        double step = (final_location-init_position)/100.0;

        for (int nn = 0; nn < 100; nn++)
        {
            flicker_elapsetime.reset();
            double ns = init_position + nn * step;
            flickerbeam.setPosition(ns);


            if (nn < 80) { // go down faster
                while (flicker_elapsetime.milliseconds() < 10) { // to slow down
                }
            } else {
                while (flicker_elapsetime.milliseconds() < 30) { // to slow down
                }
            }

            if (nn > 90) {   // only when near the jewel
                detectJewel();
            }

        }
    }

    // Change the servo angle range if needed
    private void setServoRange() {
        if (flickerbeam.getController()instanceof ServoControllerEx) { // prevent crash
            ServoControllerEx theControl = (ServoControllerEx) flickerbeam.getController();
            int thePort = flickerbeam.getPortNumber();
            PwmControl.PwmRange theRange = new PwmControl.PwmRange(800, 2000);
            theControl.setServoPwmRange(thePort, theRange);
        }
    }



    // ====================================
    public int flickJewel(boolean isRedAlliance) {

        LowerBeam();

        detectJewel();

        if(isJewelDetected) {
            if (isRedAlliance) { // for Red alliance
                if (isJewelRed) {
                    return -1;  // turn right
                } else {
                    return 1;   // turn left
                }
            } else {              // Blue alliance
                if (isJewelRed) {
                    return 1;   // turn left
                } else {
                    return -1;
                }
            }
        }

        Initial();
        return 0;
    }

    // ==================================================

    private void detectJewel() {
        Colordistance.measure();
        readRed = Colordistance.getRed();
        readBlue = Colordistance.getBlue();

        if (Colordistance.getDistance_CM() < 11.0) {
            if ((readBlue - readRed) >= 4) {
                isJewelDetected = true;
                isJewelRed = false;
            } else if ((readRed - readBlue) > 4) {
                isJewelDetected = true;
                isJewelRed = true;
            }
        }
    }
}