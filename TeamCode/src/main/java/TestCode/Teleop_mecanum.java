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
    private boolean is_angle_locked = false; // if locked to the left joystick

    public void runOpMode() throws InterruptedException
    {
        mecanumDrive = new Mecanum(hardwareMap);


        waitForStart();

        mecanumDrive.Start();  // default to start mecanum and its IMU, robot point away from the driver

        // Start with pointing the joystick to where the robot points to, then press X

        while(opModeIsActive())
        {

            if (gamepad1.b) {  // unlock robot orientation from left joystick. Do this before gamepad1.x below
                is_angle_locked = false;
            }


            if(is_angle_locked) {   // angle locked to left joystick where it points to
                if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.5) { // left stick actually point somewhere
                    float angle_robot = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_y, (double) gamepad1.left_stick_x));
                    mecanumDrive.set_angle_locked(angle_robot);
                }
            } else {
                if (gamepad1.x) { // point left joystick to the front of robot, then press X
                    if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.5) { // left stick actually points somewhere
                        float angle_robot = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_y, (double) gamepad1.left_stick_x));
                        mecanumDrive.setCurrentAngle(angle_robot);
                        mecanumDrive.set_current_angle_locked();
                        is_angle_locked = true;
                    }
                }
            }


            // if locked in an angle
            if (is_angle_locked) {
                // mecanumDrive.run_Motor_angle_locked(gamepad1.right_stick_x, -gamepad1.right_stick_y);
                // Experimental driving it relative to the driver X-Y instead of the robot X-Y
                mecanumDrive.run_Motor_angle_locked_relative_to_driver(gamepad1.right_stick_x, -gamepad1.right_stick_y);

            } else {
                rotation = gamepad1.left_stick_x;
                mecanumDrive.run_Motors_no_encoder(gamepad1.right_stick_x, -gamepad1.right_stick_y, rotation);
            }


            telemetry.addData("IMU angle  :", mecanumDrive.IMU_getAngle());
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
