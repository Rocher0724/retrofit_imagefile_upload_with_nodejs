package choongyul.android.com.retrofit2study;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import choongyul.android.com.retrofit2study.domain.Qna;
import choongyul.android.com.retrofit2study.domain.Token;
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
import retrofit2.http.Part;

import static choongyul.android.com.retrofit2study.MainActivity.SITE_URL;


public class WriteActivity extends AppCompatActivity {

    private static final int REQ_GALLERY = 100;
    private static final String TAG = "WriteActivity";
    EditText etTitle, etName, etContent;
    Button btnGoGal;
    private Uri selectedImageUrl;
    ImageView imageView;
    Uri fileUri;
    private boolean goGalleryFlag = false;

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
                Log.e(TAG,"온클릭들어옴");
                Qna qna = new Qna();
                qna.setTitle(etTitle.getText() + "");
                qna.setContent(etContent.getText() + "");
                qna.setName(etName.getText() + "");

//
                if( goGalleryFlag ) {
                    uploadFile2(fileUri, qna);
                } else {
                    whenUserNoSelectImage(R.drawable.ic_launcher);
                    uploadFile1(fileUri , qna);
                }

//

            }
        });
        btnGoGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goGallery();
            }
        });
    }

    private void uploadBoth(Qna qna, Uri fileUri) {
        File file = new File(fileUri.getPath());
        RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), qna.getTitle());
        RequestBody content = RequestBody.create(MediaType.parse("text/plain"), qna.getContent());
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), qna.getName());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserClient client = retrofit.create(UserClient.class);
        Call<Qna> call = client.editUser(fbody, title, content, name);

        call.enqueue(new Callback<Qna>() {

            @Override
            public void onResponse(Call<Qna> call, Response<Qna> response) {

            }

            @Override
            public void onFailure(Call<Qna> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // 내 갤러리에 있는 이미지만 받아올 수 있도록 세팅
    private void goGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult( intent, REQ_GALLERY);
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
        goGalleryFlag = true;
        fileUri = data.getData();
        Log.e(TAG, fileUri + "");
    }

    private void whenUserNoSelectImage(int resid) {
        Resources res = this.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res,
                resid);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

        File file = new File(extStorageDirectory, "ic_launcher.PNG");
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        outStream.flush();
        outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileUri = Uri.parse(file.getPath());
        Log.e(TAG, fileUri + "");
        if( file != null) {
            Log.e(TAG, "파일이 있다");
            return;
        } else {
            Log.e(TAG, "파일이 없다");
            return;
        }

    }

    private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;
        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(final Uri fileUri, Qna qna) {

        // Qna 클래스는 title, content, name 으로 구성되어있다.
        RequestBody qnaPart1 = RequestBody.create(MultipartBody.FORM, qna.getTitle());
        RequestBody qnaPart2 = RequestBody.create(MultipartBody.FORM, qna.getContent());
        RequestBody qnaPart3 = RequestBody.create(MultipartBody.FORM, qna.getName());

        //////
        Gson gson = new Gson();
        String qnaJsonString = gson.toJson(qna);
        MultipartBody.Part qnaToJson = MultipartBody.Part.createFormData("qna.json", qnaJsonString);

        Token.setKey("asdasdasdasdasd");
        Map<String, RequestBody> map = new HashMap<>();
        map.put("title", qnaPart1);
        map.put("content", qnaPart2);
        map.put("name", qnaPart3);

        File originalFile = FileUtils.getFile(this, fileUri);

        RequestBody filePart = RequestBody.create(
                MediaType.parse(getContentResolver().getType(fileUri)),
                originalFile
        );


//        File originalFile = new File(String.valueOf(fileUri));
//        RequestBody filePart = RequestBody.create(MediaType.parse("multipart/form-data"), originalFile);


                                                                // 이미지 넣을때 키값
        MultipartBody.Part file = MultipartBody.Part.createFormData("userfile", originalFile.getName() , filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        UserClient client = retrofit.create(UserClient.class);

        Call<ResponseBody> call = client.upload(Token.getKey(), map, file);
//        Call<ResponseBody> call2 = client.upload(qnaToJson, file); // 제이슨 키값 캐치가 가능한경우.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Toast.makeText(WriteActivity.this, "Yeah!!!", Toast.LENGTH_SHORT).show();
                    Log.v("Upload", "success");
//                    File file = new File(String.valueOf(fileUri));
//                    file.delete();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "nooooo!!!", Toast.LENGTH_SHORT).show();
                File file = new File(String.valueOf(fileUri));
                file.delete();
                Log.e("Upload error:", t.getMessage());
            }
        });
    }



    private void uploadFile1(final Uri fileUri, Qna qna) {

        // Qna 클래스는 title, content, name 으로 구성되어있다.
        RequestBody qnaPart1 = RequestBody.create(MultipartBody.FORM, qna.getTitle());
        RequestBody qnaPart2 = RequestBody.create(MultipartBody.FORM, qna.getContent());
        RequestBody qnaPart3 = RequestBody.create(MultipartBody.FORM, qna.getName());

        //////
        Gson gson = new Gson();
        String qnaJsonString = gson.toJson(qna);
        MultipartBody.Part qnaToJson = MultipartBody.Part.createFormData("qna.json", qnaJsonString);

        Token.setKey("asdasdasdasdasd");
        Map<String, RequestBody> map = new HashMap<>();
        map.put("title", qnaPart1);
        map.put("content", qnaPart2);
        map.put("name", qnaPart3);

//        Uri fileUri1 = Uri.parse("http://cfile9.uf.tistory.com/image/25621D40573264E71FA362");
//        File originalFile = FileUtils.getFile(this, fileUri);
        File originalFile = new File(String.valueOf(fileUri));
        RequestBody filePart = RequestBody.create(MediaType.parse("multipart/form-data"), originalFile);

//        File file = new File(filePath);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        Call<ApiResponse> call = service.upload(token, userId, msg, requestBody);



//        RequestBody filePart = RequestBody.create(
//                MediaType.parse(getContentResolver().getType(fileUri)),
//                originalFile
//        );
        // 이미지 넣을때 키값
        MultipartBody.Part file = MultipartBody.Part.createFormData("userfile", originalFile.getName() , filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        UserClient client = retrofit.create(UserClient.class);

        Call<ResponseBody> call = client.upload1(map, file);
//        Call<ResponseBody> call2 = client.upload(qnaToJson, file); // 제이슨 키값 캐치가 가능한경우.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Toast.makeText(WriteActivity.this, "Yeah!!!", Toast.LENGTH_SHORT).show();
                    Log.v("Upload", "success");
//                    File file = new File(String.valueOf(fileUri));
//                    file.delete();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "nooooo!!!", Toast.LENGTH_SHORT).show();
                File file = new File(String.valueOf(fileUri));
                file.delete();
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    private void uploadFile2(final Uri fileUri, Qna qna) {

        // Qna 클래스는 title, content, name 으로 구성되어있다.
        RequestBody qnaPart1 = RequestBody.create(MultipartBody.FORM, qna.getTitle());
        RequestBody qnaPart2 = RequestBody.create(MultipartBody.FORM, qna.getContent());
        RequestBody qnaPart3 = RequestBody.create(MultipartBody.FORM, qna.getName());

        //////
        Gson gson = new Gson();
        String qnaJsonString = gson.toJson(qna);
        MultipartBody.Part qnaToJson = MultipartBody.Part.createFormData("qna.json", qnaJsonString);

        Token.setKey("asdasdasdasdasd");
        Map<String, RequestBody> map = new HashMap<>();
        map.put("title", qnaPart1);
        map.put("content", qnaPart2);
        map.put("name", qnaPart3);

//        Uri fileUri1 = Uri.parse("http://cfile9.uf.tistory.com/image/25621D40573264E71FA362");
        File originalFile = FileUtils.getFile(this, fileUri);
//        File originalFile = new File(String.valueOf(fileUri));
//        RequestBody filePart = RequestBody.create(MediaType.parse("multipart/form-data"), originalFile);

//        File file = new File(filePath);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        Call<ApiResponse> call = service.upload(token, userId, msg, requestBody);



        RequestBody filePart = RequestBody.create(
                MediaType.parse(getContentResolver().getType(fileUri)),
                originalFile
        );
        // 이미지 넣을때 키값
        MultipartBody.Part file = MultipartBody.Part.createFormData("userfile", originalFile.getName() , filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(SITE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        UserClient client = retrofit.create(UserClient.class);

        Call<ResponseBody> call = client.upload2(qnaPart1,qnaPart2, 3, file);
//        Call<ResponseBody> call2 = client.upload(qnaToJson, file); // 제이슨 키값 캐치가 가능한경우.
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Toast.makeText(WriteActivity.this, "Yeah!!!", Toast.LENGTH_SHORT).show();
                    Log.v("Upload", "success");
//                    File file = new File(String.valueOf(fileUri));
//                    file.delete();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WriteActivity.this, "nooooo!!!", Toast.LENGTH_SHORT).show();
                File file = new File(String.valueOf(fileUri));
                file.delete();
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
}
