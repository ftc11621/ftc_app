package Library;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


public class Glypher {
    final int DOWN_ENCODER = 0;    // encoder location when the glyper tilt is down
    final int MID_ENCODER  = 2500;
    final int UP_ENCODER   = 5000; // encoder location when the glypher is up


    //private Servo Booter;
    //private Servo LeftIntake;
    //private Servo RightIntake;
    private DcMotor motorGlypher;
    private DcMotor TiltGlypher;
    private DcMotor Elevator;
    private ElapsedTime glypher_runtime = new ElapsedTime();
    private double lastBooterPosition = 0.0;
    private DcMotor grabber;
    private int     Elevator_init_position;

    public Glypher(HardwareMap hardwareMap) {    // constructor to create object

        Elevator = hardwareMap.dcMotor.get("GlypherElevator");
        grabber = hardwareMap.dcMotor.get("GlypherGrabber");

        Elevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        Elevator.setDirection(DcMotor.Direction.FORWARD);
        grabber.setDirection(DcMotor.Direction.REVERSE);

        Elevator_init_position = Elevator.getCurrentPosition();
    }


    public void GrabberSetPower(double GrabberPower) {
        grabber.setPower(GrabberPower);
    }


    //-------Elevator-------

    public void setElevatorPower (double Elevatorpower) {
        double ElevatorPower_max = 0.1;
        if (Elevatorpower > 0) {  // harder to lift than lowering
            Elevatorpower *= 3.0;
        }
        Elevator.setPower(Elevatorpower * ElevatorPower_max);
    }

    public void setElevatorPosition(int newpos) {
        Elevator.setTargetPosition(Elevator_init_position+newpos);
        Elevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        int init_pos = Elevator.getCurrentPosition();
        glypher_runtime.reset();
        while (Elevator.isBusy() && glypher_runtime.seconds() < 5.0) {
            if ( ( Elevator_init_position+newpos) > init_pos) {
                Elevator.setPower(0.3);
            } else {
                Elevator.setPower(0.1);
            }
            //wait(1);
        }
        Elevator.setPower(0.0);
        Elevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Elevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    public int getElevatorPosition() {
        return Elevator.getCurrentPosition();
    }
}