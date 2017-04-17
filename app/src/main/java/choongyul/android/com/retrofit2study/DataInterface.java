package choongyul.android.com.retrofit2study;

import java.util.Map;

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

    // call<리턴값> 함수명 ( @형식 보내는 타입 밸류명)
    @POST("post")
    Call<Qna> setDB(@Body Qna qna);

    @POST("asdasd")
    Call<String> asdasd(@Body Map a);

}
