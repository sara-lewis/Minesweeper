package us.ait.android.minesweeper;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.support.design.widget.Snackbar;

public class MainActivity extends AppCompatActivity {

    boolean flagMode = false;
    Switch flagToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flagToggle = findViewById(R.id.switch1);
        Button btnClear = findViewById(R.id.button);
        final MineSweeperView mineSweeperView = findViewById(R.id.mineSweeperView);

        MineSweeperModel.getInstance().initializeModel();
        MineSweeperModel.getInstance().placeMines();

        flagToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be true if the switch is in the On position
                String mode;
                if(isChecked){
                    mode = getString(R.string.flag_mode);
                    flagMode = true;
                }else{
                    mode = getString(R.string.try_mode);
                    flagMode = false;
                }
                final TextView toggleInfo = findViewById(R.id.textView);
                toggleInfo.setText(getString(R.string.mode, mode));
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call function that clears game
                mineSweeperView.resetGame();
            }
        });
    }

    public boolean returnFlagMode(){
        return flagMode;
    }

    public void toggleFlagToggle(){
        flagToggle.setChecked(false);
    }

    public void showMessage(String message){
        setText(message);
        final ConstraintLayout constraintLayout = findViewById(R.id.layoutContent);
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_LONG).show();

    }

    public void setText(String message){
        final TextView toggleInfo = findViewById(R.id.textView);
        toggleInfo.setText(message);
    }

}
