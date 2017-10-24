package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Red Alliance Left Side", group = "TestCode")
// @Disabled
public class BaseNavigation_example extends BaseNavigation
{
    @Override

    protected void navigate() {

        robotInitial(true,true);       //
        flickJewel();                 // for Red alliance
        //offBalancingStone();     //  Red alliance on the left side
    }

}
