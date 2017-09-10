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


public class JewelServo
{
    Servo servo;
    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        servo =  hardwareMap.get(Servo.class, "JewelServo");
        servo.setPosition(0.5);
    }


    public void LeftFlick (){
        servo.setPosition(0);
    }


    public void RightFlick(){
        servo.setPosition(1);
    }



} // End of run_Motors_encoder_CM ====
