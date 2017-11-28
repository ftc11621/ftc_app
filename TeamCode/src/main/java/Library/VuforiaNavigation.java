/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package Library;

import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.MatrixF;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

public class VuforiaNavigation  {

    // settings
    public final float mmPerInch        = 25.4f;
    float mmBotWidth       = 18 * mmPerInch;            // ... or whatever is right for your robot
    float mmFTCFieldWidth  = (12*12 - 2) * mmPerInch;   // the FTC field is ~11'10" center-to-center of the glass panels

    public final double Crytobox_red_left_mm = 27.0 * mmPerInch; // distance picture to the center of crytobox
    public final double Crytobox_red_right_x_mm = 39.0 * mmPerInch; // distance to the wall
    public final double Crytobox_red_right_y_mm = 36.0 * mmPerInch; // distance to the center of crytobox
    public final double Crytobox_blue_right_mm = 45.0 * mmPerInch; // distance picture to the center of crytobox
    public final double Crytobox_blue_left_x_mm = 57.0 * mmPerInch; // distance to the wall
    public final double Crytobox_blue_left_y_mm = 36.0 * mmPerInch; // distance to the center of crytobox

    // target location in mm
    //public final float wheels_x_mm = 12 * mmPerInch, wheels_y_mm = 71 * mmPerInch;
    //public final float tools_x_mm = -71 * mmPerInch, tools_y_mm  = 36 * mmPerInch;
    //public final float legos_x_mm = -36 * mmPerInch, legos_y_mm  = 71 * mmPerInch;
    //public final float gears_x_mm = -71 * mmPerInch, gears_y_mm = -12 * mmPerInch;


    public static final String TAG = "Vuforia VuMark Sample";
    OpenGLMatrix lastRobotLocation = null;
    VuforiaLocalizer vuforia;
    VuforiaTrackables targets = null;
    VuforiaTrackable relicTemplate  = null;

    List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
    VuforiaLocalizer.Parameters parameters;

    VectorF trans=null;
    Orientation rot=null;
    // private String      vumark;         // L M or R side
    private RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.UNKNOWN;
    public double X_coordinate_mm, Y_coordinate_mm;
    private boolean isUpsideDown = false;


    public VuforiaNavigation(boolean enableExtendedTracking, boolean is_UpsideDown) {        // constructor
        //VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AdksQ3j/////AAAAGVB9GUsSEE0BlMaVB7HcRZRM4Sv74bxusFbCpn3gwnUkr3GuOtSWhrTCHnTU/93+Im+JlrYI6///bytu1igZT48xQ6182nSTpVzJ2ZP+Q/sNzSg3qvIOMnjEptutngqB+e3mQ1+YTiDa9aZod1e8X7UvGsAJ3cfV+X/S3E4M/81d1IRSMPRPEaLpKFdMqN3AcbDpBHoqp82fAp7XWVN3qd/BRe0CAAoNsr26scPBAxvm9cizRG1WeRSFms3XkwFN6eGpH7VpNAdPPXep9RQ3lLZMTFQGOfiV/vRQXq/Tlaj/b7dkA12zBSW81MfBiXRxp06NGieFe7KvXNuu2aDyyXoaPFsI44FEGp1z/SVSEVR4"; // Insert your own key here
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        parameters.useExtendedTracking = enableExtendedTracking;        // true by default,whether to use extended tracking
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        targets = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = targets.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary since only one target

        is_UpsideDown = is_UpsideDown;

        //wheels = targets.get(0);
        //wheels.setName("wheels");

        //tools = targets.get(1);
        //tools.setName("tools");

        //legos  = targets.get(2);
        //legos.setName("legos");

        //gears  = targets.get(3);
        //gears.setName("gears");

        /** For convenience, gather together all the trackable objects in one easily-iterable collection */
        //List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        //allTrackables.addAll(targets);

        // possibly in the center for now
        /*
        OpenGLMatrix relicLocationOnField = OpenGLMatrix
                .translation(0, 0, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));


        OpenGLMatrix wheelsLocationOnField = OpenGLMatrix
                .translation(wheels_x_mm, wheels_y_mm, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));


        OpenGLMatrix legosLocationOnField = OpenGLMatrix
                .translation(legos_x_mm, legos_y_mm, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0));


        OpenGLMatrix toolsLocationOnField = OpenGLMatrix
                .translation(tools_x_mm, tools_y_mm, 0)
                .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));


        OpenGLMatrix gearsLocationOnField = OpenGLMatrix
                .translation(gears_x_mm, gears_y_mm, 0)
                .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        */

        // for phone in front, 6mm to the left
        /*
        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(0, 0, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, 0, 45, 0));  // insert phone from the left front
        */
        //AngleUnit.DEGREES, -90, 90, 0));  // -90,0,0 for the right side

        //relicTemplate.setLocation(relicLocationOnField);
        //wheels.setLocation(wheelsLocationOnField);
        //legos.setLocation(legosLocationOnField);
        //tools.setLocation(toolsLocationOnField);
        //gears.setLocation(gearsLocationOnField);

        //RobotLog.ii(TAG, "Relic Target=%s", format(relicLocationOnField));
        //RobotLog.ii(TAG, "Wheels Target=%s", format(wheelsLocationOnField));
        //RobotLog.ii(TAG, "Legos Target=%s", format(legosLocationOnField));
        //RobotLog.ii(TAG, "Tools Target=%s", format(toolsLocationOnField));
        //RobotLog.ii(TAG, "Gears Target=%s", format(gearsLocationOnField));
        //RobotLog.ii(TAG, "phone=%s", format(phoneLocationOnRobot));

        //((VuforiaTrackableDefaultListener)relicTemplate.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        //((VuforiaTrackableDefaultListener)wheels.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        //((VuforiaTrackableDefaultListener)legos.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        //((VuforiaTrackableDefaultListener)tools.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        //((VuforiaTrackableDefaultListener)gears.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);

    } // ----- end of constructor -----------------

