package choongyul.android.com.retrofit2study;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import choongyul.android.com.retrofit2study.DataForHR.RealData;
import choongyul.android.com.retrofit2study.DataForHR.Results;
import choongyul.android.com.retrofit2study.domain.Data;
import choongyul.android.com.retrofit2study.domain.DataStore;
import choongyul.android.com.retrofit2study.domain.EmailSet;
import choongyul.android.com.retrofit2study.domain.Qna;
import choongyul.android.com.retrofit2study.domain.Token;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String SITE_URL = "http://192.168.0.194/";
    private static final int REQ_PERMISSION = 101;
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    CustomAdapter adapter;
    List<Qna> datas;
    Button button, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVersion(REQ_PERMISSION);


        getData();
        setList();
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                retrofitttt();
//                signup("test@naver.com","asdasdasd");
                dataSetting();
            }
        });
    }

    private void retrofitttt() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserClient localhost = retrofit.create(UserClient.class);

        Qna qna = new Qna();
        qna.set_id("1");
        qna.setTitle("1");
        qna.setContent("1");
        qna.setName("1");
        Token.setKey("asdasdasdasdasd");

        Call<ResponseBody> result = localhost.modifyWithoutImage1(Token.getKey(), qna);

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 값이 정상적으로 리턴되었을 때
                if( response.isSuccessful() ){
                    Log.e("asdaasd",response.body().toString());

                    Log.e("값이","정상리턴");
                } else {
                    Log.e("onResponse","값이 비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void signup(String email, String password) {

        Log.e(TAG,"regist 들어왔다.");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserClient localhost = retrofit.create(UserClient.class);
        EmailSet emailSet = new EmailSet(email, password);

        Call<RequestBody> call = localhost.signup(emailSet);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                if( response.isSuccessful() ){
                    if( response.code() == 200) {

                        Toast.makeText(MainActivity.this, "계정이 생성되었습니다.", Toast.LENGTH_SHORT).show();
//                        onBackPressed(); // 계정이 생성된 경우 로그인할수 있게 뒤로가기를 눌러준다.

                        Log.e(TAG, "signup : 정상리턴");
                    } else {
                        // 동일한 정보를 가진 사용자가 있다.
                        Toast.makeText(MainActivity.this, "동일한 정보의 사용자가 있습니다.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Log.e("signup","비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.e(TAG,"regist 서버통신 실패");
                Log.e(TAG,t.toString());
            }
        });
    }

    private void setList() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        DataStore dataStore = DataStore.getInstance();
        datas = dataStore.getDatas();
        Log.e("데이터의 크기", ""+ datas.size());
        adapter = new CustomAdapter(this, datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // 데이터 읽기파트
    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DataInterface localhost = retrofit.create(DataInterface.class);

        Call<Data> result = localhost.getData();

        result.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                // 값이 정상적으로 리턴되었을 때
                if( response.isSuccessful() ){
                    Data data = response.body();
                    DataStore dataStore = DataStore.getInstance();
                    dataStore.setDatas(data.getData());
                    Log.e("데이터 길이",""+dataStore.getDatas().size());

                    Log.e("값이","정상리턴");
                } else {
                    Log.e("onResponse","값이 비정상적으로 리턴되었다. = " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });

    }

//    private void setAuthor() {
//
//        String token = "Token fbea4ad53df55595a51ef08e1d9920ca014a1830";
//        Log.e(TAG, "token : " + token);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://haru.ycsinabro.com/") // 포트까지가 베이스url이다.
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        // 2. 사용할 인터페이스를 설정한다.
//        DataInterface localhost = retrofit.create(DataInterface.class);
//        // 3. 토큰을 보내 데이터를 가져온다
//        Call<GetUserID> result = localhost.getAuthor(token);
////        Call<ResponseBody> result = localhost.getAuthor();
//        String[] str = {"1","2"};
//        result.enqueue(new Callback<GetUserID>() {
//            @Override
//            public void onResponse(Call<GetUserID> call, Response<GetUserID> response) {
//                Log.e(TAG,"코드 : " + response.code());
//                switch (response.code()) {
//                    case 200:
//                        int te = response.body().getResults().size();
//                        String asd = response.body().getResults().get(te-1).getId();
//                        String qwe = response.body().getResults().get(te-1).getEmail();
////                        DataTemp dataTemp = response.body();
//                        Log.e(TAG, "id, email, length : " + asd + " " + qwe + " " +  te);
////                        int size = dataTemp.getData().size();
////                        int id = dataTemp.getData().get(0).getResults().getAuthor();
////                        Log.e(TAG, "id : " + id);
//                        break;
//                    case 401:
//                        Log.e(TAG, "setAuthor key 이름이 잘못되거나 유효하지 않은 token입니다.");
////                        String detail = response.body().getDetail();
////                        Log.e(TAG, "setAuthor detail : " + detail);
//                        break;
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetUserID> call, Throwable t) {
//
//                Log.e(TAG,"setAuthor 서버통신 실패");
//                Log.e(TAG,t.toString());
//            }
//        });
//    }

    private void dataSetting() {
        String token = "Token fbea4ad53df55595a51ef08e1d9920ca014a1830";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://haru.ycsinabro.com/") // 포트까지가 베이스url이다.
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 2. 사용할 인터페이스를 설정한다.
        DataInterface localhost = retrofit.create(DataInterface.class);
        int pageNum = 1;
        // 3. 토큰을 보내 데이터를 가져온다
        Call<RealData> result = localhost.getd(token, pageNum);

        result.enqueue(new Callback<RealData>() {
            @Override
            public void onResponse(Call<RealData> call, Response<RealData> response) {
                Log.e(TAG,"코드 : " + response.code());
                switch (response.code()) {
                    case 200:
                        RealData data = response.body();
                        int count = data.getCount();
                        Log.e(TAG,"count : " + count);
                        break;
                    case 401:
                        Log.e(TAG, "dataSetting 잘못된 요청입니다.");
                        break;
                    case 500:
                        Log.e(TAG, "dataSetting 잘못된 페이지번호입니다.");
                        // 사용자가 작성한 데이터가 없을때 404 에러가 나며 그냥 액티비티 체인지시켜주면됀다.
                        break;
                }
            }
            @Override
            public void onFailure(Call<RealData> call, Throwable t) {
                Log.e(TAG,"dataSetting 서버통신 실패");
                Log.e(TAG,t.toString());
            }
        });
    }


    // 퍼미션체크
    public final String PERMISSION_ARRAY[] = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
            // TODO 원하는 permission 추가 또는 수정하기, manifest도 추가해줘야 실제 화면에서 선택창 뜸
    };

    public void checkVersion(int REQ_PERMISSION) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( checkPermission(REQ_PERMISSION) ) {
                return;
            }
        } else {
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission(int REQ_PERMISSION) {
        // 1.1 런타임 권한체크 (권한을 추가할때 1.2 목록작성과 2.1 권한체크에도 추가해야한다.)
        boolean permCheck = true;
        for(String perm : PERMISSION_ARRAY) {
            if ( this.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED ) {
                permCheck = false;
                break;
            }
        }

        // 1.2 퍼미션이 모두 true 이면 프로그램 실행
        if(permCheck) {
            // TODO 퍼미션이 승인 되었을때 해야하는 작업이 있다면 여기에서 실행하자.

            return true;
        } else {
            // 1.3 퍼미션중에 false가 있으면 시스템에 권한요청
            this.requestPermissions(PERMISSION_ARRAY, REQ_PERMISSION);
            return false;
        }
    }

    //2. 권한체크 후 콜백 - 사용자가 확인 후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == REQ_PERMISSION) {

            if( onCheckResult(grantResults)) {
                // TODO 퍼미션이 승인 되었을때 해야하는 작업이 있다면 여기에서 실행하자.

                return;
            } else {
                Toast.makeText(this, "권한을 활성화 해야 모든 기능을 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                // 선택 : 1 종료, 2 권한체크 다시물어보기, 3 권한 획득하지 못한 기능만 정지시키기
                // finish();
            }
        }
    }
    public static boolean onCheckResult(int[] grantResults) {

        boolean checkResult = true;
        // 권한 처리 결과 값을 반복문을 돌면서 확인한 후 하나라도 승인되지 않았다면 false를 리턴해준다.
        for(int result : grantResults) {
            if( result != PackageManager.PERMISSION_GRANTED) {
                checkResult = false;
                break;
            }
        }
        return checkResult;
    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    List<Qna> datas;
    Context context;

    public CustomAdapter(Context context , List<Qna> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Qna qna = datas.get(position);
        holder.tvTitle.setText(qna.getTitle());
        holder.tvName.setText(qna.getName());
        holder.position = position;
        Log.e(TAG,"_id : " + qna.get_id());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvName;
        LinearLayout li;
        int position;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            li = (LinearLayout) itemView.findViewById(R.id.li);
            li.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
        }
    }


}