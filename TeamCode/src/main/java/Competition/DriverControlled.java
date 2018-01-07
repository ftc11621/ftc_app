package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Archives.Chassis_motors;
import Library.Glypher;
import Library.JewelServo;
import Library.Mecanum;

@TeleOp(name = "Driver Controlled", group = "Competition")
// @Disabled
public class DriverControlled extends LinearOpMode
{

    private JewelServo JewelFlicker = null;
    private Mecanum mecanumDrive = null;
    private Glypher GlypherObject = null;

    private double rotation;
    private boolean is_angle_locked = false; // if locked to the left joystick

    public void runOpMode() throws InterruptedException
    {
        mecanumDrive = new Mecanum(hardwareMap);
        GlypherObject = new Glypher(hardwareMap);
        JewelFlicker = new JewelServo(hardwareMap);


        mecanumDrive.Start();  // default to start mecanum and its IMU, robot point away from the driver

        JewelFlicker.Initial();

        waitForStart();


        while(opModeIsActive())
        {
            // Glypher Stopper ------------------------------------
            if(gamepad2.left_bumper) {
                GlypherObject.glyphstopper_close();
            } else if (gamepad2.right_bumper) {
                GlypherObject.glyphstopper_open();
            }


            // Grabber:
            GlypherObject.GrabberSetPower(gamepad2.right_stick_x);


            // Elevator:
            //GlypherObject.setElevatorPower(-gamepad2.right_stick_y * 0.1); // by power
            GlypherObject.setElevatorUpDown(0.2, (int) (-gamepad2.right_stick_y*20)); // by encoder

            telemetry.addData("Elevator position: ", GlypherObject.getElevatorPosition());
            if (gamepad2.y) {
                GlypherObject.setElevatorPosition(480);
            } else if (gamepad2.x) {
                GlypherObject.setElevatorPosition(350);
            } else if (gamepad2.b) {
                GlypherObject.setElevatorPosition(70);
            } else if (gamepad2.a) {
                GlypherObject.setElevatorPosition(0);
            }


            // Driving section -----------------------------------------
            if (gamepad1.b) {  // unlock robot orientation from left joystick. Do this before gamepad1.x below
                is_angle_locked = false;
            }

            // To do minor spin left or right
            // It automatically turns off the angle locking.
            rotation = 0.0;
            if (gamepad1.left_trigger > 0.5) {
                rotation = 0.4;
                is_angle_locked = false;
            } else if (gamepad1.right_trigger > 0.5) {
                rotation = -0.4;
                is_angle_locked = false;
            }

            // to set speed
            if(gamepad1.dpad_down) {
                mecanumDrive.set_max_power(0.05);
            } else if(gamepad1.dpad_left) {
                mecanumDrive.set_max_power(0.1);
            } else if(gamepad1.dpad_up) {
                mecanumDrive.set_max_power(0.2);
            } else if(gamepad1.dpad_right) {
                mecanumDrive.set_max_power(0.3);
            }

            if(is_angle_locked) {   // angle locked to left joystick where it points to

                if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.5) { // left stick actually points somewhere
                    float angle_lock = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_x, (double) gamepad1.left_stick_y));
                    mecanumDrive.set_angle_locked(angle_lock);
                    mecanumDrive.run_Motor_angle_locked_relative_to_driver(gamepad1.right_stick_x, gamepad1.right_stick_y);
                } else {
                    telemetry.addData("Robot angle:", mecanumDrive.getRobotAngle());
                    telemetry.addData("Angular velocity  :", mecanumDrive.get_angular_velocity());
                    mecanumDrive.run_Motor_relative_to_driver(gamepad1.right_stick_x , gamepad1.right_stick_y );
                }
            } else { // Unlock Section

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




            //telemetry.addData("Locked angle  :", mecanumDrive.get_locked_angle());
            //if (is_angle_locked) {
            //    telemetry.addData("Angle Lock: ", "Yes");
            //} else {
            //    telemetry.addData("Angle Lock: ", "No");
            //}
            telemetry.update();

            idle();

        }
    }
}
