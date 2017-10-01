package Library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Glypher
{
    // chassis and wheel settings
    Servo Booter;
    private DcMotor motorGlypher;
    private DcMotor TiltGlypher;

    public Glypher(HardwareMap hardwareMap){    // constructor to create object
        motorGlypher = hardwareMap.dcMotor.get("GlypherDrive");
        TiltGlypher = hardwareMap.dcMotor.get("GlypherTilter");
        motorGlypher.setDirection(DcMotor.Direction.REVERSE);
        Booter = hardwareMap.get(Servo.class, "ServoBooter");
    }


    public void RunGlypherMotor(double power) {
        motorGlypher.setPower(power);
    }
    public void BooterKickOut() {
        Booter.setPosition(0.5);
    }
    public void BooterRetract() {
        Booter.setPosition(0.0);
    }
    public void Tilt(double power) {
        TiltGlypher.setPower(power);
    }

} // End of run_Motors_encoder_CM ====
