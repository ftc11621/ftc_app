package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import Library.Chassis_motors;

@Autonomous(name = "Autonomous example", group = "TestCode")
public class AutonV1_with_encoder_example extends LinearOpMode
{

    private Chassis_motors chassis_Object = null;

    public void runOpMode() throws InterruptedException
    {
        chassis_Object = new Chassis_motors(hardwareMap);

        waitForStart();


        // autonomous sequence
        chassis_Object.run_Motors_encoder_CM(1.0, 57.5, 57.5, 10); sleep(2000); // forward 20 cm
        //chassis_Object.run_Motors_encoder_CM(0.8, -15, 15, 10); sleep(2000); // turn
        //chassis_Object.run_Motors_encoder_CM(0.8, 20, 20, 10); sleep(2000); // forward 20 cm
        //chassis_Object.run_Motors_encoder_CM(0.8, -15, 15, 10); // turn

        while(opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
        {
            idle();

        }
    }
}
