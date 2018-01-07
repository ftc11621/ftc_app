package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.JewelServo;
import Library.REVColorDistance;


@TeleOp(name = "Test Flicker Red Alliance", group = "TestCode")
@Disabled
public class Teleop_JewelTest extends LinearOpMode
{
    private JewelServo JewelFlicker = null;
    private REVColorDistance Colordistance = null;

    public void runOpMode() throws InterruptedException
    {
        JewelFlicker = new JewelServo(hardwareMap);

        waitForStart();

        JewelFlicker.flickJewel(true);

        while(opModeIsActive())
        {
            /*
            Colordistance.measure();


            if (Colordistance.getBlue() > 20 && Colordistance.getRed()<15) {
            //    JewelFlicker.LeftFlick();
            }else if (Colordistance.getRed()>20 && Colordistance.getBlue()<15){
            //    JewelFlicker.RightFlick();


            }
            */
            idle();
        }
        //JewelFlicker.RaiseBeam();
    }

}
