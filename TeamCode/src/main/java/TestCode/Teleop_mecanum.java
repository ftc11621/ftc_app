package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.JewelServo;
import Library.Mecanum;

@TeleOp(name = "Mecanum Drive test", group = "TestCode")
public class Teleop_mecanum extends LinearOpMode
{
    private Mecanum mecanumDrive = null;
    private float rotation;

    public void runOpMode() throws InterruptedException
    {
        mecanumDrive = new Mecanum(hardwareMap);


        waitForStart();

        //mecanumDrive.start_angle_locked(90.0f);  // to lock on an orientation angle


        while(opModeIsActive())
        {

            rotation = -gamepad1.left_stick_x;

            mecanumDrive.run_Motors_no_encoder(gamepad1.right_stick_x, -gamepad1.right_stick_y, rotation);

            // or locked in an angle
            //mecanumDrive.run_Motor_angle_locked(gamepad1.right_stick_x, -gamepad1.right_stick_y);

            idle();

        }
    }
}
