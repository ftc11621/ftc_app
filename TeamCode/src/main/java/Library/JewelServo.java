package Library;

import java.lang.Thread;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.util.ElapsedTime;

public class JewelServo {
    final double BEAM_RAISE     = 0.33;
    final double BEAM_LOWER     = 0.85;

    private boolean isJewelDetected     = false;
    private boolean isJewelRed          = true;
    public int  readRed, readBlue;

    private Servo flickerbeam;
    private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;

    ElapsedTime flicker_elapsetime = new ElapsedTime();

    public JewelServo(HardwareMap hardwareMap) {    // constructor to create object
        flickerbeam = hardwareMap.get(Servo.class, "JewelServoBeam");
        Colordistance = new REVColorDistance(hardwareMap);
        mecanumDrive = new Mecanum(hardwareMap);
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
    }

    // =============== Beam methods ==================
    private void RaiseBeam() {
        RotateBeam(BEAM_LOWER, BEAM_RAISE);
    }
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

            if (final_location < init_position) { // lowering the beam
                while (flicker_elapsetime.milliseconds() < 30 ) {
                }
                if (nn > 90) {   // only when near the jewel
                    detectJewel();
                }
            } else {
                while (flicker_elapsetime.milliseconds() < 5 ) {
                }
            }
        }
    }

    public void Initial() {
        flickerbeam.setPosition(BEAM_RAISE); //Initialzation sticker add to bot
        mecanumDrive.setCurrentAngle(0.0);
        mecanumDrive.set_angle_locked(0.0);
        mecanumDrive.set_Angle_tolerance(5.0);
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



    // ====================================
    public void flickJewel(boolean isRedAlliance) {

        double turn_power = 0.2;
        double turn_time_sec = 2.5;
        double turn_angle = 15.0;

        LowerBeam();

        detectJewel();

        if(isJewelDetected) {
            if (isRedAlliance) { // for Red alliance
                if (isJewelRed) {
                    Robot_Turn(turn_time_sec, turn_power, -1.0 * turn_angle);
                } else {
                    Robot_Turn(turn_time_sec, turn_power, turn_angle);
                }
            } else {              // Blue alliance
                if (isJewelRed) {
                    Robot_Turn(turn_time_sec, turn_power, turn_angle);
                } else {
                    Robot_Turn(turn_time_sec, turn_power, -1.0 * turn_angle);
                }
            }
        }

        Initial();
    }

    private void detectJewel() {
        Colordistance.measure();
        readRed = Colordistance.getRed();
        readBlue = Colordistance.getBlue();

        if (Colordistance.getDistance_CM() < 11.0) {
            if ((readBlue - readRed) >= 4) {
                isJewelDetected = true;
                isJewelRed = false;
            } else if ((readRed - readBlue) >= 4) {
                isJewelDetected = true;
                isJewelRed = true;
            }
        }
    }

    // ================ Robot Turn ============
    private void Robot_Turn(double time_sec, double power, double angle) {
        mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle() + angle); //Initial_orientation + 15.0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, time_sec, power);
    }

}