    public void activate() {
        this.targets.activate();
    }

    public boolean isTarget_visible() {

        return ((VuforiaTrackableDefaultListener) relicTemplate.getListener()).isVisible();
        /*
        return ((VuforiaTrackableDefaultListener) wheels.getListener()).isVisible() ||
                ((VuforiaTrackableDefaultListener) legos.getListener()).isVisible() ||
                ((VuforiaTrackableDefaultListener) tools.getListener()).isVisible() ||
                ((VuforiaTrackableDefaultListener) gears.getListener()).isVisible();
                */
    }


    public boolean updateRobotLocation()  {

        vuMark = RelicRecoveryVuMark.from(relicTemplate);
        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

            OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) relicTemplate.getListener()).getPose();
            if (pose != null) {     // target location found
                trans = pose.getTranslation();
                rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                double orient = getOrientation();
                double signofX = Math.signum(orient);
                double anglerad = Math.toRadians(orient);
                double xloc = getX();
                if (isUpsideDown) {
                    xloc *= 1.0;
                }
                X_coordinate_mm = signofX*(getY()*Math.sin(anglerad) + signofX * xloc*Math.cos(anglerad));
                Y_coordinate_mm = getY()*Math.cos(anglerad) - signofX * xloc*Math.sin(anglerad);
                return true;
            }
        }
        return false;  // target not found
        /*
        OpenGLMatrix robotLocationTransform = null;
        boolean currentlocation_flag = false;

        for (VuforiaTrackable trackable : allTrackables) {  // for all target pictures
            robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                lastRobotLocation = robotLocationTransform;
                currentlocation_flag = true;
            }
        }
        return currentlocation_flag;       // when no new location found
        */
    }

    // distance the robot needs to go to a destination X-Y coordinate
    private double getDestinationDistance_mm(double destination_X, double destination_Y) {
        return Math.sqrt(Math.pow(getX()-destination_X,2) + Math.pow(getY()-destination_Y,2));
    }

    // angle > 0 when the destination is on the right side of the robot, destination in X-Y coordinate
    private double getRobotNeedToTurnAngle(double destination_X, double destination_Y) {
        double destination_from_y_axis_angle = Math.toDegrees( Math.atan2(destination_X-getX(), destination_Y-getY()));
        return  destination_from_y_axis_angle + getOrientation();
    }

    public int getCrytoboxColumn() {
        if (vuMark == RelicRecoveryVuMark.CENTER) {
            return 1;
        } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
            return 2;
        } else {
            return 0;       // pick the left one by default
        }
    }


    // ==================== Phone location ======================

    public double getX() { // robot x location
        //float[] coordinates = lastRobotLocation.getTranslation().getData();
        //return coordinates[0];
        return trans.get(0);
    }

    private double getY_vuforia() { // vuforia y location
        //float[] coordinates = lastRobotLocation.getTranslation().getData();
        //return coordinates[1];
        return trans.get(1);
    }
    public double getY() { // robot y location actually
        //float[] coordinates = lastRobotLocation.getTranslation().getData();
        //return coordinates[1];
        return -trans.get(2);
    }

    public double getXcoordinate_mm() { // location respect to picture regardless of orientation
        double orient = getOrientation();
        double signofX = Math.signum(orient);
        double anglerad = Math.toRadians(orient);
        return signofX*(getY()*Math.sin(anglerad) + signofX * getX()*Math.cos(anglerad));
    }

    public double getYcoordinate_mm() { // location respect to picture regardless of orientation
        double orient = getOrientation();
        double signofX = Math.signum(orient);
        double anglerad = Math.toRadians(orient);
        return getY()*Math.cos(anglerad) - signofX * getX()*Math.sin(anglerad);
    }

    public float getOrientation() {  // 1st, 2nd, and 3rd angle
        /*
        float orient_angle;
        switch (angleorder) {
            case 1:
                orient_angle = Orientation.getOrientation(lastRobotLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).firstAngle;
                break;
            case 2:
                orient_angle = Orientation.getOrientation(lastRobotLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).secondAngle;
                break;
            default:
                orient_angle = Orientation.getOrientation(lastRobotLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
        }
        return orient_angle;
        */
        return -rot.secondAngle;
    }

    /*
    private String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }
    */

}
