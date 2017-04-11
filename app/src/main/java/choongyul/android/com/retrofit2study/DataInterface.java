package choongyul.android.com.retrofit2study;

import choongyul.android.com.retrofit2study.domain.Data;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by myPC on 2017-04-11.
 */

public interface DataInterface {

    @GET("post")
    Call<Data> getData();


}
