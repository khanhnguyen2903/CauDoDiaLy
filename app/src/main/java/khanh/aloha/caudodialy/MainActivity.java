package khanh.aloha.caudodialy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private Button btnPlay;
    private TextView txtHighScore, txtTitle;

    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighScore";

    private int highScore, scoreNow;

    FirebaseDatabase database;
    DatabaseReference questions;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        init();

        // Load quang cao
        MobileAds.initialize(this, "ca-app-pub-8428307888801088~2242203197");
        mAdView = findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadQuestions();

        addFont();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra kết nối mạng để chơi", Toast.LENGTH_SHORT).show();
                } else {
                    if (Common.questionList.size() > 0) {
                        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_QUIZ);
                    } else {
                        Toast.makeText(getApplicationContext(), "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Lấy value từ DapAnActivity hoặc QuizActivity truyền về
        Intent callerHighScore = getIntent();
        scoreNow = callerHighScore.getIntExtra("SAVESCORE", 0);

        loadHighScore();
    }

    private void init() {
        btnPlay = (Button) findViewById(R.id.buttonPlay);
        txtHighScore = (TextView) findViewById(R.id.textViewDiemCaoNhat);
        txtTitle = (TextView) findViewById(R.id.textViewTitle);

        // Firebase
        database = FirebaseDatabase.getInstance();
        questions = database.getReference("questions");
    }

    private void loadQuestions() {
        if(Common.questionList.size() > 0) {
            Common.questionList.clear();
        }
        questions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Question ques = postSnapshot.getValue(Question.class);
                    Common.questionList.add(ques);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error !", Toast.LENGTH_SHORT).show();
            }
        });

        Collections.shuffle(Common.questionList);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void addFont() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/RixLoveFool.ttf");
        txtHighScore.setTypeface(typeface);
        txtTitle.setTypeface(typeface);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private void loadHighScore() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = preferences.getInt(KEY_HIGHSCORE, 0);
        if(scoreNow > highScore) {
            updateHighScore(scoreNow);
        }
        txtHighScore.setText("Điểm cao nhất: " + highScore);

    }

    private void updateHighScore(int highScoreNew) {
        highScore = highScoreNew;
        txtHighScore.setText("Điểm cao nhất: " + highScore);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_HIGHSCORE, highScore);
        editor.apply();
    }
}
