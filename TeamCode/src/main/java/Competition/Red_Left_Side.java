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

        robotInitial(true,true);

        flickJewel();
/*
        Robot_Forward(0.7,0.3,0);  // to crytobox

        Robot_Turn(1.0, 0.2, -90); // turn toward crytobox

        Robot_Glyph_Deposit();  // to deposit glyph
        */
    }

}