package com.lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.lab.objects.Letter;
import com.lab.objects.LetterAddapter;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public final static String KEY_GUESSED_WORDS = "KEY_GUESSED_WORDS";
    public final static String KEY_START_TIME = "KEY_START_TIME";
    public final static String KEY_LETTERS_POOL = "KEY_LETTERS_POOL";
    public final static String KEY_LETTERS_WORD = "KEY_LETTERS_WORD";
    public final static String KEY_CORRECT_LETTERS = "KEY_CORRECT_LETTERS";

    private int minws_c = 0;
    private int maxws_c = 0;
    private int gamet_c = 0;

    private int minws_v;
    private int maxws_v;
    private int gamet_v;

    private String words_a[];
    private Random random = new Random();

    TextView time_left;
    TextView guessed_words;
    RecyclerView line_1;
    RecyclerView line_2;

    LetterAddapter.OnClickListener line_1_ocl;
    LetterAddapter.OnClickListener line_2_ocl;

    Letter letters_pool[];
    Letter letters_word[];

    private long startTime = 0;
    private int correct_letters = 0;
    private int words_g = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            seconds = gamet_v - seconds;
            if(seconds > 0) {
                time_left.setText(getString(R.string.n_s, seconds));
                timerHandler.postDelayed(this, 500);
            } else {
                time_left.setText(getString(R.string.n_s, 0));
                final AlertDialog ad = new AlertDialog.Builder(GameActivity.this)
                        .setMessage(getString(R.string.n_guessed_words, words_g))
                        .setPositiveButton(R.string.yes, null)
                        .setOnDismissListener(dialogInterface -> GameActivity.this.finish())
                        .show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        restoreData(savedInstanceState);

        initResources();
        initGuessedWords();
        initLines();

        if(savedInstanceState == null)
            newWord();
        Letter.null_letter = getString(R.string.null_letter);
        redrawLeters();

        startTimer();
    }

    private void restoreData(Bundle savedInstanceState){
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SettingsActivity.KEY_MIN_WORD_SIZE))
                minws_c = savedInstanceState.getInt(SettingsActivity.KEY_MIN_WORD_SIZE);
            if(savedInstanceState.containsKey(SettingsActivity.KEY_MAX_WORD_SIZE))
                maxws_c = savedInstanceState.getInt(SettingsActivity.KEY_MAX_WORD_SIZE);
            if(savedInstanceState.containsKey(SettingsActivity.KEY_GAME_TIME))
                gamet_c = savedInstanceState.getInt(SettingsActivity.KEY_GAME_TIME);
            if(savedInstanceState.containsKey(KEY_START_TIME))
                startTime = savedInstanceState.getLong(KEY_START_TIME);
            if(savedInstanceState.containsKey(KEY_LETTERS_POOL))
                letters_pool = (Letter[]) savedInstanceState.getParcelableArray(KEY_LETTERS_POOL);
            if(savedInstanceState.containsKey(KEY_LETTERS_WORD))
                letters_word = (Letter[]) savedInstanceState.getParcelableArray(KEY_LETTERS_WORD);
            if(savedInstanceState.containsKey(KEY_CORRECT_LETTERS))
                correct_letters = savedInstanceState.getInt(KEY_CORRECT_LETTERS);
        } else {
            Intent data = getIntent();
            if(data.hasExtra(SettingsActivity.KEY_MIN_WORD_SIZE))
                minws_c = data.getIntExtra(SettingsActivity.KEY_MIN_WORD_SIZE, 0);
            if(data.hasExtra(SettingsActivity.KEY_MAX_WORD_SIZE))
                maxws_c = data.getIntExtra(SettingsActivity.KEY_MAX_WORD_SIZE, 0);
            if(data.hasExtra(SettingsActivity.KEY_GAME_TIME))
                gamet_c = data.getIntExtra(SettingsActivity.KEY_GAME_TIME, 0);
        }
    }

    private void storeData(Bundle outState){
        outState.putInt(SettingsActivity.KEY_MIN_WORD_SIZE, minws_c);
        outState.putInt(SettingsActivity.KEY_MAX_WORD_SIZE, maxws_c);
        outState.putInt(SettingsActivity.KEY_GAME_TIME, gamet_c);
        outState.putLong(KEY_START_TIME, startTime);
        outState.putLong(KEY_GUESSED_WORDS, words_g);
        outState.putParcelableArray(KEY_LETTERS_POOL, letters_pool);
        outState.putParcelableArray(KEY_LETTERS_WORD, letters_word);
        outState.putInt(KEY_CORRECT_LETTERS, correct_letters);
    }

    private void startTimer(){
        if(startTime == 0)
            startTime = System.currentTimeMillis();
        time_left = findViewById(R.id.time_left);
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void initGuessedWords(){
        guessed_words = findViewById(R.id.guessed_words);
        updateGuessedWords();
    }

    private void initResources(){
        minws_v = getResources().getIntArray(R.array.min_word_size)[minws_c];
        maxws_v = getResources().getIntArray(R.array.max_word_size)[maxws_c];
        gamet_v = getResources().getIntArray(R.array.game_time)[gamet_c] * 60;
        words_a = getResources().getStringArray(R.array.words);
    }

    private void initLines(){
        line_1 = findViewById(R.id.line_1);
        line_2 = findViewById(R.id.line_2);

        LinearLayoutManager layoutManager1= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

        line_1.setLayoutManager(layoutManager1);
        line_2.setLayoutManager(layoutManager2);

        line_1_ocl = (v, pp) -> {
            if(letters_pool[pp].v) {
                int pw = 0;

                for (int i = 0; i < letters_pool.length; i++) {
                    if (!letters_word[i].v) {
                        pw = i;
                        break;
                    }
                }

                letters_word[pw].l = letters_pool[pp].l;
                letters_word[pw].p = pp;

                if(letters_pool[pp].p == pw) {
                    correct_letters++;
                }

                if(correct_letters == letters_pool.length){
                    words_g++;
                    Toast.makeText(this, R.string.guessed, Toast.LENGTH_SHORT).show();
                    updateGuessedWords();
                    newWord();
                    redrawLeters();
                }else {
                    letters_pool[pp].v = false;
                    letters_word[pw].v = true;
                    updateLeters(pp, pw);
                }
            }
        };

        line_2_ocl = (v, pw) -> {
            if(letters_word[pw].v) {
                int pp = letters_word[pw].p;

                if(letters_pool[pp].p == pw) {
                    correct_letters--;
                }

                letters_pool[pp].v = true;
                letters_word[pw].v = false;
                updateLeters(pp, pw);
            }
        };
    }

    private void updateLeters(int l1p, int l2p){
        line_1.getAdapter().notifyItemChanged(l1p);
        line_2.getAdapter().notifyItemChanged(l2p);
    }

    private void updateGuessedWords(){
        guessed_words.setText(getString(R.string.n_words, words_g));
    }

    private void redrawLeters(){
        LetterAddapter lad_1 = new LetterAddapter(letters_pool, line_1_ocl);
        LetterAddapter lad_2 = new LetterAddapter(letters_word, line_2_ocl);
        line_1.setAdapter(lad_1);
        line_2.setAdapter(lad_2);
    }

    private void newWord(){
        correct_letters = 0;
        char word[] = getRandomWord().toCharArray();
        letters_pool = new Letter[word.length];
        letters_word = new Letter[word.length];
        int p;
        for (int i = 0; i < letters_pool.length; i++) {
            do{
                p = random.nextInt(word.length);
            }while(word[p] == '0');
            letters_pool[i] = new Letter(word[p], p, true);
            letters_word[i] = new Letter();
            word[p] = '0';
        }
    }

    private String getRandomWord(){
        String word = "";
        do{
            word = words_a[random.nextInt(words_a.length)];
        }while(word.length() > maxws_v || word.length() < minws_v);
        return word;
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        storeData(outState);
    }
}