package Library;

//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.lang.Thread;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public class JewelServo {
    final double BEAM_RAISE     = 0.55;
    final double BEAM_LOWER     = 0.0;

    public boolean isJewelDetected     = false;
    public boolean isJewelRed          = true;

    Servo flickerbeam;
    private REVColorDistance Colordistance = null;
    ElapsedTime flicker_elapsetime = new ElapsedTime();

    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        flickerbeam = hardwareMap.get(Servo.class, "JewelServoBeam");
        Colordistance = new REVColorDistance(hardwareMap);
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
    }

    // =============== Beam methods ==================
    public void RaiseBeam() {
        RotateBeam(BEAM_LOWER, BEAM_RAISE);
    }
    public void LowerBeam(){
        RotateBeam(BEAM_RAISE, BEAM_LOWER);
    }

    private void RotateBeam(double init_position, double final_location){
        double step = (final_location-init_position)/100.0;

        for (int nn = 0; nn < 100; nn++)
        {
            flicker_elapsetime.reset();
            double ns = init_position + nn * step;
            flickerbeam.setPosition(ns);



            if (final_location < init_position) { // lowering the beam
                while (flicker_elapsetime.milliseconds() < 30 ) {
                }
                if (nn > 90) {   // only when near the jewel
                    detectJewel();
                }
            } else {
                while (flicker_elapsetime.milliseconds() < 20 ) {
                }
            }
        }
    }

    public void detectJewel() {
        if (Colordistance.getDistance_CM() < 11.0) {
            if ((Colordistance.getBlue() - Colordistance.getRed()) > 4) {
                isJewelDetected = true;
                isJewelRed = false;
            } else if ((Colordistance.getRed() - Colordistance.getBlue()) > 4) {
                isJewelDetected = true;
                isJewelRed = true;
            }
        }
    }

    public void Initial() {
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
    }

    // Change the servo angle range
    private void setServoRange() {
        if (flickerbeam.getController()instanceof ServoControllerEx) { // prevent crash
            ServoControllerEx theControl = (ServoControllerEx) flickerbeam.getController();
            int thePort = flickerbeam.getPortNumber();
            PwmControl.PwmRange theRange = new PwmControl.PwmRange(800, 2000);
            theControl.setServoPwmRange(thePort, theRange);
        }
    }


} // End of run_Motors_encoder_CM ====