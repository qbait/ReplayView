package eu.szwiec.replayview.replay;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class SpeedButton extends AppCompatButton {

    private State mState;
    private Listener mListener;

    public SpeedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setState(State.SPEED_1);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        int next = ((mState.ordinal() + 1) % State.values().length);
        setState(State.values()[next]);
        performSpeedClick();
        return true;
    }

    private void performSpeedClick() {
        if (mListener == null) {
            return;
        }
        mListener.onSpeedChange(mState.getSpeed());
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        if (state == null) {
            return;
        }
        this.mState = state;
        setText(state.toString());

    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public enum State {
        SPEED_1(1),
        SPEED_4(4),
        SPEED_16(16),
        SPEED_32(32);

        private int mSpeed;

        State(final int speed) {
            this.mSpeed = speed;
        }

        public int getSpeed() {
            return mSpeed;
        }

        @Override
        public String toString() {
            return mSpeed + "x";
        }
    }

    public interface Listener {
        void onSpeedChange(int speed);
    }

}