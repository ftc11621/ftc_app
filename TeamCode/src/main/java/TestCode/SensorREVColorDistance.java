package TestCode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


import java.util.Locale;

import Library.REVColorDistance;

@TeleOp(name = "Test: REVColorDistance", group = "TestCode")
//@Disabled                            // Comment this out to add to the opmode list
public class SensorREVColorDistance extends LinearOpMode {

    REVColorDistance Rev_color_distance = null;

    @Override
    public void runOpMode() {

        Rev_color_distance = new REVColorDistance(hardwareMap);

        // wait for the start button to be pressed.
        waitForStart();

        while (opModeIsActive()) {

            Rev_color_distance.measure(); // measure color and distance (within 10 cm)

            telemetry.addData("Distance (cm)",
                    String.format(Locale.US, "%.02f", Rev_color_distance.getDistance_CM() ));
            telemetry.addData("Red  ", Rev_color_distance.getRed());
            telemetry.addData("Green", Rev_color_distance.getGreen());
            telemetry.addData("Blue ", Rev_color_distance.getBlue());

            telemetry.update();
            idle();
        }

    }
}
