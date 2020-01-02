package com.tenke.test.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.tenke.test.R;

public class TTSActivity extends AppCompatActivity {
    Button mButton;
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        mEditText = findViewById(R.id.text);
        mButton = findViewById(R.id.speak);

        mEditText.setOnLongClickListener(v -> {
            mEditText.setText("");
            return true;
        });
        mButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString();
//            try {
//                TTSManager.getIntance().speak(text);
//            } catch (JSONException e) {
//                Logger.e("",e);
//            }
        });
    }
}
