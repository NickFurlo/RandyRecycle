package cit280.randyrecycle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class factScreen3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_screen_3);


    }

    public void nextLevel(View v){
        Intent intent = new Intent (this, GameActivity4.class);
        startActivity(intent);
    }
}