package Library;

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
    private double leftDistance, rightDistance;

    public Chassis_motors(HardwareMap hardwareMap){    // constructor to create object
        motorLeft = hardwareMap.dcMotor.get("LeftDrive");
        motorRight = hardwareMap.dcMotor.get("RightDrive");
        set_Direction_Forward();                        // forward by default
        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //also works when encoder is not used
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//also works when encoder is not used
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

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

    public void turn_encoder_degree(double power, double turnangle, double timeout, double distance_tolerance) {
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
        while ((chassis_runtime.seconds() < timeout) &&
                (is_motors_busy()) &&
                (Math.abs(motorLeft.getCurrentPosition()-newLeftTarget) > countTolerance ||
                        Math.abs(motorRight.getCurrentPosition()-newRightTarget) > countTolerance)) {
            newleftpower = power;
            newrightpower = power;
            if (Math.abs(motorLeft.getCurrentPosition()-newLeftTarget) < 1000) {
                //newleftpower = power* (Math.abs(motorLeft.getCurrentPosition()-newLeftTarget)/2000.0);
                newleftpower = MOTOR_STOP_POWER;
            }
            if (Math.abs(motorRight.getCurrentPosition()-newRightTarget) < 1000) {
                //newrightpower = power*(Math.abs(motorRight.getCurrentPosition()-newRightTarget)/2000.0);
                newrightpower = MOTOR_STOP_POWER;
            }
            //newleftpower = Math.abs(0.5 * (motorLeft.getCurrentPosition()-newLeftTarget)/(newLeftTarget-leftlastpos));
            //newrightpower = Math.abs(0.5 * (motorRight.getCurrentPosition()-newRightTarget)/(newRightTarget-rightlastpos));
            smooth_motors(newleftpower,newrightpower);
            motorLeft.setPower(lastleftpower);
            motorRight.setPower(lastrightpower);

        }

        /*
        while ((chassis_runtime.seconds() < timeout) &&
                (is_motors_busy())) {
            motorLeft.setPower(power);
            motorRight.setPower(power);

        }
*/
        leftDistance = (motorLeft.getCurrentPosition()-leftlastpos) / COUNTS_PER_CM;
        rightDistance = (motorRight.getCurrentPosition()-rightlastpos) / COUNTS_PER_CM;
        // Turn off RUN_TO_POSITION
        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Stop all motion;
        //motorLeft.setPower(0);
        //motorRight.setPower(0);
        lastleftpower = 0.0; lastrightpower = 0.0;
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    public double getLeftDistance_cm () { return leftDistance; }
    public double getRightDistance_cm () { return rightDistance; }



    // If either motor is busy
    private boolean is_motors_busy(){
        return motorLeft.isBusy() || motorRight.isBusy();
    }

    private void smooth_motors(double newleft, double newright) {
        lastleftpower = newleft * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastleftpower;
        lastrightpower = newright * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastrightpower;

    }


} // End of run_Motors_encoder_CM ====
