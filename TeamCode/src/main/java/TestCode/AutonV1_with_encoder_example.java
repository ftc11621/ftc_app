package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Archives.Chassis_motors;

@Autonomous(name = "Autonomous example", group = "TestCode")
@Disabled
public class AutonV1_with_encoder_example extends LinearOpMode
{

    private Chassis_motors chassis_Object = null;

    public void runOpMode() throws InterruptedException
    {
        chassis_Object = new Chassis_motors(hardwareMap);

        waitForStart();


        // autonomous sequence
        chassis_Object.run_Motors_encoder(0.5, 57.5, 57.5, 10, 2.0); sleep(2000); // forward 20 cm
        double actual_left_distance =chassis_Object.getLeftDistance_cm();
        double actual_right_distance =chassis_Object.getRightDistance_cm();
        telemetry.addData("Left  Actual Distance: ", actual_left_distance);
        telemetry.addData("Right Actual Distance: ", actual_right_distance);
        telemetry.update();
        //chassis_Object.turn_encoder_degree(1.0, 90, 10); sleep(2000); // turn 90 degree
        //chassis_Object.run_Motors_encoder_CM(0.8, -15, 15, 10); sleep(2000); // turn
        //chassis_Object.run_Motors_encoder_CM(0.8, 20, 20, 10); sleep(2000); // forward 20 cm
        //chassis_Object.run_Motors_encoder_CM(0.8, -15, 15, 10); // turn

        while(opModeIsActive()) // after autonomous is done wait for manual stop or stop after the timer
        {
            idle();

        }
    }
}
