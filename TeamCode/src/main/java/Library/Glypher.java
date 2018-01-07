package Library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Glypher {
    final double glyphstopper_open = 0.46;
    final double glyphstopper_close = 0.69;


    private DcMotor Elevator;
    private Servo GlyphStopper;
    private ElapsedTime glypher_runtime = new ElapsedTime();
    private double lastBooterPosition = 0.0;
    private DcMotor grabber;
    private int     Elevator_init_position;

    public Glypher(HardwareMap hardwareMap) {    // constructor to create object

        Elevator = hardwareMap.dcMotor.get("GlypherElevator");
        grabber = hardwareMap.dcMotor.get("GlypherGrabber");
        GlyphStopper = hardwareMap.get(Servo.class, "GlyphStopper");

        Elevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Elevator.setDirection(DcMotor.Direction.FORWARD);
        grabber.setDirection(DcMotor.Direction.REVERSE);

        Elevator_init_position = Elevator.getCurrentPosition();
    }


    public void GrabberSetPower(double GrabberPower) {
        grabber.setPower(GrabberPower);
    }

    public void glyphstopper_open() {
        GlyphStopper.setPosition(glyphstopper_open);
    }

    public void glyphstopper_close() {
        GlyphStopper.setPosition(glyphstopper_close);
    }

    //-------Elevator-------

    public void setElevatorPower (double Elevatorpower) {
        if (Elevatorpower > 0) {  // harder to lift than lowering
            Elevatorpower *= 2.0;
        }
        Elevator.setPower(Elevatorpower);
    }

    public void setElevatorPosition(int newpos) {
        Elevator.setTargetPosition(Elevator_init_position+newpos);
        Elevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        int init_pos = Elevator.getCurrentPosition();
        glypher_runtime.reset();
        while (Elevator.isBusy() && glypher_runtime.seconds() < 5.0) {
            if ( ( Elevator_init_position+newpos) > Elevator.getCurrentPosition()) {
                Elevator.setPower(0.2);
            } else {
                Elevator.setPower(0.1);
            }
            //wait(1);
        }
        //Elevator.setPower(0.0);
        //Elevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setElevatorUpDown(double Elevatorpower, int increment_step) {
        int target_position = Elevator.getCurrentPosition()+ increment_step;
        Elevator.setTargetPosition(target_position);
        Elevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if (Elevator.getCurrentPosition() < target_position) {
            Elevator.setPower(Elevatorpower * 2.0);
        } else {
            Elevator.setPower(Elevatorpower);
        }
    }

    public int getElevatorPosition() {
        return Elevator.getCurrentPosition();
    }
}