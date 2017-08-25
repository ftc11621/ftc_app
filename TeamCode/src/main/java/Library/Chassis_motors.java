package Library;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import  com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.ar.pl.DebugLog;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Locale;
import java.lang.Thread;


public class Chassis_motors
{
    private static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    private static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    private static final double     WHEEL_DIAMETER_CM       = 9.15 ;     // For figuring circumference
    private static final double     COUNTS_PER_CM           = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_CM * Math.PI );


    private static final double     WHEELS_SPACING_CM       = 40.8;     // spacing between wheels for turns

    private DcMotor motorLeft;
    private DcMotor motorRight;
    private ElapsedTime chassis_runtime = new ElapsedTime();

    private double lastleftpower, lastrightpower;

    public Chassis_motors(HardwareMap hardwareMap){    // constructor to create object
        motorLeft = hardwareMap.dcMotor.get("LeftDrive");
        motorRight = hardwareMap.dcMotor.get("RightDrive");
        set_Direction_Forward();                        // forward by default
    }

    // set direction forward
    public void set_Direction_Forward () {
        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        motorRight.setDirection(DcMotor.Direction.FORWARD);
    }

    // set direction reverse
    public void set_Direction_Reverse () {
        motorLeft.setDirection(DcMotor.Direction.FORWARD);
        motorRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void run_Motors_no_encoder(double leftPower, double rightPower) {
        motorLeft.setPower(leftPower);
        motorRight.setPower(rightPower);
    }

    // Run motors with encoders, specify the power, distance in CM (centimeter)
    public void run_Motors_encoder_CM(double power, double leftDistance, double rightDistance, double timeout) {
        int leftlastpos = motorLeft.getCurrentPosition();
        int rightlastpos = motorRight.getCurrentPosition();
        int newLeftTarget  = leftlastpos  + (int)(leftDistance * COUNTS_PER_CM);
        int newRightTarget = rightlastpos + (int)(rightDistance * COUNTS_PER_CM);
        motorLeft.setTargetPosition(newLeftTarget);
        motorRight.setTargetPosition(newRightTarget);

        // Turn On RUN_TO_POSITION
        motorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        chassis_runtime.reset();

        // set the power to start the motors
        //motorLeft.setPower(power);
        //motorRight.setPower(power);


        // keep looping while we are still active, and there is time left, and both motors are running.
        lastleftpower = 0.0;
        lastrightpower = 0.0;
        double newleftpower = power;
        double newrightpower = power;
        while ((chassis_runtime.seconds() < timeout) &&
                (is_motors_busy())) {
            if (Math.abs(motorLeft.getCurrentPosition()-newLeftTarget) < 1000) {
                newleftpower = power* (Math.abs(motorLeft.getCurrentPosition()-newLeftTarget)/1000.0);
            }
            if (Math.abs(motorRight.getCurrentPosition()-newRightTarget) < 1000) {
                newrightpower = power*(Math.abs(motorRight.getCurrentPosition()-newRightTarget)/2000.0);
            }
            //newleftpower = Math.abs(0.5 * (motorLeft.getCurrentPosition()-newLeftTarget)/(newLeftTarget-leftlastpos));
            //newrightpower = Math.abs(0.5 * (motorRight.getCurrentPosition()-newRightTarget)/(newRightTarget-rightlastpos));
            smooth_motors(newleftpower,newrightpower);
            motorLeft.setPower(lastleftpower);
            motorRight.setPower(lastrightpower);

        }

        // Stop all motion;
        motorLeft.setPower(0);
        motorRight.setPower(0);
        lastleftpower = 0.0; lastrightpower = 0.0;

        // Turn off RUN_TO_POSITION
        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    // If either motor is busy
    private boolean is_motors_busy(){
        return motorLeft.isBusy() || motorRight.isBusy();
    }

    private void smooth_motors(double newleft, double newright) {
        lastleftpower = newleft * 0.1 + (1.0-0.1) * lastleftpower;
        lastrightpower = newright * 0.1 + (1.0-0.1) * lastrightpower;

    }


} // End of run_Motors_encoder_CM ====
