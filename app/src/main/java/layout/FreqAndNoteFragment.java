package layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceView;

import com.rerx.alexey.audiocontrol.R;

public class FreqAndNoteFragment extends SurfaceView {

    Canvas canvas;

    private int FREQ_POSITION_X;
    private int FREQ_POSITION_Y;
    private int NOTE_POSITION_X;
    private int NOTE_POSITION_Y;
    private int note_color;
    private int freq_color;
    private Paint paint = new Paint();


    public FreqAndNoteFragment(Context context) {
        super(context);
        note_color = context.getResources().getColor(R.color.determined_note);
        freq_color = context.getResources().getColor(R.color.black);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = new Canvas();
        super.onDraw(this.canvas);
        setPositions();
        Log.d("fragment", "" + this.canvas);
    }

    private void setPositions() {
        FREQ_POSITION_X = canvas.getWidth() / 6;
        FREQ_POSITION_Y = canvas.getHeight() / 2;
        NOTE_POSITION_X = canvas.getWidth() * 4 / 6;
        NOTE_POSITION_Y = canvas.getHeight() / 2;
    }

    public void printNote(String text) {
        paint.setColor(note_color);
        canvas.drawText(text, NOTE_POSITION_X, NOTE_POSITION_Y, paint);
    }

    public void printFreq(String text) {
        paint.setColor(freq_color);
        canvas.drawText(text, FREQ_POSITION_X, FREQ_POSITION_Y, paint);
    }


}
