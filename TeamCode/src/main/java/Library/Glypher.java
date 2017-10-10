package Library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Glypher
{
    final int TILT_DOWN_ENCODER  =                  0;    // encoder location when the glyper tilt is down
    final int TILT_UP_ENCODER =                     5000; // encoder location when the glypher is up

    Servo Booter;
    private DcMotor motorGlypher;
    private DcMotor TiltGlypher;
    private ElapsedTime glypher_runtime = new ElapsedTime();
    private double lastBooterPosition = 0.0;

    public Glypher(HardwareMap hardwareMap){    // constructor to create object

        motorGlypher = hardwareMap.dcMotor.get("GlypherDrive");
        TiltGlypher = hardwareMap.dcMotor.get("GlypherTilter");
        Booter = hardwareMap.get(Servo.class, "ServoBooter");

        motorGlypher.setDirection(DcMotor.Direction.FORWARD);
        //TiltGlypher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        TiltGlypher.setDirection(DcMotor.Direction.FORWARD);
        //TiltGlypher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    public void RunGlypherMotor(double power) {
        motorGlypher.setPower(power);
    }
    public void BooterKickOut() {
        lastBooterPosition = 0.5;
        setBooterPosition();
    }
    public void BooterRetract() {
        lastBooterPosition = 0.0;
        setBooterPosition();
    }

    public void BooterSlowKickOut() {
        lastBooterPosition += 0.001;
        setBooterPosition();
    }
    public void BooterSlowRetract() {
        lastBooterPosition -= 0.001;
        setBooterPosition();
    }
    private void setBooterPosition () {
        if (lastBooterPosition > 0.5) { lastBooterPosition = 0.5; }
        if (lastBooterPosition < 0.0) { lastBooterPosition = 0.0; }
        Booter.setPosition(lastBooterPosition);
    }

    // =============== Tilt ===========================
    public void Tilt(double power) {
        TiltGlypher.setPower(power);
    }
    public int Tilt_getCurrentEncoder() {
        return TiltGlypher.getCurrentPosition();
    }
    public void Tilt_goDown() {
        Tilt_goToEncoder(TILT_DOWN_ENCODER);
    }
    public void Tilt_goUp() {
        Tilt_goToEncoder(TILT_UP_ENCODER);
    }
    private void Tilt_goToEncoder(int encoderLocation) {
        glypher_runtime.reset();
        TiltGlypher.setTargetPosition(TILT_DOWN_ENCODER);
        while (TiltGlypher.isBusy() && glypher_runtime.seconds() < 30) { // 60 seconds timeout
            Tilt(0.4);
        }
        Tilt(0.0);
    }

}
