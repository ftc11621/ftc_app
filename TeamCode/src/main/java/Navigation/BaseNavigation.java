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

    final double Initial_orientation = -90.0;   // initial robot orientatian respect to Jewels

    @Override
    public void runOpMode() {

        JewelFlicker = new JewelServo(hardwareMap);
        Colordistance = new REVColorDistance(hardwareMap);
        mecanumDrive = new Mecanum(hardwareMap);
        GlypherObject = new Glypher(hardwareMap);


        //JewelFlicker.LeftFlick();       // so it doesn't go over 18"

        waitForStart();

        mecanumDrive.Start();
        //JewelFlicker.CenterFlick();

        navigate();

        while(opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
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
        mecanumDrive.set_max_power(0.2);
        mecanumDrive.set_angle_locked(10.0); //Initial_orientation + 15.0);

        //while (Math.abs( mecanumDrive.getRobotAngle() - 10.0) > 2.0) {
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, 1.5);
        //}
        mecanumDrive.set_max_power(0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0,0.0,0.05);
        telemetry.update();
        //mecanumDrive.run_Motor_angle_locked(0.0,0.0);
    }

    private void Robot_Turn(boolean isLeftTurn) {
        //telemetry.addData("angle:", mecanumDrive.get_locked_angle());
        mecanumDrive.set_max_power(0.2);
        if (isLeftTurn){
            mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()+15.0); //Initial_orientation + 15.0);
        }
        else{
            mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()-15.0); //Initial_orientation - 15.0);
        }
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 0.0, 2.5);
        mecanumDrive.set_max_power(0);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0.0,0.0,0.05);
        mecanumDrive.set_angle_locked(Initial_orientation);
    }

    // ====================================================================
    // Jewel Flicker method
    public void flickJewel() {

        JewelFlicker.LowerBeam();

        Colordistance.measure();

        telemetry.addData("Red :", Colordistance.getRed());
        telemetry.addData("Blue:", Colordistance.getBlue());

        if (Colordistance.getDistance_CM() < 11.0) {
            if (isRedAlliance) { // for Red alliance
                if ((Colordistance.getBlue() - Colordistance.getRed()) > 4) {
                    telemetry.addData("Flick: ", "Left");
                    Robot_Turn(true);
                    //mecanumDrive.set_angle_locked(Initial_orientation + 15.0);
                } else if ((Colordistance.getRed() - Colordistance.getBlue()) > 4) {
                    telemetry.addData("Flick: ", "Right");
                    Robot_Turn(false);
                    //mecanumDrive.set_angle_locked(Initial_orientation - 15.0);
                }
            } else {              // for Blue alliance
                if ((Colordistance.getRed() - Colordistance.getBlue()) > 4) {
                    telemetry.addData("Flick: ", "Left");
                    //mecanumDrive.set_angle_locked(Initial_orientation + 15.0);
                    Robot_Turn(true);
                    //JewelFlicker.LeftFlick();
                } else if ((Colordistance.getBlue() - Colordistance.getRed()) > 4) {
                    telemetry.addData("Flick: ", "Right");
                    Robot_Turn(false);
                    //mecanumDrive.set_angle_locked(Initial_orientation - 15.0);
                }
            }
            mecanumDrive.run_Motor_angle_locked(0.0,0.0);   // only spin, no X-Y movement
        } else if (JewelFlicker.isJewelDetected) {  // if detected when lowering the beam near jewel
            if (isRedAlliance) { // for Red alliance
                if (JewelFlicker.isJewelRed) {
                    telemetry.addData("Detected, Flick: ", "Right");
                    Robot_Turn(false);
                } else {
                    telemetry.addData("Detected, Flick: ", "Left");
                    Robot_Turn(true);
                }
            } else {              // Blue alliance
                if (JewelFlicker.isJewelRed) {
                    telemetry.addData("Detected, Flick: ", "Left");
                    Robot_Turn(true);
                } else {
                    telemetry.addData("Detected Flick: ", "Right");
                    Robot_Turn(false);
                }
            }
            mecanumDrive.run_Motor_angle_locked(0.0,0.0);   // only spin, no X-Y movement
        }

        JewelFlicker.Initial();

        mecanumDrive.set_angle_locked(Initial_orientation);
        mecanumDrive.set_max_power(0.3);
        if (isRedAlliance) {    // move toward crytobox
            mecanumDrive.run_Motor_angle_locked_with_Timer(0, 1, 0.7);
        } else {
            mecanumDrive.run_Motor_angle_locked_with_Timer(0, -1, 0.7);
        }
        mecanumDrive.set_max_power(0.0);  // to stop
        mecanumDrive.run_Motor_angle_locked(0.0,0.0);

        //JewelFlicker.RaiseBeam();
        JewelFlicker.Initial();

        if (isRedAlliance) {   // to face the crytobox
            if (isLeftSide) {
                mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()-90);
            }
        } else {
            if (isLeftSide) {
                mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()+180);
            } else {
                mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()-90);
            }
        }
        //mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()+180);
        mecanumDrive.set_max_power(0.3);
        mecanumDrive.run_Motor_angle_locked_with_Timer(0,0,0.7);
        mecanumDrive.set_max_power(0.0); // to stop
        mecanumDrive.run_Motor_angle_locked(0.0,0.0);


//        mecanumDrive.set_angle_locked(Initial_orientation);

        JewelFlicker.Initial();

        // kick glyph out
        GlypherObject.BooterKickOut();

        // turn 90 degree and push the glyph in
        mecanumDrive.set_max_power(0.2);
        mecanumDrive.set_angle_locked(Initial_orientation+180);
        mecanumDrive.run_Motor_angle_locked_with_Timer(-1,0,1.0);
        mecanumDrive.set_max_power(0.0); // to stop
        mecanumDrive.run_Motor_angle_locked(0.0,0.0);

        //mecanumDrive.run_Motor_angle_locked_with_Timer(0.0, 1.0, 2.0);

        telemetry.update();
    }
    // ====================================================================

    // ====================================================================
    // Getting off Balancing Stone
    public void offBalancingStone() {

        basenavigation_elapsetime.reset();
        while(basenavigation_elapsetime.seconds() < 10.0) {
            if (isRedAlliance) {
                if (isLeftSide) {
                    mecanumDrive.set_angle_locked(0.0);
                    mecanumDrive.run_Motor_angle_locked(-0.8, 0.2);
                } else {
                    mecanumDrive.set_angle_locked(90.0);
                    mecanumDrive.run_Motor_angle_locked(-0.2, 0.8);
                }
            } else {    // Blue alliance
                if (isLeftSide) {
                    mecanumDrive.set_angle_locked(-90.0);
                    mecanumDrive.run_Motor_angle_locked(0.2, 0.8);
                } else {
                    mecanumDrive.set_angle_locked(0.0);
                    mecanumDrive.run_Motor_angle_locked(0.8, 0.2);
                }
            }
        }
        basenavigation_elapsetime.reset();
        while(basenavigation_elapsetime.seconds() < 3.0) {
            mecanumDrive.run_Motor_angle_locked(0.0, 0.4);
        }
    }
}