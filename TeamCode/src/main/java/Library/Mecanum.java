package Library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


public class Mecanum
{
    // chassis and wheel settings
    private static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    private static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    private static final double     WHEEL_DIAMETER_CM       = 9.15 ;     // For figuring circumference
    private static final double     COUNTS_PER_CM           = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_CM * Math.PI );
    private static final double     WHEELS_SPACING_CM       = 40.8;     // spacing between wheels for turns
    private static final double     SMOOTHING_COEFFICIENT   = 0.1;      // to smooth out power changes, smaller=smoother

    private static final float      YAW_PID_KP                = 0.03f;       // PID KP coefficient
    private static final float      YAW_PID_KI                = 0.00f;      // PID KI coefficient
    private float                   Yaw_Ki_sum                = 0.0f;        // PID KI integration
    private float                   Yaw_locked_angle;                       // angle to lock the robot orientation
    private float  max_speed                                  = 0.7f;

    private IMU IMU_Object = null;
    private float IMU_yaw_offset = 0;
    private ElapsedTime chassis_runtime = new ElapsedTime();

    private double lastleftFpower, lastrightFpower,lastleftRpower, lastrightRpower;
    //private double leftDistance_actual, rightDistance_actual;         // actual distance by encoders

    private DcMotor motorLF, motorRF, motorLR, motorRR;      // four motors on four corners

    public Mecanum(HardwareMap hardwareMap){    // constructor to create object
        motorLF = hardwareMap.dcMotor.get("LFDrive");
        motorLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //also works when encoder is not used
        motorRF = hardwareMap.dcMotor.get("RFDrive");
        motorRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //also works when encoder is not used
        motorLR = hardwareMap.dcMotor.get("LRDrive");
        motorLR.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //also works when encoder is not used
        motorRR = hardwareMap.dcMotor.get("RRDrive");
        motorRR.setMode(DcMotor.RunMode.RUN_USING_ENCODER); //also works when encoder is not used

        motorLF.setDirection(DcMotor.Direction.REVERSE);
        motorRF.setDirection(DcMotor.Direction.FORWARD);
        motorLR.setDirection(DcMotor.Direction.REVERSE);
        motorRR.setDirection(DcMotor.Direction.FORWARD);

        IMU_Object = new IMU(hardwareMap);
    }

    public void Start() {
        IMU_Object.start();
    }

    public float IMU_getAngle() {
        IMU_Object.measure();
        return (float)IMU_Object.yaw();
    }
    public float getRobotAngle() {
        return (float)IMU_Object.yaw() + IMU_yaw_offset;
    }

    // all inputs from -1 to 1, rotation as well
    public void run_Motors_no_encoder(float X_of_robot, float Y_of_robot, float rotation) {
        double LF = Y_of_robot + X_of_robot - rotation;
        double RF = Y_of_robot - X_of_robot + rotation;
        double LR = Y_of_robot - X_of_robot - rotation;
        double RR = Y_of_robot + X_of_robot + rotation;
        // normalized just in case magnitude greater than 1.0
        double normalized = Math.max(1.0,Math.max( Math.max(Math.abs(LF), Math.abs(LR)) , Math.max( Math.abs(RF), Math.abs(RR)) )) / max_speed;
        motorLF.setPower(LF / normalized);
        motorRF.setPower(RF / normalized);
        motorLR.setPower(LR / normalized);
        motorRR.setPower(RR / normalized);
    }

    public void setCurrentAngle(float setAngle) {  // set the robot orientation to a known angle
        IMU_yaw_offset = setAngle - IMU_getAngle();
    }

    public void set_angle_locked(float yaw_locked_angle) {    // start locking an orientation
        Yaw_locked_angle = yaw_locked_angle;
        Yaw_Ki_sum = 0.0f;           // reset the PID error sum
    }

    public float get_locked_angle () { return Yaw_locked_angle; }
    //public void set_current_angle_locked() {    // start locking the current orientation
    //    set_angle_locked(IMU_getAngle()+IMU_yaw_offset);
    //}

    public void run_Motor_angle_locked(float X_of_robot, float Y_of_robot ) { // move with locked orientation

        float angle_deviation = Yaw_locked_angle - getRobotAngle();
        // to avoid spinning more than 180 degree either direction for efficiency
        if (angle_deviation>180f) {
            angle_deviation -= 360f;
        } else if (angle_deviation < -180f) {
            angle_deviation += 360f;
        }
        Yaw_Ki_sum += angle_deviation * YAW_PID_KI;
        if (Yaw_Ki_sum > 1.0) {
            Yaw_Ki_sum = 1.0f;
        } else if (Yaw_Ki_sum < -1.0) {
            Yaw_Ki_sum = -1.0f;
        }
        float rotation = YAW_PID_KP * angle_deviation + Yaw_Ki_sum;

        if (Math.abs(rotation) > 1.0) {
            rotation = 1.0f * Math.signum(rotation);
        }
        run_Motors_no_encoder(X_of_robot, Y_of_robot, rotation);
    }

    // Drive the robot relative to the driver X-Y instead of the robot X-Y
    public void run_Motor_angle_locked_relative_to_driver(float X_of_Joystick, float Y_of_Joystick) {
        // angle difference between the joystick and the robot in radiant
        float mag = (float)Math.sqrt(X_of_Joystick*X_of_Joystick+Y_of_Joystick*Y_of_Joystick);
        //if ((Math.abs(X_of_Joystick)+Math.abs(Y_of_Joystick)) > 0.1) {
            double angle_diff = (Math.PI / 180.0) * (90.0+Math.toDegrees(Math.atan2(X_of_Joystick, Y_of_Joystick)) - getRobotAngle());
            float ref_X = mag * (float)Math.cos(angle_diff);
            float ref_Y = mag * (float)Math.sin(angle_diff);
            run_Motor_angle_locked(ref_X, ref_Y);
        //} else {
        //    run_Motor_angle_locked(0.0f, 0.0f);
        //}
    }

    public void spin_Motors_no_encoder(float power) {  // -1 to 1 positive for counter clockwise
        run_Motors_no_encoder(0,0, power);
    }

    // spin, positive is counter clockwise
    public void spin_encoder_degree(double power, double turnangle, double timeout, double distance_tolerance) {
        double totalDistanceToMove = Math.PI*WHEELS_SPACING_CM * turnangle / 360.0;// get total distance to move in centimeters
        run_Motors_encoder(power , -totalDistanceToMove,totalDistanceToMove,-totalDistanceToMove,totalDistanceToMove, timeout, distance_tolerance);
    }


    // Run motors with encoders, specify the power, distance in CM (centimeter)
    public void run_Motors_encoder(double power, double leftFDistance, double rightFDistance, double leftRDistance, double rightRDistance,double timeout, double distance_tolerance) {
        int leftFlastpos = motorLF.getCurrentPosition();
        int rightFlastpos = motorRF.getCurrentPosition();
        int leftRlastpos = motorLR.getCurrentPosition();
        int rightRlastpos = motorRR.getCurrentPosition();
        int newLeftFTarget  = leftFlastpos  + (int)(leftFDistance * COUNTS_PER_CM);
        int newRightFTarget = rightFlastpos + (int)(rightFDistance * COUNTS_PER_CM);
        int newLeftRTarget  = leftRlastpos  + (int)(leftRDistance * COUNTS_PER_CM);
        int newRightRTarget = rightRlastpos + (int)(rightRDistance * COUNTS_PER_CM);

        int countTolerance = (int)(distance_tolerance * COUNTS_PER_CM);
        motorLF.setTargetPosition(newLeftFTarget);
        motorRF.setTargetPosition(newRightFTarget);
        motorLR.setTargetPosition(newLeftRTarget);
        motorRR.setTargetPosition(newRightRTarget);

        // Turn On RUN_TO_POSITION
        motorLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorLR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorRR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        chassis_runtime.reset();

        // set the power to start the motors
        //motorLeft.setPower(power);
        //motorRight.setPower(power);


        // keep looping while we are still active, and there is time left, and both motors are running.
        double newleftFpower = power;
        double newrightFpower = power;
        double newleftRpower = power;
        double newrightRpower = power;
        double leftF_remaining = 0;
        double rightF_remaining = 0; // remaining distances in encoder counts, normalized
        double leftR_remaining = 0;
        double rightR_remaining = 0; // remaining distances in encoder counts, normalized

        while ((chassis_runtime.seconds() < timeout) &&
                (is_motors_busy()) &&
                (Math.abs(motorLF.getCurrentPosition()-newLeftFTarget) > countTolerance ||
                        Math.abs(motorRF.getCurrentPosition()-newRightFTarget) > countTolerance ||
                        Math.abs(motorLR.getCurrentPosition()-newLeftRTarget) > countTolerance ||
                        Math.abs(motorRR.getCurrentPosition()-newRightRTarget) > countTolerance)) {

            if (leftFlastpos != newLeftFTarget) {     // avoid dividing by zero
                leftF_remaining = Math.abs((double) (motorLF.getCurrentPosition() - newLeftFTarget) / (double) (leftFlastpos - newLeftFTarget));
            }
            if (rightFlastpos != newRightFTarget) {   // avoid dividing by zero
                rightF_remaining = Math.abs((double) (motorRF.getCurrentPosition() - newRightFTarget) / (double) (rightFlastpos - newRightFTarget));
            }
            if (leftRlastpos != newLeftRTarget) {     // avoid dividing by zero
                leftR_remaining = Math.abs((double) (motorLR.getCurrentPosition() - newLeftRTarget) / (double) (leftRlastpos - newLeftRTarget));
            }
            if (rightRlastpos != newRightRTarget) {   // avoid dividing by zero
                rightR_remaining = Math.abs((double) (motorRR.getCurrentPosition() - newRightRTarget) / (double) (rightRlastpos - newRightRTarget));
            }
            if (leftF_remaining < rightF_remaining) { // adjust the power to move both motor consistently
                newleftFpower  = power * leftF_remaining / rightF_remaining;
                newrightFpower = power ;
            } else {
                newleftFpower  = power ;
                newrightFpower = power * rightF_remaining / leftF_remaining;
            }
            if (leftR_remaining < rightR_remaining) { // adjust the power to move both motor consistently
                newleftRpower  = power * leftR_remaining / rightR_remaining;
                newrightRpower = power ;
            } else {
                newleftRpower  = power ;
                newrightRpower = power * rightR_remaining / leftR_remaining;
            }


            // Slowing down when it gets close
            if (Math.abs(motorLF.getCurrentPosition()-newLeftFTarget) < countTolerance*3) {  // close to stopping
                newleftFpower *= Math.abs((double)(motorLF.getCurrentPosition()-newLeftFTarget)/(countTolerance*3.0));
                //newleftpower = MOTOR_STOP_POWER;
            }
            if (Math.abs(motorRF.getCurrentPosition()-newRightFTarget) < countTolerance*3) { // close to stopping
                newrightFpower *= Math.abs((double)(motorRF.getCurrentPosition()-newRightFTarget)/(countTolerance*3.0));
                //newrightpower = MOTOR_STOP_POWER;
            }
            if (Math.abs(motorLR.getCurrentPosition()-newLeftRTarget) < countTolerance*3) {  // close to stopping
                newleftRpower *= Math.abs((double)(motorLR.getCurrentPosition()-newLeftRTarget)/(countTolerance*3.0));
                //newleftpower = MOTOR_STOP_POWER;
            }
            if (Math.abs(motorRR.getCurrentPosition()-newRightRTarget) < countTolerance*3) { // close to stopping
                newrightRpower *= Math.abs((double)(motorRR.getCurrentPosition()-newRightRTarget)/(countTolerance*3.0));
                //newrightpower = MOTOR_STOP_POWER;
            }

            smooth_motors(newleftFpower,newrightFpower, newleftRpower, newrightRpower);
            motorLF.setPower(lastleftFpower);
            motorRF.setPower(lastrightFpower);
            motorLR.setPower(lastleftRpower);
            motorRR.setPower(lastrightRpower);

        }
        motorLF.setPower(0);
        motorRF.setPower(0);
        motorLR.setPower(0);
        motorRR.setPower(0);

        //leftDistance_actual = (motorLeft.getCurrentPosition()-leftlastpos) / COUNTS_PER_CM;
        //rightDistance_actual = (motorRight.getCurrentPosition()-rightlastpos) / COUNTS_PER_CM;
        // Turn off RUN_TO_POSITION
        motorLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Stop all motion;

        lastleftFpower = 0.0; lastrightFpower = 0.0; lastleftRpower = 0.0; lastrightRpower = 0.0;
        motorLF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRF.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    // end if encoder


    // If either motor is busy
    private boolean is_motors_busy(){
        return motorLF.isBusy() || motorRF.isBusy() || motorLR.isBusy()|| motorRR.isBusy();
    }

    private void smooth_motors(double newleftF, double newrightF, double newleftR, double newrightR) {
        lastleftFpower = newleftF * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastleftFpower;
        lastrightFpower = newrightF * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastrightFpower;
        lastleftRpower = newleftR * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastleftRpower;
        lastrightRpower = newrightR * SMOOTHING_COEFFICIENT + (1.0-SMOOTHING_COEFFICIENT) * lastrightRpower;
    }


} // End of run_Motors_encoder_CM ====
