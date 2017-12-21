package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Blue Alliance Right Side", group = "Competition")
//@Disabled
public class Blue_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        // Ultrasonic Range sensor
        double X_distance = 60.0 - 8.5;  // or 24.0-8.5;  if to Balance board to the left wall minus sensor offset
        double Y_distance = 10.0;        // to the wall facing crytobox

        double X_target_inch = 45.0;
        double Y_target_inch =  7.0;
        double phone_X_offset_inch = -9.0;
        double phone_Y_offset_inch = 6.0;

        robotInitial(false,false);

        flickJewel();

        get_off_Balancing_Stone();

        Spin_locked_angle(0.0, 10.0);        // facing crytobox

        Glyph_Deposit();

    }

}