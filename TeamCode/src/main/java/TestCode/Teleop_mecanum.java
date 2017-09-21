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

        mecanumDrive.Start(0.0f);       // to start mecanum and its IMU, robot point toward the driver

        while(opModeIsActive())
        {
            //if (gamepad1.x) { // lock the current orientation
            //    is_angle_locked = true;
            //    mecanumDrive.set_current_angle_locked();
            //}
            if (gamepad1.b) {  // reset 0-degree, typically the robot facing the driver
                mecanumDrive.setCurrentAngle(0.0f);
            }
            if (gamepad1.a) {   // make the robot to stay on angle=0 degree
                mecanumDrive.set_angle_locked(0.0f);    // to stay on 0-degree toward crytobox
                //is_angle_locked = true;
            }
            if (gamepad1.y) {   // make the robot to stay on angle=180 degree toward glyph
                mecanumDrive.set_angle_locked(180.0f);    // to stay on 0-degree
                //is_angle_locked = true;
            }

            // disable angle lock when the left joystick adjust angle
            //if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.2) {
            //    is_angle_locked = false;
            //}

            // robot points where the left joystick points to
            if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.5) {
                float angle_robot = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_y, (double) gamepad1.left_stick_x));
                mecanumDrive.set_angle_locked(angle_robot);
            }

            // always locked to an orientation
            mecanumDrive.run_Motor_angle_locked(gamepad1.right_stick_x, -gamepad1.right_stick_y);

            telemetry.addData("IMU angle  :", mecanumDrive.IMU_getAngle());
            telemetry.addData("Robot angle:", mecanumDrive.getRobotAngle());
            telemetry.update();
            // Experimental driving it relative to the driver X-Y instead of the robot X-Y
            //mecanumDrive.run_Motor_angle_locked_relative_to_driver(gamepad1.right_stick_x, -gamepad1.right_stick_y);

            /*
            // if locked in an angle
            if (is_angle_locked) {
                mecanumDrive.run_Motor_angle_locked(gamepad1.right_stick_x, -gamepad1.right_stick_y);
            } else {
                rotation = 0.0f;
                if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.2) {
                    rotation = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_y, (double) gamepad1.left_stick_x));
                }
                mecanumDrive.run_Motors_no_encoder(gamepad1.right_stick_x, -gamepad1.right_stick_y, rotation);
            }
            */

            idle();

        }
    }
}
