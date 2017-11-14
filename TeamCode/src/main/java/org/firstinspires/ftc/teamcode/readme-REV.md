Servo:
    Black cable on the left side
    
    
If REV mounted vertically:
    
   byte AXIS_MAP_CONFIG_BYTE = 0x6; //This is what to write to the AXIS_MAP_CONFIG register to swap x and z axes
   byte AXIS_MAP_SIGN_BYTE = 0x1; //This is what to write to the AXIS_MAP_SIGN register to negate the z axis
   
   //Need to be in CONFIG mode to write to registers
   imu.write8(BNO055IMU.Register.OPR_MODE,BNO055IMU.S ensorMode.CONFIG.bVal & 0x0F);
   
   sleep(100); //Changing modes requires a delay before doing anything else
   
   //Write to the AXIS_MAP_CONFIG register
   imu.write8(BNO055IMU.Register.AXIS_MAP_CONFIG,AXIS _MAP_CONFIG_BYTE & 0x0F);
   
   //Write to the AXIS_MAP_SIGN register
   imu.write8(BNO055IMU.Register.AXIS_MAP_SIGN,AXIS_M AP_SIGN_BYTE & 0x0F);
   
   //Need to change back into the IMU mode to use the gyro
   imu.write8(BNO055IMU.Register.OPR_MODE,BNO055IMU.S ensorMode.IMU.bVal & 0x0F);
   
   sleep(100); //Changing modes again requires a delay
   
   Then we used the REV unit just as we had when it was horizontal, using the z axis (now remapped to x) for heading.
   
   This allowed us to get good heading data with the REV unit mounted vertically. We didn't have time to look closely at roll and pitch, but we really only care about the heading.
   
   Depending on how you turn it vertical, you need to either swap (x with z) or (y with z).
   
   The AXIS_MAP_CONFIG and AXIS_MAP_SIGN registers are documented in the BNO055 datasheet available on the Adafruit website.
   