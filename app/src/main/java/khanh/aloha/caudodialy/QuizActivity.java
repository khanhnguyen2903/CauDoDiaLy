package khanh.aloha.caudodialy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Collections;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView txtScore, txtQuestionCount, txtQuestion, txtLogoQuestion, txtCauhoi, txtDiemTitle, txtTime, txtTitleTime;
    private ImageView imgQuestion;
    private Button btnAnswer1, btnAnswer2, btnAnswer3;

    private int questionCounter;
    private Question currentQuestion;
    private int score, maxScore;
    private String ket_qua;
    private long timer;

    public static final String EXTRA_SCORE = "extraScore";

    private MediaPlayer correct;
    private int numQuestion;
    private CountDownTimer countDownTimer;

    private static final int REQUEST_CODE_QUIZ = 1;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quiz);

        init();

        // Load quang cao
        MobileAds.initialize(this,
                "ca-app-pub-8428307888801088~2242203197");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8428307888801088/9406377340");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        // Lấy value từ DapAnActivity truyền về
        Intent caller = getIntent();
        questionCounter = caller.getIntExtra(DapAnActivity.EXTRA_POINTER, 0);
        score = caller.getIntExtra(DapAnActivity.EXTRA_SCORE, 0);

        if(score == 0) {
            Collections.shuffle(Common.questionList);
        }

        if(questionCounter < Common.questionList.size()) {
            setData();
        } else {
            Intent intent = new Intent(QuizActivity.this, MainActivity.class);
            startActivity(intent);
        }

        if(!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra kết nối mạng để chơi", Toast.LENGTH_SHORT).show();
        }

        addFont();
    }

    private void setData() {
        if(questionCounter < Common.questionList.size()) {
            currentQuestion =Common.questionList.get(questionCounter);

            if(currentQuestion.getIsImageQuestion().equals("true")) {
                Glide.with(getBaseContext())
                        .load(currentQuestion.getQuestion())
                        .into(imgQuestion);

                imgQuestion.setVisibility(View.VISIBLE);
                txtLogoQuestion.setVisibility(View.VISIBLE);
                txtQuestion.setVisibility(View.INVISIBLE);
            } else {
                imgQuestion.setVisibility(View.INVISIBLE);
                txtLogoQuestion.setVisibility(View.INVISIBLE);
                txtQuestion.setVisibility(View.VISIBLE);
                txtQuestion.setText(currentQuestion.getQuestion());
            }

            btnAnswer1.setText(currentQuestion.getOption1());
            btnAnswer2.setText(currentQuestion.getOption2());
            btnAnswer3.setText(currentQuestion.getOption3());

            txtScore.setText(score + "");
            //questionCounter++;
            txtQuestionCount.setText(1+questionCounter+"");
        } else {
            Toast.makeText(getApplicationContext(), "Bạn đã trả lời hết tất cả câu hỏi", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }

        setTime();
    }

    private void init() {
        txtScore = (TextView) findViewById(R.id.textViewScore);
        txtQuestionCount = (TextView) findViewById(R.id.textViewQuestionCount);
        txtQuestion = (TextView) findViewById(R.id.textViewQuestion);
        txtLogoQuestion = (TextView) findViewById(R.id.textViewLogoQuestion);
        txtCauhoi = (TextView) findViewById(R.id.textViewCauhoi);
        txtDiemTitle = (TextView) findViewById(R.id.textViewDiemTitle);
        txtTime = (TextView) findViewById(R.id.textViewTime);
        txtTitleTime = (TextView) findViewById(R.id.textViewTitleTime);

        imgQuestion = (ImageView) findViewById(R.id.imageViewQuestion);
        btnAnswer1 = (Button) findViewById(R.id.buttonAnswer1);
        btnAnswer2 = (Button) findViewById(R.id.buttonAnswer2);
        btnAnswer3 = (Button) findViewById(R.id.buttonAnswer3);

        btnAnswer1.setOnClickListener(this);
        btnAnswer2.setOnClickListener(this);
        btnAnswer3.setOnClickListener(this);

        // Chen am thanh correct khi tra loi dung
        correct = MediaPlayer.create(getApplicationContext(), R.raw.correctsound);
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
        txtQuestionCount.setTypeface(typeface);
        txtScore.setTypeface(typeface);
        txtDiemTitle.setTypeface(typeface);
        txtCauhoi.setTypeface(typeface);
        txtTime.setTypeface(typeface);
        txtTitleTime.setTypeface(typeface);
    }

    @Override
    public void onClick(View view) {
        Button clickedButton = (Button) view;
        Intent intent = new Intent(QuizActivity.this, DapAnActivity.class);
        Bundle bundle = new Bundle();
        if(clickedButton.getText().equals(currentQuestion.getAnswerNr())) {
            //Toast.makeText(getApplicationContext(), "Bạn đã trả lời đúng", Toast.LENGTH_SHORT).show();
            correct.start();

            ket_qua = "Chính xác !";
            if((60 >= timer) && (timer >= 45)) {
                score += 15;
            } else if((45 > timer) && (timer >= 30)) {
                score += 10;
            } else if((30 > timer) && (timer >= 15)) {
                score += 5;
            } else if(timer < 15) {
                score += 1;
            }

        } else {
            //Toast.makeText(getApplicationContext(), "Bạn đã trả lời sai", Toast.LENGTH_SHORT).show();
            ket_qua = "Không chính xác !";
            score -= 5;
        }

        bundle.putString("KETQUA", ket_qua);
        bundle.putInt("POINTQUES", questionCounter);
        bundle.putInt("SCORE", score);
        intent.putExtra("DATA", bundle);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);

        countDownTimer.cancel();

        if(((questionCounter%5) == 0) && (questionCounter != 0)) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }
    }

    private void setTime() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                timer = l/1000;
                txtTime.setText(timer+"s");

                if(timer < 6) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if(vibrator.hasVibrator()) {
                        vibrator.vibrate(500);
                    }
                }
            }

            @Override
            public void onFinish() {
                txtTime.setText("Hết giờ");
                ket_qua = "Hết giờ";

                Intent intent = new Intent(QuizActivity.this, DapAnActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("KETQUA", ket_qua);
                bundle.putInt("POINTQUES", questionCounter);
                bundle.putInt("SCORE", score);
                intent.putExtra("DATA", bundle);
                startActivityForResult(intent, REQUEST_CODE_QUIZ);

                countDownTimer.cancel();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QuizActivity.this, MainActivity.class);
        intent.putExtra("SAVESCORE", score);
        startActivity(intent);
    }
}
