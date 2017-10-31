package com.akatsuki.wordbustle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class PlayActivity extends AppCompatActivity {
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private TextView bufferScreen;
    private ProgressBar timerProgressBar;
    private String stringBuffer;
    private String currentWord;
    private String tempWord[] = new String[WordCollection.WORD.length];
    private int wordNumber = 0;//nth word from the array;
    private int bufferLength = 0;//the textView buffer
    private int SCORE = 0;
    private final long initialTime = System.currentTimeMillis();
    private long currentTime;
    private long elapsedTime;
    private int userTime = WordCollection.LEVEL_TIMER;//time in seconds given to user to answer the questions
    private MediaPlayer music;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        resetGame();
        changeBackground(this);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        bufferScreen = (TextView) findViewById(R.id.viewscreen);
        timerProgressBar = (ProgressBar) findViewById(R.id.timerProgressBar);
        musicPlay(this);
        timerProgressBar.setProgress(0);
        timerTask();
        newWord();
        timerProgressBar.setMax(userTime);//each second
        bufferScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bufferScreen.setText("");
                newWord();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bufferScreen.setTextColor(Color.WHITE);
                stringBuffer += btn1.getText();
                bufferCheck();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bufferScreen.setTextColor(Color.WHITE);
                stringBuffer += btn2.getText();
                bufferCheck();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bufferScreen.setTextColor(Color.WHITE);
                stringBuffer += btn3.getText();
                bufferCheck();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bufferScreen.setTextColor(Color.WHITE);
                stringBuffer += btn4.getText();
                bufferCheck();
            }
        });
    }

    //generates a new word abiding various conditions
    private void newWord() {
        defaultState();
        int random_id_String = 0;
        int random_id_Char[] = new int[4];
        Random random = new Random();
        if (wordNumber == WordCollection.NO_OF_WORDS) return;//quit if all word list is complete;
        wordNumber++;
        //in case the word has already occured
        while (currentWord.equals("")) {
            random_id_String = random.nextInt(WordCollection.NO_OF_WORDS);
            currentWord = tempWord[random_id_String];
        }
        currentWord.toUpperCase();
        tempWord[random_id_String] = "";//so that this word will not be used again in the game session
        //generates 4 random nums each for each btn char
        for (int i = 0; i < 4; i++) {
            random_id_Char[i] = (random.nextInt(4));
            for (int j = i - 1; j >= 0; j--) {
                if (random_id_Char[i] == random_id_Char[j]) i--;
            }
        }

        btn1.setText((currentWord.charAt(random_id_Char[0]) + "").toUpperCase());
        btn2.setText((currentWord.charAt(random_id_Char[1]) + "").toUpperCase());
        btn3.setText((currentWord.charAt(random_id_Char[2]) + "").toUpperCase());
        btn4.setText((currentWord.charAt(random_id_Char[3]) + "").toUpperCase());
    }

    //each new word have to be initiated with a new default state
    private void defaultState() {
        stringBuffer = "";
        bufferLength = 0;
        currentWord = "";
    }

    //check the buffer data and on basis of its validity highlight the buffer text
    private void bufferCheck() {
        bufferScreen.setText(stringBuffer);
        bufferLength++;
        if (bufferLength == 4) {
            if (isCorrectWord()) {
                SCORE++;
                bufferScreen.setTextColor(Color.GREEN);
            } else bufferScreen.setTextColor(Color.RED);
            newWord();
        }
    }

    private boolean isCorrectWord() {
        if (stringBuffer.equalsIgnoreCase(currentWord)) return true;
        return false;
    }

    private void timerTask() {
        final TextView counts = (TextView) findViewById(R.id.counts);
        countDownTimer = new CountDownTimer(userTime * 1000, 5) {
            @Override
            public void onTick(long millisUntilFinished) {

                currentTime = System.currentTimeMillis();
                elapsedTime = currentTime - initialTime;
                timerProgressBar.setProgress((int) elapsedTime / 1000);
                String countDown = "" + ((int) ((millisUntilFinished / 1000) + 1));
                counts.setText(countDown);
            }

            @Override
            public void onFinish() {
                sendDataBack();
            }
        }.start();
    }

    //to reset the whole activity on restart
    private void resetGame() {
        SCORE = 0;
        defaultState();
        System.arraycopy(WordCollection.WORD, 0, tempWord, 0, WordCollection.WORD.length);
    }

    private void sendDataBack() {
        Intent data = new Intent();
        data.setData(Uri.parse("" + SCORE));
        data.putExtra("SCORE", SCORE);
        if (userTime == WordCollection.LEVEL_MEDIOCRE_TIMER)
            data.putExtra("LEVEL", "MEDIOCRE");
        else if (userTime == WordCollection.LEVEL_VETERAN_TIMER)
            data.putExtra("LEVEL", "VETERAN");
        setResult(RESULT_OK, data);
        musicStop();
        finish();
    }

    void changeBackground(Context context) {
        int bgId = R.drawable.background_veteran;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.playlayout);
        if (WordCollection.LEVEL_TIMER == WordCollection.LEVEL_MEDIOCRE_TIMER)
            bgId = R.drawable.background_mediocre;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            linearLayout.setBackground(ContextCompat.getDrawable(context, bgId));
        }
        linearLayout.invalidate();
    }

    void musicPlay(Context context) {
        music = MediaPlayer.create(context, R.raw.music);

        music.setLooping(true);
        music.start();
    }
    void musicStop(){
        music.release();
    }

    @Override
    public void onBackPressed() {

        sendDataBack();
    }
}
