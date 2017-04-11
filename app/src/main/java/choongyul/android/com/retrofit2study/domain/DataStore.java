package choongyul.android.com.retrofit2study.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by myPC on 2017-04-11.
 */

public class DataStore {

    private static DataStore instance = null;
    private DataStore() { datas = new ArrayList<>(); }
    public static DataStore getInstance () {
        if(instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private List<Qna> datas;

    public List<Qna> getDatas() {
        return datas;
    }

    public void setDatas(List<Qna> datas) {
        this.datas.clear();
        for(Qna qna : datas) {
            this.datas.add(qna);
        }
        this.datas = datas;
    }

    public void addData(Qna qna) {
        this.datas.add(qna);
    }
}
