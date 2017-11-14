package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Blue Alliance Right Side", group = "Competition")
//@Disabled
public class Blue_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        robotInitial(false,false);

        flickJewel();

        Robot_Reverse(0.7,0.3,0); // to crytobox

        Robot_Turn(1.0, 0.2, -90); // turn toward crytobox

        Robot_Glyph_Deposit();  // to deposit glyph
    }

}