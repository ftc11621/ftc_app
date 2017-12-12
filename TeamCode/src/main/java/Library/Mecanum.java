package Library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;


public class Mecanum
{
    // chassis and wheel settings
    //private static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    //private static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    //private static final double     WHEEL_DIAMETER_CM       = 9.15 ;     // For figuring circumference
    //private static final double     COUNTS_PER_CM           = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
    //        (WHEEL_DIAMETER_CM * Math.PI );
    //private static final double     WHEELS_SPACING_CM       = 40.8;     // spacing between wheels for turns
    private static final double     SMOOTHING_COEFFICIENT   = 0.1;      // to smooth out power changes, smaller=smoother

    private double     YAW_PID_KP                = 0.01;      // 0.01, 0.007;       // PID KP coefficient
    private double     YAW_PID_KI                = 0.000;     // 0.0002 PID KI coefficient
    private double     YAW_PID_KD                = 0.01;      // 0.01, 0.001;       // PID KD coefficient
    private double                  Yaw_Ki_sum                = 0.0;        // PID KI integration
    private double                  Yaw_locked_angle;                       // angle to lock the robot orientation
    private double  max_speed                                 = 0.1;
    private double  angle_tolerance                           = 5;

    private IMU IMU_Object = null;
    private double IMU_yaw_offset = 0;

    private ElapsedTime chassis_runtime = new ElapsedTime();

    private double lastleftFpower, lastrightFpower,lastleftRpower, lastrightRpower;

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

        //motorLF.setDirection(DcMotor.Direction.REVERSE);
        //motorRF.setDirection(DcMotor.Direction.FORWARD);
        //motorLR.setDirection(DcMotor.Direction.REVERSE);
        //motorRR.setDirection(DcMotor.Direction.FORWARD);
        // Neverest is the opposite
        motorLF.setDirection(DcMotor.Direction.FORWARD);
        motorRF.setDirection(DcMotor.Direction.REVERSE);
        motorLR.setDirection(DcMotor.Direction.FORWARD);
        motorRR.setDirection(DcMotor.Direction.REVERSE);

