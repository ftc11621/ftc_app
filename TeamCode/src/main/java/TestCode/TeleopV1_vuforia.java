package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Locale;

import Library.Chassis_motors;
import Library.VuforiaNavigation;

@TeleOp(name = "teleop Vuforia test", group = "TestCode")
public class TeleopV1_vuforia extends LinearOpMode
{
    private VuforiaNavigation vuforia_test = null;

    public void runOpMode() throws InterruptedException
    {
        vuforia_test = new VuforiaNavigation(true);  // true=extended Tracking of a target picture

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

                telemetry.addData("Distance to Relic Template (inch): ",
                        formatDouble( vuforia_test.getDestinationDistance_mm(0, 0) / 25.4));
                telemetry.addData("Angle to Relic Template: ",
                        formatDouble( vuforia_test.getRobotNeedToTurnAngle(0, 0)));
                telemetry.addData("Crytobox side: ", vuforia_test.getCrytoboxSide());

                /*
                telemetry.addData("Distance to wheels (inch): ",
                       formatDouble( vuforia_test.getDestinationDistance_mm(vuforia_test.wheels_x_mm, vuforia_test.wheels_y_mm) / 25.4));
                telemetry.addData("Distance to tools (inch) : ",
                        formatDouble( vuforia_test.getDestinationDistance_mm(vuforia_test.tools_x_mm, vuforia_test.tools_y_mm) / 25.4));
                telemetry.addData("Distance to legos (inch): ",
                        formatDouble( vuforia_test.getDestinationDistance_mm(vuforia_test.legos_x_mm, vuforia_test.legos_y_mm) / 25.4));
                telemetry.addData("Distance to gears (inch): ",
                        formatDouble( vuforia_test.getDestinationDistance_mm(vuforia_test.gears_x_mm, vuforia_test.gears_y_mm) / 25.4));

                telemetry.addData("Angle to wheels (inch): ",
                        formatDouble( vuforia_test.getRobotNeedToTurnAngle(vuforia_test.wheels_x_mm, vuforia_test.wheels_y_mm) / 25.4));
                telemetry.addData("Angle to tools (inch) : ",
                        formatDouble( vuforia_test.getRobotNeedToTurnAngle(vuforia_test.tools_x_mm, vuforia_test.tools_y_mm) / 25.4));
                telemetry.addData("Angle to legos (inch): ",
                        formatDouble( vuforia_test.getRobotNeedToTurnAngle(vuforia_test.legos_x_mm, vuforia_test.legos_y_mm) / 25.4));
                telemetry.addData("Angle to gears (inch): ",
                        formatDouble( vuforia_test.getRobotNeedToTurnAngle(vuforia_test.gears_x_mm, vuforia_test.gears_y_mm) / 25.4));
                        */
            } else {
                telemetry.addData("Vuforia", "NOT visible");
            }
            telemetry.update();
        }
    }
    private String formatDouble (double datadouble) {
        return String.format(Locale.US, "%.0f", datadouble);
    }
}
