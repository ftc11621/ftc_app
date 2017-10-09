package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Locale;

import Library.VuforiaNavigation;

@TeleOp(name = "teleop Vuforia test", group = "TestCode")
public class TeleopV1_vuforia extends LinearOpMode
{
    private VuforiaNavigation vuforia_test = null;
    private double cos45degree             = 0.707; // cos(45) or sin(45)

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

                telemetry.addData("X (inch): ", formatDouble(vuforia_test.getX() / 25.4));
                telemetry.addData("Y (inch): ", formatDouble(vuforia_test.getY() / 25.4));
                telemetry.addData("Y2(inch): ", formatDouble(vuforia_test.getY_vuforia() / 25.4));

                double signofX = Math.signum(vuforia_test.getOrientation());

                telemetry.addData("45-degree X (inch): ", formatDouble((vuforia_test.getY() + signofX * vuforia_test.getX())*cos45degree / 25.4));
                telemetry.addData("45-degree Y (inch): ", formatDouble((vuforia_test.getY() - signofX * vuforia_test.getX())*cos45degree / 25.4));



                //telemetry.addData("Distance to Relic Template (inch): ",
                //        formatDouble(vuforia_test.getDestinationDistance_mm(0, 0) / 25.4));
                telemetry.addData("Robot orientation (degree): ",
                        formatDouble( vuforia_test.getOrientation()));
                //telemetry.addData("Angle to Relic Template: ",
                //        formatDouble( vuforia_test.getRobotNeedToTurnAngle(0, 0)));
                telemetry.addData("Crytobox column: ", vuforia_test.getCrytoboxColumn()); // 0,1,2 for L,C,R

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
        return String.format(Locale.US, "%.1f", datadouble);
    }
}