        IMU_Object = new IMU(hardwareMap);
    }

    public void Start() {
        IMU_Object.start();
    }

    public void set_max_power (double max_power) { // max speed from 0 to 1
        max_speed = max_power;
    }

    //public double IMU_getAngle() {
    //    IMU_Object.measure();
    //    return IMU_Object.yaw();
    //}

    // --------------------------------------------------------------------
    public double getRobotAngle() {
        return setAngleInRange(IMU_Object.yaw() + IMU_yaw_offset);
    }

    // ----------------------------------------------------------------------
    // all inputs from -1 to 1, rotation as well
    public void run_Motors_no_encoder(double X_of_robot, double Y_of_robot, double rotation) {
        double LF = Y_of_robot + X_of_robot - rotation;
        double RF = Y_of_robot - X_of_robot + rotation;
        double LR = Y_of_robot - X_of_robot - rotation;
        double RR = Y_of_robot + X_of_robot + rotation;
        // normalized just in case magnitude greater than 1.0
        double normalized = Math.max(1.0,Math.max( Math.max(Math.abs(LF), Math.abs(LR)) , Math.max( Math.abs(RF), Math.abs(RR)) ));
        motorLF.setPower(max_speed * LF / normalized);
        motorRF.setPower(max_speed * RF / normalized);
        motorLR.setPower(max_speed * LR / normalized);
        motorRR.setPower(max_speed * RR / normalized);
    }

    // ----------------------------------------------------
    public void setCurrentAngle(double setAngle) {  // set the robot orientation to a known angle
        IMU_Object.measure();
        IMU_yaw_offset = setAngleInRange( setAngle - IMU_Object.yaw());
    }

    // ------------------------------------------------------
    public void set_angle_locked(double yaw_locked_angle) {    // start locking an orientation
        Yaw_locked_angle = yaw_locked_angle;
        Yaw_Ki_sum = 0.0f;           // reset the PID error sum
    }

    // --------------------------------------------------------
    public double get_locked_angle () { return Yaw_locked_angle; }
    //public void set_current_angle_locked() {    // start locking the current orientation
    //    set_angle_locked(IMU_getAngle()+IMU_yaw_offset);
    //}

    public float get_angular_velocity() {
        return IMU_Object.angular_velocity;
    }

    // --------------------------------------------------------
    public void run_Motor_angle_locked(double X_of_robot, double Y_of_robot ) { // move with locked orientation

        double rotation = 0.0;

        if (IMU_Object.measure() ) {   // read angle if it's not too fast

            double angle_deviation = setAngleInRange(Yaw_locked_angle - getRobotAngle());

            Yaw_Ki_sum += angle_deviation * YAW_PID_KI;
            if (Yaw_Ki_sum > 0.2) {
                Yaw_Ki_sum = 0.2f;
            } else if (Yaw_Ki_sum < -0.2) {
                Yaw_Ki_sum = -0.2f;
            }
            rotation = YAW_PID_KP * angle_deviation + Yaw_Ki_sum;

            double damper_rotation = -1.0 * YAW_PID_KD * IMU_Object.angular_velocity; // damping rotation

            if ((damper_rotation / rotation) < -1.5) { // too much damping, may oscillate
                rotation = 0.0;
            } else if ((damper_rotation / rotation) < 0.0) { // speed in direction it's supposed to go
                rotation += damper_rotation;
            }

            if (Math.abs(rotation) > 1.0) {   // prevent too high rotational value
                rotation = 1.0 * Math.signum(rotation);
            }
        }
        run_Motors_no_encoder(X_of_robot, Y_of_robot, rotation);
    }

    // -----------------------------------------------------------------
    // Drive the robot relative to the driver X-Y instead of the robot X-Y
    public void run_Motor_angle_locked_relative_to_driver(double X_of_Joystick, double Y_of_Joystick) {

        //IMU_Object.measure();

        // angle difference between the joystick and the robot in radiant
        double mag = Math.sqrt(X_of_Joystick*X_of_Joystick+Y_of_Joystick*Y_of_Joystick);

        double angle_diff = (Math.PI / 180.0) * (90.0+Math.toDegrees(Math.atan2(X_of_Joystick, Y_of_Joystick)) - getRobotAngle());
        double ref_X = mag * Math.cos(angle_diff);
        double ref_Y = mag * Math.sin(angle_diff);
        run_Motor_angle_locked(ref_X, ref_Y);
    }

    // Drive the robot relative to the driver X-Y instead of the robot X-Y
    public void run_Motor_relative_to_driver(double X_of_Joystick, double Y_of_Joystick) {

        //IMU_Object.measure();
        // angle difference between the joystick and the robot in radiant
        double mag = Math.sqrt(X_of_Joystick*X_of_Joystick+Y_of_Joystick*Y_of_Joystick);

        double angle_diff = (Math.PI / 180.0) * (90.0+Math.toDegrees(Math.atan2(X_of_Joystick, Y_of_Joystick)) - getRobotAngle());
        double ref_X = mag * Math.cos(angle_diff);
        double ref_Y = mag * Math.sin(angle_diff);

        double angle_deviation = setAngleInRange(Yaw_locked_angle - getRobotAngle());

        if (Math.abs(angle_deviation) < angle_tolerance) {     // if less than 10 degree
            IMU_Object.measure();
            run_Motors_no_encoder(ref_X, ref_Y, 0.0);
        } else {
            run_Motor_angle_locked(ref_X, ref_Y);
        }
    }

    // =====================================
    public void set_Angle_tolerance (double new_tol_angle) {
        angle_tolerance = new_tol_angle;
    }

    // Run with timer : ==========================
    public void run_Motor_angle_locked_with_Timer(double X_of_robot, double Y_of_robot, double time_sec, double power) {

        chassis_runtime.reset();
        Yaw_Ki_sum = 0.0;       // reset the PID KI sum
        max_speed = power;
        while(chassis_runtime.seconds() < time_sec) {
            //IMU_Object.measure();   // read angle
            run_Motor_angle_locked(X_of_robot,Y_of_robot);
        }
        //IMU_Object.measure();   // read angle
        stop_Motor_with_locked();
        Yaw_Ki_sum = 0.0;       // reset the PID KI sum
    }

    // =========================================
    // Spin only to a locked angle with timer :
    public void spin_Motor_angle_locked_with_Timer(double time_sec, double power, double locked_angle) {

        set_angle_locked(locked_angle); //Initial_orientation + 15.0);

        chassis_runtime.reset();
        Yaw_Ki_sum = 0.0;       // reset the PID KI sum
        max_speed = power;
        while(chassis_runtime.seconds() < time_sec && Math.abs(setAngleInRange(Yaw_locked_angle - getRobotAngle())) > angle_tolerance) {
            //IMU_Object.measure();   // read angle
            run_Motor_angle_locked(0.0, 0.0);
        }
        stop_Motor_with_locked();
        Yaw_Ki_sum = 0.0;       // reset the PID KI sum
    }

    // =============================================
    public void stop_Motor_with_locked() {
        set_max_power(0.0);
        motorLF.setPower(0.0);
        motorRF.setPower(0.0);
        motorLR.setPower(0.0);
        motorRR.setPower(0.0);
    }

    // ================== get encoder values ==========
    public int get_Encoder_value(int nmotor) {
        switch (nmotor) {
            case 0:
                return motorLF.getCurrentPosition();
            case 1:
                return motorRF.getCurrentPosition();
            case 2:
                return motorLR.getCurrentPosition();
            case 3:
                return motorRR.getCurrentPosition();
            default:
                return 0;
        }
    }

    // --------------- Set angle within -180 to 180 ---------------------------
    private double setAngleInRange(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    // -------------------------------------------------------------------------
    public void spin_Motors_no_encoder(float power) {  // -1 to 1 positive for counter clockwise
        run_Motors_no_encoder(0,0, power);
    }

} // End of run_Motors_encoder_CM ====