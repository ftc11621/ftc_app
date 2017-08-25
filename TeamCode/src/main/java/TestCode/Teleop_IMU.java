package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Func;

import java.util.Locale;

import Library.IMU;


@TeleOp(name = "IMU test", group = "TestCode")
public class Teleop_IMU extends LinearOpMode
{
    private IMU IMU_Object = null;

    public void runOpMode() throws InterruptedException
    {
        IMU_Object = new IMU(hardwareMap);

        // Set up our telemetry dashboard
        composeTelemetry();

        waitForStart();

        IMU_Object.start();

        while(opModeIsActive())
        {
            telemetry.update();
            idle();

        }
    }
    

    void composeTelemetry() {
        telemetry.addAction(new Runnable() { @Override public void run()
        {
            IMU_Object.measure();
        }
        });

        telemetry.addLine()
                .addData("Yaw: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.get_yaw());
                    }
                });
        telemetry.addLine()
                .addData("Roll: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.get_roll());
                    }
                });
        telemetry.addLine()
                .addData("Pitch: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.get_pitch());
                    }
                });

    }
}
