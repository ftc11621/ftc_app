package Navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import Library.JewelServo;
import Library.Mecanum;
import Library.REVColorDistance;

public abstract class BaseNavigation extends LinearOpMode {

    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;
    boolean isRedAlliance, isLeftSide;
    ElapsedTime basenavigation_elapsetime = new ElapsedTime();

    @Override
    public void runOpMode() {

        JewelFlicker = new JewelServo(hardwareMap);
        Colordistance = new REVColorDistance(hardwareMap);
        mecanumDrive = new Mecanum(hardwareMap);


        JewelFlicker.LeftFlick();       // so it doesn't go over 18"

        waitForStart();

        mecanumDrive.Start();
        JewelFlicker.CenterFlick();

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
        mecanumDrive.setCurrentAngle(90.0);     // to the right of Jewel
    }

    // ====================================================================
    // Jewel Flicker method
    public void flickJewel() {

        JewelFlicker.LowerBeam();

        Colordistance.measure();

        telemetry.addData("Red :", Colordistance.getRed());
        telemetry.addData("Blue:", Colordistance.getBlue());


        if (Colordistance.getDistance_CM() < 10.0) {
            if (isRedAlliance) { // for Red alliance
                if ((Colordistance.getBlue() - Colordistance.getRed()) > 4) {
                    telemetry.addData("Flick: ", "Left");
                    mecanumDrive.set_angle_locked(90.0 + 15.0);
                    //JewelFlicker.LeftFlick();
                } else if ((Colordistance.getRed() - Colordistance.getBlue()) > 4) {
                    telemetry.addData("Flick: ", "Right");
                    mecanumDrive.set_angle_locked(90.0 - 15.0);
                    //JewelFlicker.RightFlick();
                }
            } else {              // for Blue alliance
                if ((Colordistance.getRed() - Colordistance.getBlue()) > 4) {
                    telemetry.addData("Flick: ", "Left");
                    mecanumDrive.set_angle_locked(90.0 + 15.0);
                    //JewelFlicker.LeftFlick();
                } else if ((Colordistance.getBlue() - Colordistance.getRed()) > 4) {
                    telemetry.addData("Flick: ", "Right");
                    mecanumDrive.set_angle_locked(90.0 - 15.0);
                    //JewelFlicker.RightFlick();
                }
            }
        }

        JewelFlicker.RaiseBeam();
        //JewelFlicker.RightFlick();
        //JewelFlicker.LeftFlick();
        mecanumDrive.set_angle_locked(90.0);

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
