package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import Navigation.BaseNavigation;

@Autonomous(name = "Best Navigation Test", group = "TestCode")
//@Disabled
public class BaseNavigation_example extends BaseNavigation
{
    @Override

    protected void navigate() {

        double X_target_inch = -39.0 + 7.0;
        double Y_target_inch = 36.0;
        double phone_X_offset_inch = -9.0;
        double phone_Y_offset_inch = 6.0;

        //robotInitial(true,false);
        robotInitial(true,true);       //

        //flickJewel();                 // for Red alliance
        //offBalancingStone();     //  Red alliance on the left side

        Move_by_Distance_inch(10.0, 100.0, 100.0, 10.0);

    }

}
