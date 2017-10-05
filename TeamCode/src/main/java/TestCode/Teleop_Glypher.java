package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.Glypher;
import Library.JewelServo;
import Library.REVColorDistance;


@TeleOp(name = "Glypher Test", group = "TestCode")
public class Teleop_Glypher extends LinearOpMode
{
    private Glypher GlypherObject = null;


    public void runOpMode() throws InterruptedException
    {
        GlypherObject = new Glypher(hardwareMap);


        waitForStart();


        while(opModeIsActive())
        {
            GlypherObject.RunGlypherMotor(-gamepad2.left_stick_y);

            GlypherObject.Tilt(-gamepad2.right_stick_y);

            if (gamepad2.y) {
                GlypherObject.BooterKickOut();
            } else if (gamepad2.a) {
                GlypherObject.BooterRetract();
            }

            telemetry.addData("Tilt Encoder: ", GlypherObject.Tilt_getCurrentEncoder());
            telemetry.update();
            idle();
        }

    }

}
