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
    private REVColorDistance Colordistance = null;
    final int beam_steps = 30;

    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        flicker = hardwareMap.get(Servo.class, "JewelServoFlicker");
        flickerbeam = hardwareMap.get(Servo.class, "JewelServoBeam");
        Colordistance = new REVColorDistance(hardwareMap);

    }

    public void CenterFlick() {
        flicker.setPosition(0.5);
    }
    public void LeftFlick() {
        flicker.setPosition(0.0);
    }
    public void RightFlick() {
        flicker.setPosition(1);
    }

    public void LowerBeam() {
        int nn;
        for (nn = 0; nn < beam_steps; nn++)
        {
            double ns = nn * 0.5 / beam_steps;
            flickerbeam.setPosition(ns);
        }
    }
    public void RaiseBeam(){
        int nn;
        for (nn = 0; nn > -beam_steps; nn--)
        {
            double ns = 0.5 + nn * 0.5 / beam_steps;
            flickerbeam.setPosition(ns);
        }
    }

    // Lower beam and flick the jewel
    public void runFlickJewel(boolean isRedAlliance) {
        CenterFlick();
        LowerBeam();

        Colordistance.measure();

        if (isRedAlliance) { // for Red alliance
            if (Colordistance.getBlue() > 20 && Colordistance.getRed() < 15) {
                LeftFlick();
            } else if (Colordistance.getRed() > 20 && Colordistance.getBlue() < 15) {
                RightFlick();
            }
        } else {              // for Blue alliance
            if (Colordistance.getRed() > 20 && Colordistance.getBlue() < 15) {
                LeftFlick();
            } else if (Colordistance.getBlue() > 20 && Colordistance.getRed() < 15) {
                RightFlick();
            }
        }

        LeftFlick();
        RaiseBeam();
    }


} // End of run_Motors_encoder_CM ====
