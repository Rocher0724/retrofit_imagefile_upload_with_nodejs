package choongyul.android.com.retrofit2study;

import java.util.Map;

import choongyul.android.com.retrofit2study.domain.EmailSet;
import choongyul.android.com.retrofit2study.domain.Qna;
import choongyul.android.com.retrofit2study.domain.Token;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by myPC on 2017-04-11.
 */

public interface UserClient {

//    @Multipart
//    @POST("upload")
//    Call<ResponseBody> upload(
//            @Part("description") RequestBody description,
//            @Part MultipartBody.Part file
//    );


    // 이건 작동하는것.
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(
            @Header("token") String token,
            @PartMap Map<String, RequestBody> params,
            @Part MultipartBody.Part file
    );

    // 이건 qna 값을 {키 { 키 : 값 } } 으로 받으시는경우.
//    @Multipart
//    @POST("upload")
//    Call<ResponseBody> upload(
//            @Part MultipartBody.Part qnaToJson,
//            @Part MultipartBody.Part file
//    );


    @Multipart
    @POST("postingdata")
    Call<Qna> editUser (@Part("file\"; filename=\"pp.png\" ") RequestBody file
                        , @Part("title") RequestBody title
                        , @Part("content") RequestBody content
                        , @Part("name") RequestBody name);

    // 사진 미선택시 드로어블에있는 사진 업로드 실험
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload1(
//            @Header("Authorization") String token,
            @PartMap Map<String, RequestBody> map,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("upload")
    Call<ResponseBody> upload2(
//            @Header("Authorization") String token,
            @Part ("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("name") int name,
            @Part MultipartBody.Part file
    );

    @POST("asdasd")
    Call<ResponseBody> modifyWithoutImage(
            @Header("Authorization") String token,
            @Body Qna results
    );

    @POST("signup")
    Call<ResponseBody> modifyWithoutImage1(
            @Header("Authorization") String token,
            @Body Qna results
    );

    @POST("signup")
    Call<RequestBody> signup(@Body EmailSet emailSet);
}
