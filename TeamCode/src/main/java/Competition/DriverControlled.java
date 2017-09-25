package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Archives.Chassis_motors;

@TeleOp(name = "Driver Controlled", group = "Competition")
@Disabled
public class DriverControlled extends LinearOpMode
{
    private Chassis_motors chassis_Object = null;  // declare chassis motors object

    public void runOpMode() throws InterruptedException
    {
        chassis_Object = new Chassis_motors(hardwareMap); // create chassis motors object




        waitForStart();

        while(opModeIsActive())
        {

            // Joystick chassis motor control
            chassis_Object.run_Motors_no_encoder(-gamepad1.left_stick_y, -gamepad1.right_stick_y);

            idle();

        }
    }
}
