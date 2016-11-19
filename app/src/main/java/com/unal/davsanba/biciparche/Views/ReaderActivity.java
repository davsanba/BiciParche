package com.unal.davsanba.biciparche.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.unal.davsanba.biciparche.Data.ActRefs;
import com.unal.davsanba.biciparche.R;

public class ReaderActivity extends AppCompatActivity {

    private TextView title;
    private TextView content;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        title = (TextView) findViewById(R.id.static_reader_title);
        content = (TextView) findViewById(R.id.static_reader_content);

        mode = getIntent().getStringExtra(ActRefs.EXTRA_CREATE_UPDATE_SHOW);

        if(mode.equals(ActRefs.EXTRA_TERMS)){
            title.setText(getString(R.string.menu_terms_and_conditions));
            content.setText(getString(R.string.terms_condition_content));
        }else{
            title.setText(getString(R.string.menu_privacy));
            content.setText(getString(R.string.privacy_content));
        }
    }
}
