package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import Library.RangeSensor;


@TeleOp(name = "Range Sensor test", group = "TestCode")
public class Teleop_RangeSensor extends LinearOpMode
{
    private RangeSensor rangesensor = null;

    public void runOpMode() throws InterruptedException
    {
        rangesensor = new RangeSensor(hardwareMap);

        waitForStart();


        while(opModeIsActive())
        {
            telemetry.addData("Front Left Distance (cm): ", rangesensor.getDistance_frontLeft_inch(200));
            telemetry.addData("Right Distance (cm): ", rangesensor.getDistance_Right_inch(200));
            telemetry.addData("Left Distance (cm): ", rangesensor.getDistance_Left_inch(200));


            telemetry.update();
            idle();

        }
    }

}
