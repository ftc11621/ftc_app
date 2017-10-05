package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "BaseNavigation Flicker", group = "TestCode")
// @Disabled
public class BaseNavigation_example extends BaseNavigation
{
    @Override

    protected void navigate() {

        runFlicker(true);                 // for Red alliance

        //getOutofBalancingStone(true);     //  Red alliance on the left side

        //goTo_Red_left_Crytobox();

    }

}
