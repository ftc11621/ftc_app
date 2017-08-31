package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Library.Chassis_motors;
import Library.VuforiaNavigation;

@TeleOp(name = "teleop Vuforia test", group = "TestCode")
public class TeleopV1_vuforia extends LinearOpMode
{
    private VuforiaNavigation vuforia_test = null;

    public void runOpMode() throws InterruptedException
    {
        vuforia_test = new VuforiaNavigation();

        waitForStart();

        vuforia_test.activate();    // start vuforia

        while(opModeIsActive())
        {
            if(vuforia_test.isTarget_visible()) {

                telemetry.addData("Vuforia", "Visible");

                if (vuforia_test.updateRobotLocation()) {
                    telemetry.addData("Location Update:", "Yes");
                } else {
                    telemetry.addData("Location Update:", "No");
                }
                telemetry.addData("Distance to wheels: ", vuforia_test.getDestinationDistance_mm(vuforia_test.wheels_x_mm, vuforia_test.wheels_y_mm));
                telemetry.addData("Distance to tools : ", vuforia_test.getDestinationDistance_mm(vuforia_test.tools_x_mm, vuforia_test.tools_y_mm));
                telemetry.addData("Distance to legos : ", vuforia_test.getDestinationDistance_mm(vuforia_test.legos_x_mm, vuforia_test.legos_y_mm));
                telemetry.addData("Distance to gears : ", vuforia_test.getDestinationDistance_mm(vuforia_test.gears_x_mm, vuforia_test.gears_y_mm));

            } else {
                telemetry.addData("Vuforia", "NOT visible");
            }
            telemetry.update();
        }
    }
}
