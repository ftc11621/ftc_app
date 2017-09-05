package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import Library.Chassis_motors;
import Navigation.BaseNavigation;

@Autonomous(name = "BaseNavigation example", group = "TestCode")
public class BaseNavigation_example extends BaseNavigation
{
    @Override

    protected void navigate() {
        moveEncoderDistance(50.0);
    }

}
