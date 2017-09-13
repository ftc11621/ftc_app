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
    private boolean is_angle_locked = false;

    public void runOpMode() throws InterruptedException
    {
        mecanumDrive = new Mecanum(hardwareMap);


        waitForStart();

        //mecanumDrive.start_angle_locked(90.0f);  // to lock on an orientation angle


        while(opModeIsActive())
        {
            if (gamepad1.x) { // lock the current orientation
                is_angle_locked = true;
                mecanumDrive.current_angle_locked();
            }

            // disable angle lock when the left joystick adjust angle
            if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.2) {
                is_angle_locked = false;
            }

            if (gamepad1.a) {   // lock toward crytobox
                mecanumDrive.set_angle_locked(180.0f);
                is_angle_locked = true;
            }


            // or locked in an angle
            if (is_angle_locked) {
                mecanumDrive.run_Motor_angle_locked(gamepad1.right_stick_x, -gamepad1.right_stick_y);
            } else {
                //rotation = -gamepad1.left_stick_x;
                rotation = 0.0f;
                if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.2) {
                    rotation = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_x, (double) -gamepad1.left_stick_y));
                }
                mecanumDrive.run_Motors_no_encoder(gamepad1.right_stick_x, -gamepad1.right_stick_y, rotation);
            }

            idle();

        }
    }
}
