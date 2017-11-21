package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.JewelServo;
import Library.Mecanum;

@TeleOp(name = "Mecanum Drive test", group = "TestCode")
@Disabled
public class Teleop_mecanum extends LinearOpMode
{
    private Mecanum mecanumDrive = null;
    private float rotation;
    private boolean is_angle_locked = false; // if locked to the left joystick

    public void runOpMode() throws InterruptedException
    {
        mecanumDrive = new Mecanum(hardwareMap);
        mecanumDrive.Start();  // default to start mecanum and its IMU, robot point away from the driver

        waitForStart();


        while(opModeIsActive())
        {

            if (gamepad1.b) {  // unlock robot orientation from left joystick. Do this before gamepad1.x below
                is_angle_locked = false;
            }


            if(is_angle_locked) {   // angle locked to left joystick where it points to
                if(gamepad1.dpad_down) {
                    mecanumDrive.set_max_power(0.3);
                } else if(gamepad1.dpad_left) {
                    mecanumDrive.set_max_power(0.5);
                } else if(gamepad1.dpad_up) {
                    mecanumDrive.set_max_power(0.7);
                } else if(gamepad1.dpad_right) {
                    mecanumDrive.set_max_power(1.0);
                }
                mecanumDrive.run_Motor_angle_locked_relative_to_driver(gamepad1.right_stick_x, gamepad1.right_stick_y);

                if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.5) { // left stick actually points somewhere
                    float angle_lock = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_x, (double) gamepad1.left_stick_y));
                    mecanumDrive.set_angle_locked(angle_lock);
                }
            } else {

                rotation = 0.0f;
                if (gamepad1.dpad_left) {
                    rotation = 0.4f;
                } else if (gamepad1.dpad_right) {
                    rotation = -0.4f;
                }
                mecanumDrive.run_Motors_no_encoder(gamepad1.right_stick_x, -gamepad1.right_stick_y, rotation);

                if (gamepad1.x) { // point left joystick to the front of robot, then press X
                    if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.5) { // left stick actually points somewhere
                        float angle_robot = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_x, (double) gamepad1.left_stick_y));
                        mecanumDrive.setCurrentAngle(angle_robot);
                        mecanumDrive.set_angle_locked(angle_robot);
                        is_angle_locked = true;
                    }
                }
            }




            telemetry.addData("Locked angle  :", mecanumDrive.get_locked_angle());
            //telemetry.addData("IMU angle  :", mecanumDrive.IMU_getAngle());
            telemetry.addData("Robot angle:", mecanumDrive.getRobotAngle());
            if (is_angle_locked) {
                telemetry.addData("Angle Lock: ", "Yes");
            } else {
                telemetry.addData("Angle Lock: ", "No");
            }
            telemetry.update();

            idle();

        }
    }
}
