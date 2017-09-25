package Archives;

//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
import  com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
//import com.vuforia.ar.pl.DebugLog;

//import org.firstinspires.ftc.robotcore.external.Func;
//import org.firstinspires.ftc.robotcore.external.Telemetry;

//import java.util.Locale;
//import java.lang.Thread;


public class Chassis_motors
{
    // chassis and wheel settings
    private static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    private static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    private static final double     WHEEL_DIAMETER_CM       = 9.15 ;     // For figuring circumference
    private static final double     COUNTS_PER_CM           = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_CM * Math.PI );
    private static final double     WHEELS_SPACING_CM       = 40.8;     // spacing between wheels for turns
    private static final double     SMOOTHING_COEFFICIENT   = 0.02;      // to smooth out power changes, smaller=smoother
    //private static final int        MOTOR_STOP_TOLERANCE    = 50;       // encoder count tolerance to stop
    private static final double     MOTOR_STOP_POWER        = 0.3;      // power just before stopping

    private DcMotor motorLeft;
    private DcMotor motorRight;
    private ElapsedTime chassis_runtime = new ElapsedTime();

    private double lastleftpower, lastrightpower;
    private double leftDistance_actual, rightDistance_actual;         // actual distance by encoders

    public Chassis_motors(HardwareMap hardwareMap){    // constructor to create object
        motorLeft = hardwareMap.dcMotor.get("LeftDrive");
        motorRight = hardwareMap.dcMotor.get("RightDrive");
        set_Direction_Forward();                        // forward by default
        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //also works when encoder is not used
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//also works when encoder is not used
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

    // spin, positive to the right
    public void spin_encoder_degree(double power, double turnangle, double timeout, double distance_tolerance) {
        double totalDistanceToMove = Math.PI*WHEELS_SPACING_CM * turnangle / 360.0;// get total distance to move in centimeters
        run_Motors_encoder(power , totalDistanceToMove,-1 * totalDistanceToMove, timeout, distance_tolerance);
    }



    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    // Run motors with encoders, specify the power, distance in CM (centimeter)
    public void run_Motors_encoder(double power, double leftDistance, double rightDistance, double timeout, double distance_tolerance) {
        int leftlastpos = motorLeft.getCurrentPosition();
        int rightlastpos = motorRight.getCurrentPosition();
        int newLeftTarget  = leftlastpos  + (int)(leftDistance * COUNTS_PER_CM);
        int newRightTarget = rightlastpos + (int)(rightDistance * COUNTS_PER_CM);
        int countTolerance = (int)(distance_tolerance * COUNTS_PER_CM);
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
        double newleftpower = power;
        double newrightpower = power;
        double left_remaining = 0;
        double right_remaining = 0; // remaining distances in encoder counts, normalized

        while ((chassis_runtime.seconds() < timeout) &&
                (is_motors_busy()) &&
                (Math.abs(motorLeft.getCurrentPosition()-newLeftTarget) > countTolerance ||
                        Math.abs(motorRight.getCurrentPosition()-newRightTarget) > countTolerance)) {
            //newleftpower = power;
            //newrightpower = power;

            if (leftlastpos != newLeftTarget) {     // avoid dividing by zero
                left_remaining = Math.abs((double) (motorLeft.getCurrentPosition() - newLeftTarget) / (double) (leftlastpos - newLeftTarget));
            }
            if (rightlastpos != newRightTarget) {   // avoid dividing by zero
                right_remaining = Math.abs((double) (motorRight.getCurrentPosition() - newRightTarget) / (double) (rightlastpos - newRightTarget));
            }
            if (left_remaining < right_remaining) { // adjust the power to move both motor consistently
                newleftpower  = power * left_remaining / right_remaining;
                newrightpower = power ;
            } else {
                newleftpower  = power ;
                newrightpower = power * right_remaining / left_remaining;
            }


            // Slowing down when it gets close
            if (Math.abs(motorLeft.getCurrentPosition()-newLeftTarget) < countTolerance*3) {  // close to stopping
                newleftpower *= Math.abs((double)(motorLeft.getCurrentPosition()-newLeftTarget)/(countTolerance*3.0));
                //newleftpower = MOTOR_STOP_POWER;
            }
            if (Math.abs(motorRight.getCurrentPosition()-newRightTarget) < countTolerance*3) { // close to stopping
                newrightpower *= Math.abs((double)(motorRight.getCurrentPosition()-newRightTarget)/(countTolerance*3.0));
                //newrightpower = MOTOR_STOP_POWER;
            }

            smooth_motors(newleftpower,newrightpower);
            motorLeft.setPower(lastleftpower);
            motorRight.setPower(lastrightpower);

        }
        motorLeft.setPower(0);
        motorRight.setPower(0);

        leftDistance_actual = (motorLeft.getCurrentPosition()-leftlastpos) / COUNTS_PER_CM;
        rightDistance_actual = (motorRight.getCurrentPosition()-rightlastpos) / COUNTS_PER_CM;
        // Turn off RUN_TO_POSITION
        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Stop all motion;

        lastleftpower = 0.0; lastrightpower = 0.0;
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    public double getLeftDistance_cm () { return leftDistance_actual; }
    public double getRightDistance_cm () { return rightDistance_actual; }



    // If either motor is busy
    private boolean is_motors_busy(){
        return motorLeft.isBusy() || motorRight.isBusy();
    }

    private void smooth_motors(double newleft, double newright) {
        lastleftpower = newleft * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastleftpower;
        lastrightpower = newright * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastrightpower;

    }


} // End of run_Motors_encoder_CM ====
