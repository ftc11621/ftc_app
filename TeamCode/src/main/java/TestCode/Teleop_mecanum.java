package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.JewelServo;
import Library.Mecanum;

/**
 * Created by jakemueller on 8/10/17.
 */
@TeleOp(name = "Mecanum Drive test", group = "TestCode")
public class Teleop_mecanum extends LinearOpMode
{
    private Mecanum mecanumDrive = null;
    private float rotation;

    public void runOpMode() throws InterruptedException
    {
        mecanumDrive = new Mecanum(hardwareMap);


        waitForStart();


        while(opModeIsActive())
        {

            rotation = -gamepad1.right_stick_x;

            mecanumDrive.run_Motors_no_encoder(gamepad1.left_stick_x, -gamepad1.left_stick_y, rotation);

            idle();

        }
    }
}
