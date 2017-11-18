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

    private float rotation;
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
            // Glypher section ------------------------------------
            GlypherObject.RunGlypherMotor(-gamepad2.left_stick_y);
            GlypherObject.Tilt(-gamepad2.right_stick_y);

            if (gamepad2.y) {
                GlypherObject.BooterKickOut();
            } else if (gamepad2.a) {
                GlypherObject.BooterRetract();
            }
            /*
            if (gamepad2.dpad_up) {
                GlypherObject.BooterSlowKickOut();
                GlypherObject.BooterRetract();
            } else if (gamepad2.dpad_down) {
                GlypherObject.BooterSlowRetract();
            }
            if (gamepad2.left_bumper) {
                GlypherObject.LeftIntakeIn();
                */
            if (gamepad2.left_trigger > 0.5) {
                GlypherObject.BooterKickOut();
            }else{
                GlypherObject.BooterRetract();
            }
            /*
            }  else{
                GlypherObject.StopIntakeLeft();
            }

            if (gamepad2.right_bumper) {
                GlypherObject.RightIntakeIn();
            } else if (gamepad2.right_trigger > 0.1) {
                GlypherObject.RightIntakeOut();
            }  else{
                GlypherObject.StopIntakeRight();
            }
*/

            // Driving section -----------------------------------------
            if (gamepad1.b) {  // unlock robot orientation from left joystick. Do this before gamepad1.x below
                is_angle_locked = false;
            }

            if(is_angle_locked) {   // angle locked to left joystick where it points to
                //mecanumDrive.set_max_power(0.1);
                if(gamepad1.dpad_down) {
                    mecanumDrive.set_max_power(0.05);
                } else if(gamepad1.dpad_left) {
                    mecanumDrive.set_max_power(0.1);
                } else if(gamepad1.dpad_up) {
                    mecanumDrive.set_max_power(0.2);
                } else if(gamepad1.dpad_right) {
                    mecanumDrive.set_max_power(0.3);
                }


                if(gamepad1.left_trigger > 0.8) {
                    mecanumDrive.set_max_power(0.2);
                    mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()+2);
                }
                if(gamepad1.right_trigger > 0.8) {
                    mecanumDrive.set_max_power(0.2);
                    mecanumDrive.set_angle_locked(mecanumDrive.get_locked_angle()-2);
                }

                if ((Math.abs(gamepad1.left_stick_x) + Math.abs(gamepad1.left_stick_y)) > 0.5) { // left stick actually points somewhere
                    float angle_lock = (float) Math.toDegrees(Math.atan2((double) gamepad1.left_stick_x, (double) gamepad1.left_stick_y));
                    mecanumDrive.set_angle_locked(angle_lock);
                    mecanumDrive.run_Motor_angle_locked_relative_to_driver(gamepad1.right_stick_x, gamepad1.right_stick_y);
                } else {
                    telemetry.addData("Robot angle:", mecanumDrive.getRobotAngle());
                    mecanumDrive.run_Motor_relative_to_driver(gamepad1.right_stick_x , gamepad1.right_stick_y );
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