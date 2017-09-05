package Navigation;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import Library.Chassis_motors;
import Library.IMU;

public abstract class BaseNavigation extends LinearOpMode {

    protected Chassis_motors chassis_Object = null;
    private IMU IMU_Object = null;

    private double X_cm, Y_cm, Orient_degree, yaw_offset;      // robot coordinates

    @Override
    public void runOpMode() {

        chassis_Object = new Chassis_motors(hardwareMap);
        IMU_Object = new IMU(hardwareMap);

        waitForStart();

        IMU_Object.start();

        navigate();

        while(opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
        {
            idle();
        }
    }

    protected abstract void navigate();

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
    public void moveEncoderDistance(double distance_move_cm) {
        chassis_Object.run_Motors_encoder(1.0, distance_move_cm, distance_move_cm, 5.0, 1.0);
        // average of left and right motors
        double dist = 0.5*(chassis_Object.getLeftDistance_cm() + chassis_Object.getRightDistance_cm());
        X_cm = dist * Math.cos(Orient_degree * Math.PI / 180.0);
        Y_cm = dist * Math.sin(Orient_degree * Math.PI / 180.0);
    }

    // spin and get the current orientation
    public void spinEncoderDegree(double spin_degree) {
        chassis_Object.spin_encoder_degree(1.0, spin_degree, 5.0, 1.0);
        get_Orient_degree();        // measure and get orientation
    }
}
