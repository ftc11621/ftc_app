package Library;

//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.lang.Thread;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class JewelServo {
    final double BEAM_RAISE     = 0.85;
    final double BEAM_LOWER     = 0.35;

    Servo flicker;
    Servo flickerbeam;
    ElapsedTime flicker_elapsetime = new ElapsedTime();

    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        flicker = hardwareMap.get(Servo.class, "JewelServoFlicker");
        flickerbeam = hardwareMap.get(Servo.class, "JewelServoBeam");
        //flicker.setPosition(0.5);
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
    }

    public void CenterFlick() {flicker.setPosition(0.2);}

    public void LeftFlick()  { flicker.setPosition(0.7);}

    public void RightFlick() { flicker.setPosition(0); }

    // =============== Beam methods ==================
    public void RaiseBeam() {
        RotateBeam(BEAM_LOWER, BEAM_RAISE);
    }
    public void LowerBeam(){
        RotateBeam(BEAM_RAISE, BEAM_LOWER);
    }
    private void RotateBeam(double init_position, double final_location){
        //double init_position = flickerbeam.getPosition();
        double step = (final_location-init_position)/100.0;

        for (int nn = 0; nn < 100; nn++)
        {
            flicker_elapsetime.reset();
            double ns = init_position + nn * step;
            flickerbeam.setPosition(ns);

            while (flicker_elapsetime.milliseconds() < 100 ) {

            }

        }

    }


} // End of run_Motors_encoder_CM ====
