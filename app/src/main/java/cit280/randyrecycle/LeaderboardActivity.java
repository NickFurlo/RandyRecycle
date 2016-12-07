package cit280.randyrecycle;
//Nick furlo
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private String filePath ="leaderboard_lvl1.txt";
    private boolean filePathSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Leaderboard");

        //force portrait orientation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //populate leaderboard
        displayLeaderboard();
    }
    //Nick with help from http://stackoverflow.com/questiooutns/14376807/how-to-read-write-string-from-a-file-in-android
    //converts leaderboard txt to string.
    private String readFromFile(Context context, String filePath) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filePath);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString+"\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Leaderboard: File not found: " + e.toString());
        } catch (IOException e) {
            System.err.println("Leaderboard: Can not read file: " + e.toString());
        }

        return ret;
    }

    private void displayLeaderboard(){
        TextView leaderboardText = (TextView)findViewById(R.id.leaderboardText);
        //sortLeaderboard();
        leaderboardText.setText(readFromFile(this, filePath));
    }

    //nick with help from http://stackoverflow.com/questions/12857242/java-create-string-array-from-text-file
    //// TODO: 12/6/2016 Fix leaderboard sorter.
    private void sortLeaderboard(){
        String[] scores= null;
        List<String> scoresList = new ArrayList<String>();

        //put each line of leaderboard txt into arraylist
        try
        {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            DataInputStream data_input = new DataInputStream(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data_input));
            String record;

            while ((record = bufferedReader.readLine()) != null)
            {
                record = record.trim();
                if ((record.length()!=0))
                {
                    scoresList.add(record);
                }
            }

            scores = (String[])scoresList.toArray(new String[scoresList.size()]);
        }catch(Exception e){
            System.err.println("Caught Exception: "+e.getMessage());
        }
        //sort array numerically
        Arrays.sort(scores);
        System.out.println(Arrays.toString(scores));

        //write arraylist to txt file
        try {
            FileWriter writer = new FileWriter(filePath);
            for (String str : scores) {
                writer.write(str);
            }
            writer.close();
        }catch(Exception e){
            System.err.println("Caught Exception: "+e.getMessage());
        }
    }


    //sets filePath depending on what button user clicks in leaderboard

    public void setFilePath(View v){
        switch(v.getId()){
            case R.id.lvl1:
                filePath = "leaderboard_lvl1.txt";
                filePathSet = true;
                displayLeaderboard();
            break;
            case R.id.lvl2:
                filePath = "leaderboard_lvl2.txt";
                filePathSet = true;
                displayLeaderboard();
                break;
            case R.id.lvl3:
                filePath = "leaderboard_lvl3.txt";
                filePathSet = true;
                displayLeaderboard();
                break;
            case R.id.lvl4:
                filePath = "leaderboard_lvl4.txt";
                filePathSet = true;
                displayLeaderboard();
                break;
            case R.id.lvl5:
                filePath = "leaderboard_lvl5.txt";
                filePathSet = true;
                displayLeaderboard();
                break;
        }
    }

}
