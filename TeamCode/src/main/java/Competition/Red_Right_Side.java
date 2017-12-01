package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Red Alliance Right Side", group = "Competition")
//@Disabled
public class Red_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        double X_target_inch = -39.0 + 7.0;
        double Y_target_inch = 36.0;
        // robot is parallel to the picture
        double phone_X_offset_inch = -12.0;
        double phone_Y_offset_inch = -9.0;

        robotInitial(true,false);

        //flickJewel();

        get_off_Balancing_Stone();

        vuforia_activate();         // to start vuforia

        vuforia_find_picture();
    }

}