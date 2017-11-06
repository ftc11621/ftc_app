package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import Navigation.BaseNavigation;

@Autonomous(name = "Red Alliance Left Side", group = "Competition")
//@Disabled
public class Red_Left_Side extends BaseNavigation
{
    @Override

    protected void navigate() {
        //flickerTest();
        robotInitial(true,true);       //
        //NavigationTest();
        flickJewel();                 // for Red alliance
        //offBalancingStone();     //  Red alliance on the left side
    }

}