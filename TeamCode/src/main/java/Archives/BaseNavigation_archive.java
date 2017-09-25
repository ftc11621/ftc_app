package Archives;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Library.IMU;
import Library.JewelServo;
import Library.REVColorDistance;

public abstract class BaseNavigation_archive extends LinearOpMode {

    protected Chassis_motors chassis_Object = null;
    private IMU IMU_Object = null;
    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;

    private double X_cm, Y_cm, Orient_degree, yaw_offset;      // robot coordinates

    @Override
    public void runOpMode() {

        chassis_Object = new Chassis_motors(hardwareMap);
        IMU_Object = new IMU(hardwareMap);
        JewelFlicker = new JewelServo(hardwareMap);
        Colordistance = new REVColorDistance(hardwareMap);

        JewelFlicker.LeftFlick();       // so it doesn't go over 18"

        waitForStart();

        IMU_Object.start();
        JewelFlicker.CenterFlick();

        navigate();

        while(opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
        {
            idle();
        }
    }

    protected abstract void navigate();

    // ====================================================================
    // Jewel Flicker method
    public void flickJewel(boolean isRedAlliance) {

        JewelFlicker.LowerBeam();

        Colordistance.measure();

        if (isRedAlliance) { // for Red alliance
            if (Colordistance.getBlue() > 20 && Colordistance.getRed() < 15) {
                JewelFlicker.LeftFlick();
            } else if (Colordistance.getRed() > 20 && Colordistance.getBlue() < 15) {
                JewelFlicker.RightFlick();
            }
        } else {              // for Blue alliance
            if (Colordistance.getRed() > 20 && Colordistance.getBlue() < 15) {
                JewelFlicker.LeftFlick();
            } else if (Colordistance.getBlue() > 20 && Colordistance.getRed() < 15) {
                JewelFlicker.RightFlick();
            }
        }

        JewelFlicker.RaiseBeam();
    }
    // ====================================================================

    // to overwrite the current location
    public void setCurrentLocation(double X_loc_cm, double Y_loc_cm, double Angle_degree) {
        IMU_Object.measure();
        X_cm = X_loc_cm;
        Y_cm = Y_loc_cm;
        Orient_degree = Angle_degree; // offset yaw
        yaw_offset = Angle_degree - IMU_Object.yaw(); // offset from the initial yaw reading
    }

    // Get robot location and orientation
    public double get_X_cm() { return X_cm; }
    public double get_Y_cm() { return Y_cm; }
    public double get_Orient_degree() {
        IMU_Object.measure();   // measure orientation
        Orient_degree = IMU_Object.yaw() + yaw_offset;
        return Orient_degree;
    }

    // Move robot a distance straight line
    private void moveDistance_coarse(double distance_move_cm) { // higher tolerance for speed
        chassis_Object.run_Motors_encoder(1.0, distance_move_cm, distance_move_cm, 5.0, 3.0);
        // average of left and right motors
        double dist = 0.5*(chassis_Object.getLeftDistance_cm() + chassis_Object.getRightDistance_cm());
        X_cm = dist * Math.cos(Orient_degree * Math.PI / 180.0);
        Y_cm = dist * Math.sin(Orient_degree * Math.PI / 180.0);
    }
    private void moveDistance_fine(double distance_move_cm) { // smaller tolerance
        get_Orient_degree();    // current orientation
        chassis_Object.run_Motors_encoder(1.0, distance_move_cm, distance_move_cm, 5.0, 0.5);
        // average of left and right motors
        double dist = 0.5*(chassis_Object.getLeftDistance_cm() + chassis_Object.getRightDistance_cm());
        X_cm += dist * Math.cos(Orient_degree * Math.PI / 180.0);
        Y_cm += dist * Math.sin(Orient_degree * Math.PI / 180.0);
    }

    // spin and get the current orientation
    private void spinEncoderDegree_coarse(double spin_degree) {
        chassis_Object.spin_encoder_degree(1.0, spin_degree, 5.0, 1.0);
        get_Orient_degree();        // measure and get orientation
    }
    private void spinEncoderDegree_fine(double spin_degree) {
        chassis_Object.spin_encoder_degree(1.0, spin_degree, 5.0, 0.5);
        get_Orient_degree();        // measure and get orientation
    }

    public void goToXY_coarse(double x_target, double y_target) {    // go to X,Y coordinate
        get_Orient_degree();    // current orientation
        double target_orient = Math.toDegrees(Math.atan2(x_target-X_cm, y_target-Y_cm));
        double angle_to_turn = Orient_degree - target_orient;

        telemetry.addData("target angle : ", target_orient);
        telemetry.addData("angle to turn: ", angle_to_turn);

        spinEncoderDegree_coarse(angle_to_turn);
        telemetry.addData("angle remaining to turn: ", Orient_degree - target_orient);

        moveDistance_coarse(Math.sqrt(Math.pow(x_target-X_cm,2) + Math.pow(x_target-Y_cm,2)));
        telemetry.addData("X: ", X_cm); telemetry.addData("Y: ", Y_cm);
        telemetry.update();
    }

    public void aimAt_fine(double x_target, double y_target) {
        double target_orient = Math.toDegrees(Math.atan2(x_target-X_cm, y_target-Y_cm));
        double angle_to_turn = get_Orient_degree() - target_orient;
        while (Math.abs(angle_to_turn) > 1.0 && opModeIsActive()) {
            spinEncoderDegree_fine(angle_to_turn);
            angle_to_turn = Orient_degree - target_orient;
            telemetry.addData("target angle : ", target_orient);
            telemetry.addData("angle to turn: ", Orient_degree);

            telemetry.update();
            //sleep(10);
            //idle();
        }
    }
}
