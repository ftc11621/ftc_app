package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import Library.Chassis_motors;
import Navigation.BaseNavigation;

@Autonomous(name = "BaseNavigation example", group = "TestCode")
@Disabled
public class BaseNavigation_example extends BaseNavigation
{
    @Override

    protected void navigate() {

        setCurrentLocation(0,0,40);     // set initial location
        aimAt_fine(10,10);
        goToXY_coarse(50,50);
    }

}
