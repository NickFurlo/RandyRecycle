package cit280.randyrecycle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class factScreen extends AppCompatActivity {

    String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        score = getIntent().getStringExtra("score");
        System.out.println("Score: " + score);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_screen);
        TextView scoreValueText = (TextView) findViewById(R.id.scoreValue);
        scoreValueText.setText(""+score);
    }

    public void nextLevel(View v){
        Intent intent = new Intent (this, GameActivity2.class);
        startActivity(intent);
    }

    //Nick
    //Saves score to leaderboard txt.
    //// TODO: 12/5/2016 find file location, make sure formatting is correct in txt. 
    //https://developer.android.com/training/basics/data-storage/files.html
    public void saveScore(View v){
        String userName = findViewById(R.id.userName).toString();
        FileOutputStream outputStream;
        File leaderboard = getFileStreamPath("leaderboard_lvl1.txt");
                if (!leaderboard.exists()) {
                    try{
            leaderboard.createNewFile();
            System.out.println("Created File at " + leaderboard.getAbsolutePath());}
            catch(IOException e){
                System.err.println("Caught IO Exception"+e.getMessage());
            }
        }
        try {
            outputStream = openFileOutput("leaderboard_lvl1.txt", Context.MODE_PRIVATE);
            outputStream.write(score.getBytes());
            outputStream.write(userName.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
