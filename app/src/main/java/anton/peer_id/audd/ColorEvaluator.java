package anton.peer_id.audd;

import android.animation.TypeEvaluator;
import android.graphics.Color;

public class ColorEvaluator implements TypeEvaluator<Integer> {

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startA, startR, startG, startB;
        int aDelta = (int) ((Color.alpha(endValue) - (startA = Color.alpha(startValue))) * fraction);
        int rDelta = (int) ((Color.red(endValue) - (startR = Color.red(startValue))) * fraction);
        int gDelta = (int) ((Color.green(endValue) - (startG = Color.green(startValue))) * fraction);
        int bDelta = (int) ((Color.blue(endValue) - (startB = Color.blue(startValue))) * fraction);
        return Color.argb(startA + aDelta, startR + rDelta, startG + gDelta, startB + bDelta);
    }
}