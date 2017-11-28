package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Blue Alliance Right Side", group = "Competition")
//@Disabled
public class Blue_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        double X_target_inch = 45.0;
        double Y_target_inch =  7.0;
        double phone_X_offset_inch = -8.0;
        double phone_Y_offset_inch = 2.0;

        robotInitial(false,false);

        //flickJewel();

        robotMove_XY_inch(X_target_inch+phone_X_offset_inch, Y_target_inch+phone_Y_offset_inch);

        /*
        Robot_Reverse(0.7,0.3,0); // to crytobox

        Robot_Turn(1.0, 0.2, -90); // turn toward crytobox

        Robot_Glyph_Deposit();  // to deposit glyph
        */
    }

}