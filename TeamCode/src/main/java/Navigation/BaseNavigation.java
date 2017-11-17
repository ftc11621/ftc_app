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
    }

    public void NavigationTest() {
        telemetry.addData("angle:", mecanumDrive.get_locked_angle());
        mecanumDrive.set_angle_locked(10.0); //Initial_orientation + 15.0);

        //while (Math.abs( mecanumDrive.getRobotAngle() - 10.0) > 2.0) {
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, 1.5, 0.2);
        //}
        mecanumDrive.stop_Motor_with_locked();

        telemetry.update();
        //mecanumDrive.run_Motor_angle_locked(0.0,0.0);
    }

    // ================ Robot Turn ============
    public void Robot_Turn(double time_sec, double power, double angle) {
        //telemetry.addData("angle:", mecanumDrive.get_locked_angle());
        //mecanumDrive.set_max_power(0.2);
        mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle() + angle); //Initial_orientation + 15.0);

        //if (isLeftTurn){
        //    mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()+15.0); //Initial_orientation + 15.0);
        //}
        //else{
        //    mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()-15.0); //Initial_orientation - 15.0);
        //}
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, time_sec, power);
        //mecanumDrive.stop_Motor_with_locked();
        //mecanumDrive.set_angle_locked(Initial_orientation);
    }

    public void Robot_Forward(double time_sec, double power, double angle) {
        mecanumDrive.set_angle_locked(Initial_orientation);
        mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle() + angle); //Initial_orientation + 15.0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 1.0, time_sec, power);
    }

    public void Robot_Reverse(double time_sec, double power, double angle) {
        mecanumDrive.set_angle_locked(Initial_orientation);
        mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle() + angle); //Initial_orientation + 15.0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, -1.0, time_sec, power);
    }

    public void Robot_Glyph_Deposit() {
        // kick glyph out
        GlypherObject.RunGlypherMotor(-1); // bring down glyph
        //mecanumDrive.set_max_power(0.2);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 1.5, 0.2); // move back a little
        GlypherObject.RunGlypherMotor(0);

        mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle() - 90);
        mecanumDrive.run_Motor_angle_locked_with_Timer(-1, 0, 2.0, 0.2); // hit glyph from the side

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