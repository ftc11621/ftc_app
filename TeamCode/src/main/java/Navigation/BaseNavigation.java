package Navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import javax.microedition.khronos.opengles.GL;

import Library.Glypher;
import Library.JewelServo;
import Library.Mecanum;
import Library.REVColorDistance;

public abstract class BaseNavigation extends LinearOpMode {

    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;
    private Glypher GlypherObject = null;

    boolean isRedAlliance, isLeftSide;
    ElapsedTime basenavigation_elapsetime = new ElapsedTime();

    final double Initial_orientation = 90.0;   // initial robot orientatian respect to Jewels

    @Override
    public void runOpMode() {

        JewelFlicker = new JewelServo(hardwareMap);
        Colordistance = new REVColorDistance(hardwareMap);
        mecanumDrive = new Mecanum(hardwareMap);
        GlypherObject = new Glypher(hardwareMap);

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
    }

    // ============================ testing purpose ===================================
    public void NavigationTest() {
        double timeoutsec = 5.0;
        double testpower = 0.4;
        double waittime = 2.0;

        mecanumDrive.set_Angle_tolerance(5.0);

        telemetry.addData("angle 0:", mecanumDrive.getRobotAngle());

        //mecanumDrive.set_angle_locked(0.0);
        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, 0.0);
        //mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, 5.0, 0.4);
        //mecanumDrive.stop_Motor_with_locked();
        telemetry.addData("angle 1:", mecanumDrive.getRobotAngle());

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        //mecanumDrive.set_angle_locked(90.0);
        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, 90.0);
        //mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, 5.0, 0.4);
        //mecanumDrive.stop_Motor_with_locked();
        telemetry.addData("angle 2:", mecanumDrive.getRobotAngle());

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, 180.0);

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower, -90.0);

        // pause 2 seconds
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, waittime, 0.0);

        mecanumDrive.spin_Motor_angle_locked_with_Timer(timeoutsec, testpower,0.0);

        telemetry.update();
    }


    // ================ Robot Turn ============
    public void Robot_Turn(double time_sec, double power, double angle) {

        mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle() + angle); //Initial_orientation + 15.0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, time_sec, power);
    }

    public void Robot_Forward(double time_sec, double power, double angle) {
        //mecanumDrive.set_angle_locked(Initial_orientation);
        mecanumDrive.set_angle_locked(Initial_orientation + angle); //Initial_orientation + 15.0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 1.0, time_sec, power);
    }

    public void Robot_Reverse(double time_sec, double power, double setangle) {
        //mecanumDrive.set_angle_locked(Initial_orientation);
        //telemetry.addData("angle", mecanumDrive.IMU_getAngle());
        telemetry.addData("set angle", setangle);
        telemetry.addData("locked angle", Initial_orientation);
        mecanumDrive.set_angle_locked(Initial_orientation + setangle); //Initial_orientation + 15.0);
        telemetry.addData("new angle", mecanumDrive.get_locked_angle());

        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, -1.0, time_sec, power);
        //telemetry.addData("new angle", mecanumDrive.IMU_getAngle());
        telemetry.update();
    }

    public void Robot_Glyph_Deposit() {
        // kick glyph out
        GlypherObject.RunGlypherMotor(-1); // bring down glyph
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 1.0, 0.0); // wait for glyph to go down

        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 0.5, 0.1); // move back a little
        GlypherObject.RunGlypherMotor(0);

        Robot_Turn( 2, .2, 45);

        Robot_Turn( 2, .2, 45);

        mecanumDrive.run_Motor_angle_locked_with_Timer(-1, 0, 1.5, 0.1); // hit glyph from the side

        //mecanumDrive.run_Motor_angle_locked_with_Timer(0, 1, 1.5, 0.1); // hit glyph from the side

    }

    public void Reset_locked_angle() {
        mecanumDrive.set_angle_locked(Initial_orientation);
    }

    // ====================================================================
    // Jewel Flicker method
    public void flickJewel() {
        JewelFlicker.Initial();
        JewelFlicker.flickJewel(isRedAlliance);
        telemetry.addData("Red value : ", JewelFlicker.readRed);
        telemetry.addData("Blue value: ", JewelFlicker.readBlue);
        telemetry.update();
    }

    // ====================================================================
}