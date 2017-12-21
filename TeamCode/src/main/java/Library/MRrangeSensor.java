package Library;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import static android.os.SystemClock.sleep;

public class MRrangeSensor {

    ModernRoboticsI2cRangeSensor Front, Left, Right;
    public double Distance_front, Distance_left, Distance_right;

    public MRrangeSensor(HardwareMap hardwareMap) {    // constructor to create object

        Front = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeSensor_frontLeft");
        //Left  = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeSensor_Left");
        //Right = hardwareMap.get(ModernRoboticsI2cRangeSensor.class, "rangeSensor_Right");
    }


    public boolean isFrontAvailable(double min_distance_inch, double max_distance_inch) {
        double meas_dist = Front.getDistance(DistanceUnit.INCH);

        if (meas_dist > min_distance_inch && meas_dist < max_distance_inch) {
            Distance_front = meas_dist;
            return true;
        }
        return false;
    }

    /*
    public boolean isLeftAvailable(double min_distance_inch, double max_distance_inch) {
        double meas_dist = Left.getDistance(DistanceUnit.INCH);
        if (meas_dist > min_distance_inch && meas_dist < max_distance_inch) {
            Distance_left = meas_dist;
            return true;
        }
        return false;
    }

    public boolean isRightAvailable(double min_distance_inch, double max_distance_inch) {
        double meas_dist = Right.getDistance(DistanceUnit.INCH);
        if (meas_dist > min_distance_inch && meas_dist < max_distance_inch) {
            Distance_right = meas_dist;
            return true;
        }
        return false;
    }
    */
}
