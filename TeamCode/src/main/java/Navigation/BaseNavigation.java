package Navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import Library.JewelServo;
import Library.Mecanum;
import Library.REVColorDistance;
import Library.RangeSensor;
import Library.VuforiaNavigation;

public abstract class BaseNavigation extends LinearOpMode {

    private final float Wall_PID_KP        =  0.2f;       // PID for KP to keep distance to the wall.

    private JewelServo JewelFlicker = null;
    //private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;
    private RangeSensor rangesensors = null;
    private VuforiaNavigation vuforia = null;
    private ElapsedTime basenavigateion_runtime = new ElapsedTime();
    double pid_x_sum      = 0.0;
    double pid_y_sum      = 0.0;

    @Override
    public void runOpMode() {

        JewelFlicker  = new JewelServo(hardwareMap);
        //Colordistance = new REVColorDistance(hardwareMap);
        mecanumDrive  = new Mecanum(hardwareMap);
        rangesensors  = new RangeSensor(hardwareMap);
        vuforia       = new VuforiaNavigation(true);


        JewelFlicker.LeftFlick();       // so it doesn't go over 18"
        JewelFlicker.RaiseBeam();

        waitForStart();

        mecanumDrive.Start();
        mecanumDrive.set_max_power(0.6);  // max speed/power to be modified later if needed

        //JewelFlicker.runFlickJewel();     // flick a jewel

        navigate();

        while(opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
        {
            idle();
        }
    }

    protected abstract void navigate();


    // Flicker
    public void runFlicker(boolean isRedAlliance) {
        JewelFlicker.runFlickJewel(isRedAlliance);
    }

    // Getting robot off Balancing Stone =============================================
    public void getOutofBalancingStone(boolean isRedAlliance) {

        double time_to_get_off = 3.0;

        basenavigateion_runtime.reset();
        mecanumDrive.setCurrentAngle(90.0);
        mecanumDrive.set_angle_locked(90.0);

        double robot_y_dir = -1.0;
        if (isRedAlliance) {
            robot_y_dir = 1.0;
        }
        while(basenavigateion_runtime.seconds() < time_to_get_off) {
            mecanumDrive.run_Motor_angle_locked(0.0, robot_y_dir); // move to the right
            idle();
        }
        mecanumDrive.run_Motor_angle_locked(0.0, 0.0);  // stop

        vuforia.activate();         // start vuforia
    }
    // ====================================================================

    public void goTo_Red_left_Crytobox() {
        vuforia_goto_Cryptobox(0.0, true,true);
    }
    public void goTo_Red_right_Crytobox() {
        vuforia_goto_Cryptobox(-90.0, true,false);
    }

    public void goTo_Blue_left_Crytobox() {
        vuforia_goto_Cryptobox(90.0, false,true);
    }
    public void goTo_Blue_right_Crytobox() {
        vuforia_goto_Cryptobox(0.0, false,false);
    }

    // ====================================================================
    private void vuforia_goto_Cryptobox(double firstAngle, boolean isRedAlliance, boolean isLeftSide) {
        double initial_cryto_spacing = 4.5 * vuforia.mmPerInch;
        double vuforia_tolerance     = 20.0;      // in mm for diff^2 to the target location
        double pid_kp                = 0.005;
        double pid_ki                = 0.00001;

        int cryto_column = 0;
        double phone_x, phone_y;    // phone location on the robot
        double target_x, target_y;  // target location from phone perspective but robot axis
        double power_x, power_y;

        mecanumDrive.set_angle_locked(firstAngle);

        while (!vuforia.updateRobotLocation() && opModeIsActive()) { // wait until Vuforia is ready
            idle();
        }
        cryto_column = vuforia.getCrytoboxColumn();     // 0=left, 1=center, 2=right
        double column_offset = (cryto_column - 1.0) * 7.63 * vuforia.mmPerInch; // offset for column

        if(isRedAlliance) {
            if(isLeftSide) {
                phone_x =  8.0 * vuforia.mmPerInch; // front right side
                phone_y =  0.0  * vuforia.mmPerInch;
                target_x = -vuforia.Crytobox_red_left_mm + phone_x + column_offset;
                target_y = initial_cryto_spacing;
            } else {
                phone_x = 16.0 * vuforia.mmPerInch;  // rear right side
                phone_y = -8.0 * vuforia.mmPerInch;
                target_x = -vuforia.Crytobox_red_right_x_mm + phone_x + initial_cryto_spacing;
                target_y =  vuforia.Crytobox_red_right_y_mm + phone_y - column_offset;
            }
        } else {        // Blue alliance
            if(isLeftSide) {
                phone_x = -16.0 * vuforia.mmPerInch;   // rear left side
                phone_y = -8.0  * vuforia.mmPerInch;
                target_x =  vuforia.Crytobox_blue_left_x_mm + phone_x - initial_cryto_spacing;
                target_y =  vuforia.Crytobox_red_right_y_mm + phone_y + column_offset;
            } else {
                phone_x = -8.0 * vuforia.mmPerInch; // middle of left side
                phone_y =  8.0  * vuforia.mmPerInch;
                target_x = vuforia.Crytobox_blue_right_mm + phone_x + column_offset;
                target_y = phone_y + initial_cryto_spacing;
            }
        }

        Double x_diff = vuforia.getX() - target_x;
        Double y_diff = vuforia.getY() - target_y;

        while ( (x_diff*x_diff + y_diff*y_diff) > vuforia_tolerance && opModeIsActive()) {
            if(vuforia.isTarget_visible()) {
                if( isRedAlliance ) {
                    if (isLeftSide ) {
                        x_diff = target_x - vuforia.getX();
                        y_diff = vuforia.getY() - target_y;
                    } else {
                        x_diff = vuforia.getY() - target_y;
                        y_diff = vuforia.getX() - target_x;
                    }
                } else  {
                    if (isLeftSide) {
                        x_diff = target_y - vuforia.getY();
                        y_diff = target_x - vuforia.getX();
                    } else {
                        x_diff = target_x - vuforia.getX();
                        y_diff = vuforia.getY() - target_y;
                    }
                }

                pid_x_sum += x_diff * pid_ki;
                pid_y_sum += y_diff * pid_ki;
                if (Math.abs(pid_x_sum) > 0.5) pid_x_sum = 0.5 * Math.signum(pid_x_sum);
                if (Math.abs(pid_y_sum) > 0.5) pid_y_sum = 0.5 * Math.signum(pid_y_sum);

                power_x = pid_kp * x_diff + pid_x_sum;
                power_y = pid_kp * y_diff + pid_y_sum;
                if (Math.abs(power_x) > 1.0) power_x = 1.0 * Math.signum(power_x);
                if (Math.abs(power_y) > 1.0) power_y = 1.0 * Math.signum(power_y);
                mecanumDrive.run_Motor_angle_locked(x_diff, y_diff);

                vuforia.updateRobotLocation();
            }
            idle();
        }
        mecanumDrive.run_Motor_angle_locked(0.0, 0.0);  // stop
    }

    // ====================================================================
    // Move along a wall, negative power to move to the left
    private double moveAlongWall_untilCrytobox(double wall_angle, double distanceToWall_cm, double minDistance, double power) {
        double dist = rangesensors.getDistance_1_cm(200);

        // run until it detects crytobox wall
        mecanumDrive.set_angle_locked(wall_angle);

        basenavigateion_runtime.reset();
        while (dist > minDistance && basenavigateion_runtime.seconds() < 10) {
            mecanumDrive.run_Motor_angle_locked(power, Wall_PID_KP * (dist - distanceToWall_cm));
            dist = rangesensors.getDistance_1_cm(200);
        }
        return dist;
    }
    // ====================================================================

}
