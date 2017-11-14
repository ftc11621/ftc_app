package Competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import Navigation.BaseNavigation;

@Autonomous(name = "Red Alliance Right Side", group = "Competition")
//@Disabled
public class Red_Right_Side extends BaseNavigation
{
    @Override

    protected void navigate() {

        robotInitial(true,false);

        flickJewel();

        Robot_Forward(0.7,0.3,20); // to crytobox

        Robot_Turn(1.0, 0.2, 0); // turn toward crytobox

        Robot_Glyph_Deposit();  // to deposit glyph
    }

}