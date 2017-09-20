package Library;

public class Smoothing {

    private float smooth_coefficient;
    private float last_value;

    public Smoothing(float smoothing_coefficient, float initial_value) {
        smooth_coefficient = smoothing_coefficient;
        last_value = initial_value;
    }

    public float getSmoothValue(float InputValue) {
        last_value =  InputValue * smooth_coefficient + (1.0f - smooth_coefficient) * last_value;
        return last_value;
    }
}
