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

        double X_target_inch = -27.0;
        double Y_target_inch =  7.0;
        double phone_X_offset_inch = 12.0;
        double phone_Y_offset_inch = -9.0;

        robotInitial(true,true);


        flickJewel();

        get_off_Balancing_Stone();

        //vuforia_activate();         // to start vuforia

        //vuforia_find_picture();

    }

}