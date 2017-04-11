package choongyul.android.com.retrofit2study;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import java.io.File;

import choongyul.android.com.retrofit2study.domain.DataStore;
import choongyul.android.com.retrofit2study.domain.Qna;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static choongyul.android.com.retrofit2study.MainActivity.SITE_URL;


public class WriteActivity extends AppCompatActivity {

    private static final int REQ_GALLERY = 100;
    private static final String TAG = "WriteActivity";
    EditText etTitle, etName, etContent;
    Button btnGoGal;
    private Uri selectedImageUrl;
    ImageView imageView;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etName = (EditText) findViewById(R.id.etName);
        etContent = (EditText) findViewById(R.id.etContent);
        btnGoGal = (Button) findViewById(R.id.btnWriteGoGal);
        imageView = (ImageView) findViewById(R.id.imageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<String, Void, String> networkTask = new AsyncTask<String, Void, String>(){
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String title = params[0];
                        String name = params[1];
                        String content = params[2];

                        Qna qna = new Qna();
                        qna.setTitle(title);
                        qna.setName(name);
                        qna.setContent(content);

                        Gson gson = new Gson();
                        String jsonString = gson.toJson(qna);

                        String result = Remote.postJson(SITE_URL + "post", jsonString);

                        if("SUCCESS".equals(result)) {
                            DataStore dataStore = DataStore.getInstance();
                            dataStore.addData(qna);
                        }

                        return result;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        Toast.makeText(WriteActivity.this, "작성되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                };
                networkTask.execute(
                        etTitle.getText() + ""
                        ,etName.getText() + ""
                        ,etContent.getText() + "");

                uploadFile(fileUri);
            }
        });
        btnGoGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goGallery();
            }
        });
    }

    private void goGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 외부저장소에 있는 이미지만 가져오기 위한 필터링
        startActivityForResult( Intent.createChooser(intent, " Select Picture"), REQ_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_GALLERY:
                if(resultCode == RESULT_OK) {
                    afterPictureSelect(data);
                } else {
                    Toast.makeText(this, "사진파일이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void afterPictureSelect(Intent data) {
        selectedImageUrl = data.getData();
        Log.e(TAG, "이미지URL");
        Log.e(TAG, data.getData() + "");
        Glide.with(this)
                .load(data.getData())
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Toast.makeText(WriteActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .bitmapTransform(new CenterCrop(this)
                        , new BlurTransformation(this, 10)
                        , new ColorFilterTransformation(this, Color.argb(100, 100, 100, 100)))
                .into(imageView);
        fileUri = data.getData();
    }

    private void uploadFile(Uri fileUri) {

        String descriptionString = "userfile";
        RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, descriptionString);

        File originalFile = FileUtils.getFile(this, fileUri);

        RequestBody filePart = RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        originalFile
                );

        MultipartBody.Part file = MultipartBody.Part.createFormData("userfile", originalFile.getName() , filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        UserClient client = retrofit.create(UserClient.class);

        // finally, execute the request
        Call<ResponseBody> call = client.upload(descriptionPart, file);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Toast.makeText(WriteActivity.this, "Yeah!!!", Toast.LENGTH_SHORT).show();
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "nooooo!!!", Toast.LENGTH_SHORT).show();
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
