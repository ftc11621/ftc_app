package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "BaseNavigation Red Alliance Example", group = "TestCode")
// @Disabled
public class BaseNavigation_example extends BaseNavigation
{
    @Override

    protected void navigate() {

        flickJewel(true);                 // for Red alliance
        offBalancingStone(true,true);     //  Red alliance on the left side
    }

}
