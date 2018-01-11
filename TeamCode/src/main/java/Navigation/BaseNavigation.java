package Navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import Library.Glypher;
import Library.JewelServo;
import Library.MRrangeSensor;
import Library.Mecanum;
import Library.REVColorDistance;
import Library.VuforiaNavigation;

public abstract class BaseNavigation extends LinearOpMode {

    final double Initial_orientation = -90.0;   // initial robot orientatian respect to Jewels

    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;
    private Glypher GlypherObject = null;
    private VuforiaNavigation vuforiaObject = null;
    private MRrangeSensor Range_sensors = null;

    boolean isRedAlliance, isLeftSide;
    int     flickDirection = 0;
    ElapsedTime basenavigation_elapsetime = new ElapsedTime();
    double  cryto_offset_inc = 0.0;

    @Override
    public void runOpMode() {

        JewelFlicker = new JewelServo(hardwareMap);
        Colordistance = new REVColorDistance(hardwareMap);
        mecanumDrive = new Mecanum(hardwareMap);
        GlypherObject = new Glypher(hardwareMap);
        vuforiaObject = new VuforiaNavigation(true , false);  // true=extended Tracking of a target picture
        Range_sensors = new MRrangeSensor(hardwareMap);

        waitForStart();

        mecanumDrive.Start();

        navigate();

        while (opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
        {
            idle();
        }
    }

    protected abstract void navigate();



    // =========================== Initial Robot placement ==================
    public void robotInitial(boolean isRed, boolean isLeft) {
        isRedAlliance = isRed;
        isLeftSide = isLeft;
        mecanumDrive.setCurrentAngle(Initial_orientation);
        mecanumDrive.set_angle_locked(Initial_orientation);     // to the right of Jewel
        mecanumDrive.set_Angle_tolerance(3.0);

        vuforiaObject.activate();
    }


    // ====================================================================
    // Jewel Flicker method
    protected void flickJewel() {

        double turn_power = 0.2;
        double timeoutsec = 2.5;
        double turn_angle = 15.0;

        JewelFlicker.Initial();

        vuforiaObject.updateRobotLocation();        // scan the picture

        flickDirection = JewelFlicker.flickJewel(isRedAlliance); // 0=color not detected, 1=left,-1=right
        //telemetry.addData("Direction :", flickDirection);
        //telemetry.addData("Red value : ", JewelFlicker.readRed);
        //telemetry.addData("Blue value: ", JewelFlicker.readBlue);
        //telemetry.update();

        cryto_offset_inc = vuforiaObject.crytobox_offset_inch;      // get cryto offset

        if ( Math.abs(flickDirection) > 0) {
            mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, turn_power, flickDirection * turn_angle + Initial_orientation);
        }

        JewelFlicker.Initial();

    }



    // ===========================Get of the Balancing Stone =========================================
    protected void get_off_Balancing_Stone(double powerset, double time_to_go, double right_offset) {

        telemetry.addData("Jewel direction: ", flickDirection );

        if (isRedAlliance) {  // move backward
            mecanumDrive.run_Motor_angle_locked_with_Timer( right_offset - flickDirection * Math.sin(Math.toRadians(15.0)), -Math.cos(Math.toRadians(15.0)), time_to_go, powerset);
        } else {                // go forward
            mecanumDrive.run_Motor_angle_locked_with_Timer(right_offset + flickDirection * Math.sin(Math.toRadians(15.0)), Math.cos(Math.toRadians(15.0)), time_to_go, powerset);
        }
        telemetry.update();

        vuforiaObject.deactivate();
    }


