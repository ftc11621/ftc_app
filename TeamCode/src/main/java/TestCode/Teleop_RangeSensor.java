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
            telemetry.addData("Range Distance (cm): ", rangesensor.getDistance_frontLeft_inch(300));
            telemetry.update();
            idle();

        }
    }

}
