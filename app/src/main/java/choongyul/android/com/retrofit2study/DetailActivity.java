package choongyul.android.com.retrofit2study;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import choongyul.android.com.retrofit2study.domain.DataStore;
import choongyul.android.com.retrofit2study.domain.Qna;

public class DetailActivity extends AppCompatActivity {
    TextView tvDetailTitle, tvDetailContent;
    ImageView imgDetail;
    List<Qna> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvDetailTitle = (TextView) findViewById(R.id.tvDetailTitle);
        tvDetailContent = (TextView) findViewById(R.id.tvDetailContent);
        imgDetail = (ImageView) findViewById(R.id.imgDetail);

        datas = DataStore.getInstance().getDatas();

        Intent intent = getIntent();
        int position = intent.getExtras().getInt("position");
        tvDetailTitle.setText(datas.get(position).getTitle());
        tvDetailContent.setText(datas.get(position).getContent());

        if(datas.get(position).getName() != null) {
            Glide.with(this).load(Uri.parse(datas.get(position).getName())).into(imgDetail);
        }
    }
}
