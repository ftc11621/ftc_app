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

        // example Red left side
        double X_distance = 60.0 - 9.0;  // to the left wall minus sensor offset
        double Y_distance = 10.0;        // to the wall facing crytobox


        //robotInitial(true,false);
        robotInitial(true, true);       //

        //flickJewel();                 // for Red alliance
        //offBalancingStone();     //  Red alliance on the left side

        Move_to_Distance_inch(Y_distance, 0.0, X_distance, 10.0);

    }

}
