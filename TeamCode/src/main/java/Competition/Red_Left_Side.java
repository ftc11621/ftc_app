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
        double phone_X_offset_inch = -9.0;
        double phone_Y_offset_inch = 6.0;

        robotInitial(true,true);


        flickJewel();

        get_off_Balancing_Stone();


        // ========= Vuforia if it works
        //Spin_locked_angle(-45.0, 10.0);     // get close to pointing to a picture
        //if (vuforia_find_picture()) {       // if the picture is found
        //vuforia_Move_XY_inch_point_picture(X_target_inch+phone_X_offset_inch, Y_target_inch+phone_Y_offset_inch, 5.0);
        //}

        Spin_locked_angle(0.0, 10.0);        // facing crytobox

        Glyph_Deposit();
    }

}