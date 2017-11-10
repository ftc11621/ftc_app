package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Red Alliance Right Side", group = "Competition")
//@Disabled
public class Red_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {
        //flickerTest();
        robotInitial(true,false);       //
        //NavigationTest();
        flickJewel();                 // for Red alliance
        //offBalancingStone();     //  Red alliance on the left side
    }

}