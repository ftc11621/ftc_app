package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import Library.Chassis_motors;

@TeleOp(name = "Driver mode with chassis class", group = "TestCode")
public class TeleopV1_with_chassis_motors_class extends LinearOpMode
{

    private Chassis_motors chassis_Object = null;  // declare chassis motors object

    public void runOpMode() throws InterruptedException
    {
        chassis_Object = new Chassis_motors(hardwareMap); // create chassis motors object

        waitForStart();

        while(opModeIsActive())
        {

            // Use chassis method to run motor, without encoder
            chassis_Object.run_Motors_no_encoder(-gamepad1.left_stick_y, -gamepad1.right_stick_y);

            idle();

        }
    }
}
