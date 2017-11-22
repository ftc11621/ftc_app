package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Blue Alliance Left Side", group = "Competition")
//@Disabled
public class Blue_Left_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        robotInitial(false,true);

        NavigationTest();

        //      flickJewel();
 /*
        Robot_Reverse(0.7,0.3,-20); // to crytobox

        Robot_Turn(1.0, 0.2, -180); // turn toward crytobox

        Robot_Glyph_Deposit();  // to deposit glyph
        */
    }

}