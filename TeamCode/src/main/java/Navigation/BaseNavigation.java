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

    // ====================================================================
    // Jewel Flicker method
    public void flickJewel(boolean isRedAlliance) {

        JewelFlicker.LowerBeam();

        Colordistance.measure();

        telemetry.addData("Red :", Colordistance.getRed());
        telemetry.addData("Blue:", Colordistance.getBlue());
        telemetry.update();

        if (Colordistance.getDistance_CM() < 10.0) {
            if (isRedAlliance) { // for Red alliance
                if ((Colordistance.getBlue() - Colordistance.getRed()) > 4) {
                    JewelFlicker.LeftFlick();
                } else if ((Colordistance.getRed() - Colordistance.getBlue()) > 4) {
                    JewelFlicker.RightFlick();
                }
            } else {              // for Blue alliance
                if ((Colordistance.getRed() - Colordistance.getBlue()) > 4) {
                    JewelFlicker.LeftFlick();
                } else if ((Colordistance.getBlue() - Colordistance.getRed()) > 4) {
                    JewelFlicker.RightFlick();
                }
            }
        }

        JewelFlicker.RaiseBeam();
        //JewelFlicker.RightFlick();
        JewelFlicker.LeftFlick();
    }
    // ====================================================================

    // ====================================================================
    // Getting off Balancing Stone
    public void offBalancingStone(boolean isRedAlliance, boolean isLeftSide) {

        double timeoutsec = 10.0;

        basenavigation_elapsetime.reset();

        while(basenavigation_elapsetime.seconds() < timeoutsec) {
            if (isRedAlliance) {
                mecanumDrive.set_angle_locked(90.0);
                if (isLeftSide) {
                    mecanumDrive.run_Motor_angle_locked(0.2, 0.8);
                } else {
                    mecanumDrive.run_Motor_angle_locked(-0.2, 0.8);
                }

            } else {    // Blue alliance
                mecanumDrive.set_angle_locked(-90.0);
                if (isLeftSide) {
                    mecanumDrive.run_Motor_angle_locked(0.2, 0.8);
                } else {
                    mecanumDrive.run_Motor_angle_locked(-0.2, 0.8);
                }
            }
        }
    }
}
