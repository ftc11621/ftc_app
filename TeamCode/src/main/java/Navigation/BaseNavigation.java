package Navigation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Library.JewelServo;
import Library.Mecanum;
import Library.REVColorDistance;

public abstract class BaseNavigation extends LinearOpMode {

    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;
    private Mecanum mecanumDrive = null;


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

        if (isRedAlliance) { // for Red alliance
            if (Colordistance.getBlue() > 15 && (Colordistance.getBlue()-Colordistance.getRed()) > 8) {
                JewelFlicker.LeftFlick();
            } else if (Colordistance.getRed() > 15 && (Colordistance.getRed()-Colordistance.getBlue()) > 8) {
                JewelFlicker.RightFlick();
            }
        } else {              // for Blue alliance
            if (Colordistance.getRed() > 15 && (Colordistance.getRed()-Colordistance.getBlue()) > 8) {
                JewelFlicker.LeftFlick();
            } else if (Colordistance.getBlue() > 15 && (Colordistance.getBlue()-Colordistance.getRed()) > 8 ) {
                JewelFlicker.RightFlick();
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

        if (isRedAlliance) {
            if(isLeftSide) {

            } else {

            }

        } else {    // Blue alliance
            if(isLeftSide) {

            } else {

            }
        }
    }

}
