package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Blue Alliance Left Side", group = "Competition")
//@Disabled
public class Blue_Left_Side extends BaseNavigation
{
    @Override

    protected void navigate() {
        //flickerTest();
        robotInitial(false,true);       //
        //NavigationTest();
        flickJewel();                 // for Red alliance

    }

}