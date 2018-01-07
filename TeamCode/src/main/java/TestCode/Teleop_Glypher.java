package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.Glypher;
import Library.JewelServo;
import Library.REVColorDistance;


@TeleOp(name = "Glypher Test", group = "TestCode")
@Disabled
public class Teleop_Glypher extends LinearOpMode
{
    private Glypher GlypherObject = null;


    public void runOpMode() throws InterruptedException
    {
        GlypherObject = new Glypher(hardwareMap);


        waitForStart();


        while(opModeIsActive())
        {
            GlypherObject.setElevatorPower(-gamepad2.right_stick_y);

            GlypherObject.GrabberSetPower(gamepad2.right_stick_x);
            telemetry.addData("Elevator position: ", GlypherObject.getElevatorPosition());

            //telemetry.addData("Tilt Encoder: ", GlypherObject.Tilt_getCurrentEncoder());

            telemetry.update();
            idle();
        }

    }

}