    // ================= Robot spin =========================
    protected void Spin_locked_angle(double angle_lock, double timeoutsec) {
        double testpower  = .05;

        mecanumDrive.set_Angle_tolerance(5.0);
        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, angle_lock);
    }



    // ================ spin and find the picture =========================
    protected boolean vuforia_find_picture () {

        double timeoutsec = 5.0;
        double testpower  = 0.4;

        mecanumDrive.set_Angle_tolerance(5.0);
        basenavigation_elapsetime.reset();

        int spin_count = 0;
        while (spin_count < 90 && opModeIsActive() && !vuforiaObject.isTarget_visible() && basenavigation_elapsetime.seconds() < timeoutsec) {
            mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, mecanumDrive.get_locked_angle() - 1.0);
            spin_count += 1;
            //sleep(100);
            idle();
        }

        if (vuforiaObject.isTarget_visible()) {
            telemetry.addData("Picture:" , "Found");
        } else {
            telemetry.addData("Picture:", "NOT found");
        }
        telemetry.update();

        return vuforiaObject.isTarget_visible();
    }

    // =========================== Move to X-Y location =====================

    protected void vuforia_Move_XY_inch_point_picture(double Xloc, double Yloc, double timeout) {

        double maxpower = 0.1;
        double PID_kp = 0.02;
        double PID_ki = 0.00001;

        basenavigation_elapsetime.reset();

        double distanceX, distanceY, total_distance;
        double cryto_offset_x = 0.0;
        double cryto_offset_y = 0.0;
        double ki_sum_power = 0.0;

        while (basenavigation_elapsetime.seconds() < timeout && opModeIsActive()) {
            if(vuforiaObject.isTarget_visible()) {

                // point toward the picture
                mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()+vuforiaObject.getAngleTowardPicture());

                if (vuforiaObject.updateRobotLocation()) {
                    telemetry.addData("Location Update:", "Yes");
                } else {
                    telemetry.addData("Location Update:", "No");
                }

                if (isRedAlliance) {
                    if(isLeftSide) {
                        cryto_offset_x = vuforiaObject.crytobox_offset_inch;
                    } else {
                        cryto_offset_y = -vuforiaObject.crytobox_offset_inch;
                    }
                } else {
                    if(isLeftSide) {
                        cryto_offset_y = vuforiaObject.crytobox_offset_inch;
                    } else {
                        cryto_offset_x = vuforiaObject.crytobox_offset_inch;
                    }
                }

                Xloc += cryto_offset_x;
                Yloc += cryto_offset_y;

                distanceX = Xloc - vuforiaObject.getXcoordinate_mm()/25.4;
                distanceY = vuforiaObject.getYcoordinate_mm()/25.4 - Yloc;
                total_distance = Math.hypot(distanceX,distanceY);

                ki_sum_power += total_distance * PID_ki;
                double total_power = total_distance * PID_kp + ki_sum_power;

                mecanumDrive.set_max_power(Math.min(total_power, maxpower));

                // mecanumDrive.run_Motor_angle_locked(distanceX / total_distance,distanceY / total_distance);

                mecanumDrive.run_Motor_angle_locked_relative_to_driver(distanceX / total_distance,distanceY / total_distance);

                telemetry.addData("X distance to correct cryto column: ", distanceX);
                telemetry.addData("Y distance to correct cryto column: ", distanceY);
                telemetry.addData("Crytobox column offset: ", vuforiaObject.crytobox_offset_inch);
            } else {
                telemetry.addData("Vuforia", "NOT Visible");
                mecanumDrive.stop_Motor_with_locked();
                // do non vuforia autonomous
            }

            telemetry.update();

            idle();
        }
    }


    // ============================ Deposit glyph ===================================

    public void Glyph_Deposit() {
        // kick glyph out

        mecanumDrive.run_Motor_angle_locked_with_Timer(0, 1, 2, 0.05); // move forward

        basenavigation_elapsetime.reset();
        while (basenavigation_elapsetime.seconds() < 0.5) {
            GlypherObject.GrabberSetPower(0.5); // open the grabber
            idle();
        }
        GlypherObject.GrabberSetPower(0.0);

        mecanumDrive.run_Motor_angle_locked_with_Timer(0.1, -1, 0.5, 0.1); // move back a little


        // hit again if necessary
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, 1, 1.5, 0.05); // move forward
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 0.5, 0.1); // move back a little

        mecanumDrive.stop_Motor_with_locked();

    }

    // =========================== Move to X-Y location using Ultra Sonic sensors =====================

    protected void Move_to_Distance_inch(double leftDistance, double rightDistance, double timeout) {

        // Tweaking parameters
        double maxpower = 0.1;
        double distance_tolerance = 1.0;
        double PID_kp = 0.02;
        double PID_ki = 0.00001;
        double PID_kd = 0.0;

        basenavigation_elapsetime.reset();
        double Xnew_distance;
        double old_distance=20.0;
        double new_distance=20.0;
        double Xerror = 0.0;
        double Yerror = 0.0;

        while (basenavigation_elapsetime.seconds() < timeout && opModeIsActive() && Math.abs(new_distance) > distance_tolerance ) {

            if (leftDistance > 0.0) {
                if (Range_sensors.isLeftAvailable(leftDistance-20, leftDistance+20)) {
                    Xnew_distance = Range_sensors.Distance_left;
                    Xerror = leftDistance - Xnew_distance + cryto_offset_inc;
                }
            } else {
                if (Range_sensors.isRightAvailable(rightDistance-20, rightDistance+20)) {
                    Xnew_distance = Range_sensors.Distance_right;
                    Xerror = Xnew_distance - rightDistance + cryto_offset_inc;
                }
            }
            mecanumDrive.set_max_power(0.1);

            mecanumDrive.run_Motor_angle_locked(Xerror * 0.1 ,0);

            idle();
        }
        mecanumDrive.stop_Motor_with_locked();
    }

}
