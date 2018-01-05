package Library;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;
import static android.os.SystemClock.sleep;

public class RangeSensor {

    private static final int RANGE_REG_START   = 0x04; //Register to start reading
    private static final int OPTICAL_REG_START = 0x05; //Register to start reading
    private static final int RANGE_READ_LENGTH = 2; //Number of byte to read
    private I2cDevice RANGE_1, RANGE_2, RANGE_3;
    private I2cDeviceSynchImpl RANGE_1_Reader, RANGE_2_Reader, RANGE_3_Reader;
    //private I2cDeviceSynch RANGE_1_Reader, RANGE_2_Reader, RANGE_3_Reader;


    public RangeSensor(HardwareMap hardwareMap) {    // constructor to create object
        RANGE_1       = hardwareMap.i2cDevice.get("rangeSensor_frontLeft");
        //RANGE_2       = hardwareMap.i2cDevice.get("rangeSensor_Left");
        //RANGE_3       = hardwareMap.i2cDevice.get("rangeSensor_Right");

        RANGE_1_Reader= new I2cDeviceSynchImpl(RANGE_1, I2cAddr.create8bit(0x28), false);
        //RANGE_2_Reader= new I2cDeviceSynchImpl(RANGE_2, I2cAddr.create8bit(0x26), false);
        //RANGE_3_Reader= new I2cDeviceSynchImpl(RANGE_3, I2cAddr.create8bit(0x32), false);

    }

    public void Engage_Left() {
        RANGE_1_Reader.engage();
        //RANGE_2_Reader.engage();
    }

    public void Engage_Right() {
        RANGE_1_Reader.engage();
        //RANGE_3_Reader.engage();
    }

    public  void Engage_all() {
        RANGE_1_Reader.engage();
        //RANGE_2_Reader.engage();
        //RANGE_3_Reader.engage();
    }


    public double getDistance_frontLeft_inch(int min_distance_cm, int max_distance_cm) {
        return getSensorDistance_inch(min_distance_cm, max_distance_cm, RANGE_1_Reader);
    }
/*
    public double getDistance_Left_inch(int min_distance_cm, int max_distance_cm) {
        return getSensorDistance_inch(min_distance_cm, max_distance_cm, RANGE_2_Reader);
    }

    public double getDistance_Right_inch(int min_distance_cm, int max_distance_cm) {
        return getSensorDistance_inch(min_distance_cm, max_distance_cm, RANGE_3_Reader);
    }
*/

    private double getSensorDistance_inch(int min_distance, int max_distance, I2cDeviceSynchImpl reader) {
        byte[] range_Cache;

        range_Cache = reader.read(RANGE_REG_START, RANGE_READ_LENGTH);
        int dist = range_Cache[0] & 0xFF;
        int count = 0;
        while((dist > max_distance || dist == 0) && count < 11) {
            range_Cache = reader.read(RANGE_REG_START, RANGE_READ_LENGTH);
            dist = range_Cache[0] & 0xFF;
            count++;
            sleep(10);
        }
        return (double) dist / 2.54;
    }

}
