package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Blue Alliance Right Side", group = "Competition")
//@Disabled
public class Blue_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {
        //flickerTest();
        robotInitial(false,false);       //
        //NavigationTest();
        flickJewel();                 // for Red alliance

    }

}