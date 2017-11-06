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

    Servo flicker;
    Servo flickerbeam;

    ElapsedTime flicker_elapsetime = new ElapsedTime();

    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        //flicker = hardwareMap.get(Servo.class, "JewelServoFlicker");
        flickerbeam = hardwareMap.get(Servo.class, "JewelServoBeam");
        //flicker.setPosition(0.5);
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

            while (flicker_elapsetime.milliseconds() < 20 ) {
            }
        }
    }

    public void Initial() {
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
    }

    // Change the servo angle range
    private void setServoRange() {
        if (flicker.getController()instanceof ServoControllerEx) { // prevent crash
            ServoControllerEx theControl = (ServoControllerEx) flicker.getController();
            int thePort = flicker.getPortNumber();
            PwmControl.PwmRange theRange = new PwmControl.PwmRange(800, 2000);
            theControl.setServoPwmRange(thePort, theRange);
        }
    }


} // End of run_Motors_encoder_CM ====