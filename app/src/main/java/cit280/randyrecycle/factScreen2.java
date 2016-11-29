package cit280.randyrecycle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class factScreen2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_screen_2);


    }

    public void nextLevel(View v){
        Intent intent = new Intent (this, GameActivity3.class);
        startActivity(intent);
    }
}