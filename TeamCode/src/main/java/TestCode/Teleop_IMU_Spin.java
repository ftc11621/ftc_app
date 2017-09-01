package TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Func;

import java.util.Locale;

import Library.Chassis_motors;
import Library.IMU;


@TeleOp(name = "IMU Auto Spin test", group = "TestCode")
public class Teleop_IMU_Spin extends LinearOpMode
{
    private IMU IMU_Object = null;
    private Chassis_motors chassis_Object = null;

    private final double angle_to_spin = 90.0;

    public void runOpMode() throws InterruptedException
    {
        IMU_Object = new IMU(hardwareMap);
        chassis_Object = new Chassis_motors(hardwareMap);

        composeTelemetry();

        waitForStart();

        IMU_Object.start();

        IMU_Object.measure();
        double angle_difference = IMU_Object.yaw() - angle_to_spin;
        double power = 1.0;

        while(opModeIsActive())
        {
            //chassis_Object.run_Motors_no_encoder(power, -power);

            IMU_Object.measure();
            angle_difference = IMU_Object.yaw() - angle_to_spin;

            power = angle_difference/360.0;

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
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.yaw());
                    }
                });
        telemetry.addLine()
                .addData("Roll: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.roll());
                    }
                });
        telemetry.addLine()
                .addData("Pitch: ", new Func<String>() {
                    @Override
                    public String value() {
                        return String.format(Locale.getDefault(), "%.1f", IMU_Object.pitch());
                    }
                });

    }
}
