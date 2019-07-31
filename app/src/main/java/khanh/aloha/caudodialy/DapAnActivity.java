package khanh.aloha.caudodialy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class DapAnActivity extends AppCompatActivity {
    private TextView txtKetQua, txtDapAnDung, txtChuThich;
    private Button btnNext, btnExit;
    private ScrollView scrollView;

    private String ketQua;
    private int pointionQues, score;
    public static final String EXTRA_POINTER = "extraPointer";
    public static final String EXTRA_SCORE = "extraScore";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dap_an);

        init();

        // Load quang cao
        MobileAds.initialize(this, "ca-app-pub-8428307888801088~2242203197");
        mAdView = findViewById(R.id.adViewDapan);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Lấy value từ QuizActivity truyền sang
        Intent callerIntent = getIntent();
        Bundle packageFromCaller = callerIntent.getBundleExtra("DATA");
        ketQua = packageFromCaller.getString("KETQUA");
        pointionQues = packageFromCaller.getInt("POINTQUES");
        score = packageFromCaller.getInt("SCORE");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnNext.getText().equals("Chơi lại")) {
                    Intent intent = new Intent(DapAnActivity.this, QuizActivity.class);
                    intent.putExtra(EXTRA_POINTER, 0);
                    intent.putExtra(EXTRA_SCORE, 0);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DapAnActivity.this, QuizActivity.class);
                    intent.putExtra(EXTRA_POINTER, pointionQues + 1);
                    intent.putExtra(EXTRA_SCORE, score);
                    startActivity(intent);
                }
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DapAnActivity.this, MainActivity.class);
                intent.putExtra("SAVESCORE", score);
                startActivity(intent);
            }
        });

        hienThiDapAn();

    }

    private void init() {
        txtKetQua = (TextView) findViewById(R.id.textViewKetQua);
        txtDapAnDung = (TextView) findViewById(R.id.textViewDapAnDung);
        txtChuThich = (TextView) findViewById(R.id.textViewChuThich);
        btnNext = (Button) findViewById(R.id.buttonNext);
        btnExit = (Button) findViewById(R.id.buttonExit);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
    }

    private void hienThiDapAn() {
        txtKetQua.setText(ketQua);
        txtChuThich.setText(Common.questionList.get(pointionQues).getChu_thich());
        if(ketQua.equals("Chính xác !")) {
            txtDapAnDung.setText("Bạn đạt được " + score + " điểm");

        } else if(ketQua.equals("Không chính xác !")) {
            txtDapAnDung.setText("Đáp án đúng là: "+ Common.questionList.get(pointionQues).getAnswerNr() + "\n" +
                    "Bạn còn " + score + " điểm");
        } else {
            txtDapAnDung.setText("GAME OVER !");
            btnNext.setText("Chơi lại");
        }

        if((pointionQues == Common.questionList.size()-1) || (score == 0)) {
            btnNext.setText("Chơi lại");
        }

        if(score <= 0) {
            txtDapAnDung.setText("Đáp án đúng là: "+ Common.questionList.get(pointionQues).getAnswerNr() + "\n" +
                    "Bạn còn 0 điểm");
            btnNext.setText("Chơi lại");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DapAnActivity.this, MainActivity.class);
        intent.putExtra("SAVESCORE", score);
        startActivity(intent);
    }

}
