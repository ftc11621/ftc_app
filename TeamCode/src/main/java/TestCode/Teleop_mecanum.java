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
            telemetry.addData("Encoder LFront: ", mecanumDrive.get_Encoder_value(0));

            telemetry.addData("Encoder RFront: ", mecanumDrive.get_Encoder_value(1));

            telemetry.addData("Encoder LRear: ",  mecanumDrive.get_Encoder_value(2));

            telemetry.addData("Encoder RRear: ",  mecanumDrive.get_Encoder_value(3));

            telemetry.update();

            idle();

        }
    }
}
