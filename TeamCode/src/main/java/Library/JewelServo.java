package Library;

//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.vuforia.ar.pl.DebugLog;

//import org.firstinspires.ftc.robotcore.external.Func;
//import org.firstinspires.ftc.robotcore.external.Telemetry;

//import java.util.Locale;
//import java.lang.Thread;


public class JewelServo {
    Servo flicker;
    Servo flickerbeam;

    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        flicker = hardwareMap.get(Servo.class, "JewelServoFlicker");
        flickerbeam = hardwareMap.get(Servo.class, "JewelServoBeam");
        //flicker.setPosition(0.0);
        //flickerbeam.setPosition(0.0); //Initialzation sticker add to bot
    }

public void CenterFlick() {flicker.setPosition(0.5);}
    public void LeftFlick() {
        flicker.setPosition(0.0);
    }


    public void RightFlick() {

        flicker.setPosition(1);
    }

    public void LowerBeam() {
        int nn;
        for (nn = 0; nn < 20; nn++)
        {
            float ns = nn*0.025f;
            flickerbeam.setPosition(ns);
        }


    }
    public void RaiseBeam(){
        int nn;
        for (nn = 0; nn > -20; nn--)
        {
            float ns =.5f + nn*0.025f;
            flickerbeam.setPosition(ns);
        }
    }



} // End of run_Motors_encoder_CM ====
