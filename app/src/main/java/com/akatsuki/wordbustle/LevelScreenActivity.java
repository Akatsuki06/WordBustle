package com.akatsuki.wordbustle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class LevelScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_screen);
        final Context context=this;
        Button mediocrelevel = (Button) findViewById(R.id.btn_mediocre);
        Button veteranlevel = (Button) findViewById(R.id.btn_veteran);
        Button btnHighScore = (Button) findViewById(R.id.btn_highscore);
        ImageButton info = (ImageButton)findViewById(R.id.information);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_info, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        mediocrelevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WordCollection.LEVEL_TIMER = WordCollection.LEVEL_MEDIOCRE_TIMER;
                startActivityForResult(new Intent(LevelScreenActivity.this, PlayActivity.class), 0);
            }
        });

        veteranlevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordCollection.LEVEL_TIMER = WordCollection.LEVEL_VETERAN_TIMER;
                startActivityForResult(new Intent(LevelScreenActivity.this, PlayActivity.class), 1);
            }
        });

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_highscore, null);
        alertDialog.setView(view);
        final AlertDialog dialog = alertDialog.create();
        btnHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                TextView V_hs1, V_hs2, M_hs1, M_hs2;//high scores
                TextView V_s1, V_s2, M_s1, M_s2;//scorers

                V_hs1 = (TextView) view.findViewById(R.id.highscore_veteran1);
                V_hs2 = (TextView) view.findViewById(R.id.highscore_veteran2);
                M_hs1 = (TextView) view.findViewById(R.id.highscore_mediocre1);
                M_hs2 = (TextView) view.findViewById(R.id.highscore_mediocre2);

                V_s1 = (TextView) view.findViewById(R.id.highscorer_veteran1);
                V_s2 = (TextView) view.findViewById(R.id.highscorer_veteran2);
                M_s1 = (TextView) view.findViewById(R.id.highscorer_mediocre1);
                M_s2 = (TextView) view.findViewById(R.id.highscorer_mediocre2);
                int scores[] = getHighScores("VETERAN");
                V_hs1.setText(scores[0] + "");
                V_hs2.setText(scores[1] + "");
                String scorers[] = getHighScorer("VETERAN");
                V_s1.setText(scorers[0]);
                V_s2.setText(scorers[1]);

                scores = getHighScores("MEDIOCRE");
                M_hs1.setText(scores[0] + "");
                M_hs2.setText(scores[1] + "");
                scorers = getHighScorer("MEDIOCRE");
                M_s1.setText(scorers[0]);
                M_s2.setText(scorers[1]);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final int SCORE = data.getIntExtra("SCORE", 0);
        final String LEVEL = data.getStringExtra("LEVEL");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_gameover, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        final EditText userName = (EditText) view.findViewById(R.id.scoreText);
        final Button submit = (Button) view.findViewById(R.id.scoreSubmit);
        final TextView scoreLabel = (TextView) view.findViewById(R.id.scoreLabel);
        scoreLabel.setText("" + SCORE);

        if (getCategory(SCORE, LEVEL) == 0 || SCORE == 0) {
            submit.setText("OK");
            userName.setVisibility(View.INVISIBLE);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                if ((username.trim()).equals(""))
                    username = "USER";

                if (username.length() > 7) username=username.substring(0, 7);
                saveHighScore(username, SCORE, LEVEL);//
                alertDialog.cancel();

            }
        });
    }

    private void saveHighScore(String scorer, int score, String LEVEL) {
        int cat = getCategory(score, LEVEL);
        if (cat == 0) return;//no medal
        if (scorer == null) return;
        SharedPreferences prefs = this.getSharedPreferences("SCORES_MEDIOCRE", MODE_PRIVATE);
        if (LEVEL.equals("VETERAN")) {
            prefs = this.getSharedPreferences("SCORES_VETERAN", MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = prefs.edit();
        if (cat == 1) {//goldmedal, put old score to score2 and new score to score 1
            int oldscore = (getHighScores(LEVEL))[0];
            String oldscorer = (getHighScorer(LEVEL))[0];
            editor.putString("SCORER1", scorer);
            editor.putInt("HIGHSCORE1", score);
            editor.putString("SCORER2", oldscorer);
            editor.putInt("HIGHSCORE2", oldscore);

        } else if (cat == 2) {//silver medal
            editor.putString("SCORER2", scorer);
            editor.putInt("HIGHSCORE2", score);
        }
        editor.commit();

    }


    private int[] getHighScores(String LEVEL) {
        SharedPreferences prefs = this.getSharedPreferences("SCORES_MEDIOCRE", MODE_PRIVATE);
        if (LEVEL.equals("VETERAN")) {
            prefs = this.getSharedPreferences("SCORES_VETERAN", MODE_PRIVATE);
        }
        int arr[] = {prefs.getInt("HIGHSCORE1", 0), prefs.getInt("HIGHSCORE2", 0)};
        return arr;
    }

    private String[] getHighScorer(String LEVEL) {
        SharedPreferences prefs = this.getSharedPreferences("SCORES_MEDIOCRE", MODE_PRIVATE);
        if (LEVEL.equals("VETERAN")) {
            prefs = this.getSharedPreferences("SCORES_VETERAN", MODE_PRIVATE);
        }
        String arr[] = {prefs.getString("SCORER1", "N/A"), prefs.getString("SCORER2", "N/A")};
        return arr;
    }


    private int getCategory(int score, String LEVEL) {
        int highscore[] = getHighScores(LEVEL);
        if (score > highscore[0]) return 1;//gold medal
        else if (score > highscore[1]) return 2;//silver medal
        return 0;//no medal
    }
}
