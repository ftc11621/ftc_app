package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import Library.JewelServo;

@TeleOp(name = "Jewel Flicker Servo Test", group = "TestCode")
public class TeleopV1 extends LinearOpMode
{
    private JewelServo JewelFlicker = null;

    public void runOpMode() throws InterruptedException
    {
        JewelFlicker = new JewelServo(hardwareMap);

        waitForStart();

        while(opModeIsActive())
        {
           if (gamepad2.dpad_left){
               JewelFlicker.LeftFlick();
           }

           if (gamepad2.dpad_right){
               JewelFlicker.RightFlick();
           }



            idle();

        }
    }
}
