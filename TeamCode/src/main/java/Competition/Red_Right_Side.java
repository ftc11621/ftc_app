package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Ultra Sonic Red Alliance Right Side", group = "Competition")
//@Disabled
public class Red_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        // Ultrasonic Range sensor
        double X_distance = 36.0 - 8.5;  // to the left wall minus sensor offset
        double Y_distance = 10.0;        // to the wall facing crytobox

        double X_target_inch = -39.0 + 7.0;
        double Y_target_inch = 36.0;
        double phone_X_offset_inch = -9.0;
        double phone_Y_offset_inch = 6.0;

        robotInitial(true,false);

        flickJewel();

        get_off_Balancing_Stone();

        // Move forward to a distance in inch
        // Move_by_Distance_inch(10.0, 100.0, 100.0, 10.0);


        // ========= Vuforia if it works
        //Spin_locked_angle(-45.0, 10.0);     // get close to pointing to a picture
        //if (vuforia_find_picture()) {       // if the picture is found
        //vuforia_Move_XY_inch_point_picture(X_target_inch+phone_X_offset_inch, Y_target_inch+phone_Y_offset_inch, 5.0);
        //}

        Spin_locked_angle(-45.0, 10.0);      // facing crytobox 4-steps
        Spin_locked_angle(0.0, 10.0);        // facing crytobox
        Spin_locked_angle(45.0, 10.0);       // facing crytobox
        Spin_locked_angle(90.0, 10.0);        // facing crytobox

        Glyph_Deposit();

    }

}