package Library;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ContinuousServo
{

    CRServo servocr;
    public ContinuousServo(HardwareMap hardwareMap) {    // constructor to create object
        servocr = hardwareMap.crservo.get("crservo_example");
        servocr.setPower(0.0);
    }

    public  void setPower(double power) {  // -1 to 1
        servocr.setPower(power);
    }


}
