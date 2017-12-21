package Navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import Library.Glypher;
import Library.JewelServo;
import Library.Mecanum;
import Library.REVColorDistance;
import Library.RangeSensor;
import Library.VuforiaNavigation;

public abstract class BaseNavigation extends LinearOpMode {

    final double Initial_orientation = -90.0;   // initial robot orientatian respect to Jewels

    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;
    private Glypher GlypherObject = null;
    private VuforiaNavigation vuforiaObject = null;
    private RangeSensor Range_sensors = null;

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
        Range_sensors = new RangeSensor(hardwareMap);

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
    protected void get_off_Balancing_Stone() {

        double powerset = 0.2;
        double timeoutset = 1.5;
        double x_offset = 0.1;

        telemetry.addData("Jewel direction: ", flickDirection );

        if (isRedAlliance) {  // move forward
            Range_sensors.Engage_Right();
            if (isLeftSide) {
                //mecanumDrive.run_Motor_angle_locked_with_Timer(-x_offset + flickDirection * Math.sin(Math.toRadians(15.0)), Math.cos(Math.toRadians(15.0)), 1.0, 0.2);
                mecanumDrive.run_Motor_angle_locked_with_Timer(+ x_offset - flickDirection * Math.sin(Math.toRadians(15.0)), -Math.cos(Math.toRadians(15.0)), 1.0, powerset);
            }else {
                double crytooffset = 0.0; // extra offset
                //mecanumDrive.run_Motor_angle_locked_with_Timer(crytooffset +2.0*x_offset + flickDirection * Math.sin(Math.toRadians(25.0)), Math.cos(Math.toRadians(25.0)), timeoutset, powerset);
                mecanumDrive.run_Motor_angle_locked_with_Timer(crytooffset + x_offset - flickDirection * Math.sin(Math.toRadians(25.0)), -Math.cos(Math.toRadians(25.0)), timeoutset, powerset);
            }

        } else {                // go backward blue alliance
            Range_sensors.Engage_Left();
            if (isLeftSide) {   // a little to the left
                double crytooffset = 0.0;  // extra offset
                //mecanumDrive.run_Motor_angle_locked_with_Timer(crytooffset + x_offset - flickDirection * Math.sin(Math.toRadians(25.0)), -Math.cos(Math.toRadians(25.0)), timeoutset, powerset);
                mecanumDrive.run_Motor_angle_locked_with_Timer(crytooffset + x_offset + flickDirection * Math.sin(Math.toRadians(15.0)), Math.cos(Math.toRadians(15.0)), timeoutset, powerset);
            } else {
                //mecanumDrive.run_Motor_angle_locked_with_Timer(x_offset - flickDirection * Math.sin(Math.toRadians(15.0)), -Math.cos(Math.toRadians(15.0)), 1.0, powerset);
                mecanumDrive.run_Motor_angle_locked_with_Timer(- x_offset + flickDirection * Math.sin(Math.toRadians(15.0)), Math.cos(Math.toRadians(15.0)), 1.0, 0.2);
            }
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
        //GlypherObject.BooterKickOut();   // another option instead of using the wheels
        //GlypherObject.RunGlypherMotor(-1); // bring down glyph
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, 1, 1.5, 0.05); // move forward

        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 0.5, 0.05); // move back a little
        //GlypherObject.RunGlypherMotor(0);

        // hit again if necessary
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, 1, 1.5, 0.05); // move forward
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 0.5, 0.05); // move back a little

        mecanumDrive.stop_Motor_with_locked();

    }

    // =========================== Move to X-Y location using Ultra Sonic sensors =====================

    protected void Move_to_Distance_inch(double frontDistance_target, double leftDistance, double rightDistance, double timeout) {

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
        double Xerror = 1000.0;
        double Yerror = 1000.0;

        while (basenavigation_elapsetime.seconds() < timeout && opModeIsActive() && Math.abs(new_distance) > distance_tolerance ) {

            if (leftDistance > 0.0) {
                Xnew_distance = Range_sensors.getDistance_Left_inch(10, 200);
                Xerror = leftDistance - Xnew_distance;
            } else {
                Xnew_distance = Range_sensors.getDistance_Right_inch(10, 200);
                Xerror = Xnew_distance - rightDistance;
            }

            double Ynew_distance = Range_sensors.getDistance_frontLeft_inch(10, 200);
            Yerror = Ynew_distance - frontDistance_target;

            new_distance = Math.hypot(Xerror, Yerror);

            double totalpower = maxpower * Math.abs( new_distance * PID_kp + (new_distance - old_distance) * PID_kd );

            mecanumDrive.set_max_power(Math.min(maxpower, totalpower));

            mecanumDrive.run_Motor_angle_locked(Xerror / new_distance, Yerror / new_distance);

            telemetry.addData("Distance to the target location: ", new_distance);
            telemetry.update();

            old_distance = new_distance;
            idle();
        }
        mecanumDrive.stop_Motor_with_locked();
    }

}