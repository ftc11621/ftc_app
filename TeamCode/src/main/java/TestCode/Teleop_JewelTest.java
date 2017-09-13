package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.JewelServo;
import Library.REVColorDistance;

/**
 * Created by jakemueller on 8/10/17.
 */
@TeleOp(name = "JewelFlickerTest", group = "TestCode")
public class Teleop_JewelTest extends LinearOpMode
{
    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;

    public void runOpMode() throws InterruptedException
    {
        JewelFlicker = new JewelServo(hardwareMap);
        Colordistance = new REVColorDistance(hardwareMap);

        waitForStart();

        while(opModeIsActive())
        {
            Colordistance.measure();
            if (Colordistance.getBlue() > 10) {
                JewelFlicker.LeftFlick();
            }else if (Colordistance.getRed()>10){
                JewelFlicker.RightFlick();


            }
            idle();
        }
    }
}
