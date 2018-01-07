package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import Library.MRrangeSensor;
import Library.RangeSensor;


@TeleOp(name = "Range Sensor test", group = "TestCode")
//@Disabled
public class Teleop_RangeSensor extends LinearOpMode
{
    //private RangeSensor rangesensor = null;
    private MRrangeSensor rangesensorREV = null;

    public void runOpMode() throws InterruptedException
    {
        //rangesensor = new RangeSensor(hardwareMap);
        rangesensorREV = new MRrangeSensor(hardwareMap);

        waitForStart();

       //rangesensor.Engage_all();       // turn all range sensor on

        while(opModeIsActive()) {
            //if (rangesensorREV.isFrontAvailable(0.0,20.0)) {
                rangesensorREV.isFrontAvailable(0.0,200.0);
                telemetry.addData("Front Distance (inch): ", rangesensorREV.Distance_front);
            //}

            /*
            if (rangesensorREV.isLeftAvailable(0.0, 20.0)) {
                telemetry.addData("Left Distance (inch): ", rangesensorREV.Distance_left);
            }

            if (rangesensorREV.isRightAvailable(0.0, 20.0)) {
                telemetry.addData("Right Distance (inch): ", rangesensorREV.Distance_right);
            }
            */

            //telemetry.addData("Front Left Distance (cm): ", rangesensor.getDistance_frontLeft_inch(0,200));
            //telemetry.addData("Right Distance (cm): ", rangesensor.getDistance_Right_inch(200));
            //telemetry.addData("Left Distance (cm): ", rangesensor.getDistance_Left_inch(200));


            telemetry.update();
            idle();

        }
    }

}
