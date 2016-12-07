package cit280.randyrecycle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class factScreen extends AppCompatActivity {

    private String score;
    private String health;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        score = getIntent().getStringExtra("score");
        health = getIntent().getStringExtra("health");
        System.out.println("Score: " + score);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fact_screen);
        TextView scoreValueText = (TextView) findViewById(R.id.scoreValue);
        scoreValueText.setText("" + score);

        //force portrait orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void nextLevel(View v) {
        Intent intent = new Intent(this, GameActivity2.class);
        startActivity(intent);
    }

    //Nick
    //Saves score to leaderboard txt.
    //https://developer.android.com/training/basics/data-storage/files.html
    //http://stackoverflow.com/questions/4542318/android-append-text-file
    public void saveScore(View v) {
        String filePath = "leaderboard_lvl1.txt";
        TextView userNameTV = (TextView) findViewById(R.id.userName);
        String userName = userNameTV.getText().toString();
        File leaderboard = getFileStreamPath(filePath);

        if (!leaderboard.exists()) {
            try {
                leaderboard.createNewFile();
                FileOutputStream fileOut = openFileOutput(filePath, MODE_APPEND);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
                outputWriter.append("Level 1\n"+"Score   Health   Username\n");
                System.out.println("Created File at " + leaderboard.getAbsolutePath());
                outputWriter.close();
            } catch (IOException e) {
                System.err.println("Caught IO Exception" + e.getMessage());
            }
        }
        try {
            //http://www.androidinterview.com/android-internal-storage-read-and-write-text-file-example/
            FileOutputStream fileOut = openFileOutput(filePath, MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.append(score.toString() + "           " + health + "           " + userName + "\n");
            outputWriter.close();
            System.out.println("Edited File at " + leaderboard.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Caught IO Exception" + e.getMessage());
        }

    }
}
