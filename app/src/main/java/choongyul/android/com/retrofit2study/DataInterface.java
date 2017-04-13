package choongyul.android.com.retrofit2study;

import choongyul.android.com.retrofit2study.domain.Data;
import choongyul.android.com.retrofit2study.domain.Qna;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by myPC on 2017-04-11.
 */

public interface DataInterface {

    @GET("post")
    Call<Data> getData();

    @POST("post")
    Call<Qna> setDB(@Body Qna qna);


}
