package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Locale;

import Library.VuforiaNavigation;

@TeleOp(name = "Vuforia test Up Side Down", group = "TestCode")
public class TeleopV1_vuforia_upside_down extends LinearOpMode
{
    private VuforiaNavigation vuforia_test = null;
    private double cos45degree             = 0.707; // cos(45) or sin(45)

    public void runOpMode() throws InterruptedException
    {
        vuforia_test = new VuforiaNavigation(true, true);  // true=extended Tracking of a target picture

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
                //telemetry.addData("Y2(inch): ", formatDouble(vuforia_test.getY_vuforia() / 25.4));

                double signofX = Math.signum(vuforia_test.getOrientation());
                double anglerad = Math.toRadians(vuforia_test.getOrientation());

                telemetry.addData("45-degree X (inch): ", formatDouble(signofX*(vuforia_test.getY() + signofX * vuforia_test.getX())*cos45degree / 25.4));
                telemetry.addData("45-degree Y (inch): ", formatDouble((vuforia_test.getY() - signofX * vuforia_test.getX())*cos45degree / 25.4));

                telemetry.addData("X angle adjustment: ", formatDouble(vuforia_test.X_coordinate_mm/ 25.4));
                telemetry.addData("Y angle adjustment: ", formatDouble(vuforia_test.Y_coordinate_mm / 25.4));



                //telemetry.addData("Distance to Relic Template (inch): ",
                //        formatDouble(vuforia_test.getDestinationDistance_mm(0, 0) / 25.4));
                telemetry.addData("Robot orientation (degree): ",
                        formatDouble( vuforia_test.getOrientation()));
                //telemetry.addData("Angle to Relic Template: ",
                //        formatDouble( vuforia_test.getRobotNeedToTurnAngle(0, 0)));
                telemetry.addData("Crytobox column offset: ", vuforia_test.crytobox_offset_inch); // 0,1,2 for L,C,R


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
