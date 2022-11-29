package com.lab.ui.main;

import androidx.appcompat.app.AlertDialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lab.R;
import com.lab.objects.Letter;
import com.lab.objects.LetterAddapter;
import com.lab.services.StorageService;

import java.util.Random;

public class GameFragment extends Fragment {

    public final static String KEY_GUESSED_WORDS = "KEY_GUESSED_WORDS";
    public final static String KEY_START_TIME = "KEY_START_TIME";
    public final static String KEY_LETTERS_POOL = "KEY_LETTERS_POOL";
    public final static String KEY_LETTERS_WORD = "KEY_LETTERS_WORD";
    public final static String KEY_CORRECT_LETTERS = "KEY_CORRECT_LETTERS";

    private StorageService storageService;
    boolean cashed = false;

    private int minws_c = 0;
    private int maxws_c = 0;
    private int gamet_c = 0;

    private int minws_a[];
    private int maxws_a[];
    private int gamet_a[];

    private String words_a[];
    private Random random = new Random();

    TextView time_left;
    TextView guessed_words;
    RecyclerView line_1;
    RecyclerView line_2;

    LetterAddapter.OnClickListener line_1_ocl;
    LetterAddapter.OnClickListener line_2_ocl;

    Letter letters_pool[] = new Letter[0];
    Letter letters_word[] = new Letter[0];;

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
            seconds = gamet_a[gamet_c]*60 - seconds;
            if(seconds > 0) {
                time_left.setText(getString(R.string.n_s, seconds));
                timerHandler.postDelayed(this, 500);
            } else {
                time_left.setText(getString(R.string.n_s, 0));
                final AlertDialog ad = new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.n_guessed_words, words_g))
                        .setPositiveButton(R.string.yes, null)
                        .show();
                getActivity().onBackPressed();
            }
        }
    };

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        restoreData(savedInstanceState);
        initResources();
        initGuessedWords(v);
        initLines(v);
        initTimer(v);
        redrawLeters();
        Letter.null_letter = getString(R.string.null_letter);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            storageService = ((StorageService.StorageServiceBinder) iBinder).getService();
            if(storageService != null) {
                words_a = storageService.getStringArray(StorageService.GameSettings.KEY_WORDS);
                if (words_a.length == 0) {
                    words_a = getResources().getStringArray(R.array.words);
                    storageService.putStringArray(StorageService.GameSettings.KEY_WORDS, words_a);
                    storageService.saveStorageData();
                }
                if (!cashed) {
                    minws_c = storageService.getInt(StorageService.GameSettings.KEY_MIN_WORD_SIZE, 0);
                    maxws_c = storageService.getInt(StorageService.GameSettings.KEY_MAX_WORD_SIZE, 0);
                    gamet_c = storageService.getInt(StorageService.GameSettings.KEY_GAME_TIME, 0);
                    newWord();
                    cashed = true;
                }
                initGame();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            storageService = null;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), StorageService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(serviceConnection);
    }

    private void initGame(){
        redrawLeters();
        startTimer();
    }

    private void restoreData(Bundle savedInstanceState){

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_START_TIME))
                startTime = savedInstanceState.getLong(KEY_START_TIME);
            if(savedInstanceState.containsKey(KEY_LETTERS_POOL))
                letters_pool = (Letter[]) savedInstanceState.getParcelableArray(KEY_LETTERS_POOL);
            if(savedInstanceState.containsKey(KEY_LETTERS_WORD))
                letters_word = (Letter[]) savedInstanceState.getParcelableArray(KEY_LETTERS_WORD);
            if(savedInstanceState.containsKey(KEY_CORRECT_LETTERS))
                correct_letters = savedInstanceState.getInt(KEY_CORRECT_LETTERS);
            cashed = true;
        }
    }

    private void startTimer(){
        if(startTime == 0)
            startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void initTimer(View v){
        time_left = v.findViewById(R.id.time_left);
    }

    private void initGuessedWords(View v){
        guessed_words = v.findViewById(R.id.guessed_words);
        updateGuessedWords();
    }

    private void initResources(){
        minws_a = getResources().getIntArray(R.array.min_word_size);
        maxws_a = getResources().getIntArray(R.array.max_word_size);
        gamet_a = getResources().getIntArray(R.array.game_time);
    }

    private void initLines(View v){
        line_1 = v.findViewById(R.id.line_1);
        line_2 = v.findViewById(R.id.line_2);

        LinearLayoutManager layoutManager1= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false);

        line_1.setLayoutManager(layoutManager1);
        line_2.setLayoutManager(layoutManager2);

        line_1_ocl = (view, pp) -> {
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
                    Toast.makeText(getActivity(), R.string.guessed, Toast.LENGTH_SHORT).show();
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

        line_2_ocl = (view, pw) -> {
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
        }while(word.length() > maxws_a[maxws_c] || word.length() < minws_a[minws_c]);
        return word;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(startTime != 0)
            timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_START_TIME, startTime);
        outState.putLong(KEY_GUESSED_WORDS, words_g);
        outState.putParcelableArray(KEY_LETTERS_POOL, letters_pool);
        outState.putParcelableArray(KEY_LETTERS_WORD, letters_word);
        outState.putInt(KEY_CORRECT_LETTERS, correct_letters);
    }

}