package choongyul.android.com.retrofit2study.domain;

/**
 * Created by myPC on 2017-04-17.
 */

public class Token {
    private static String key;

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        Token.key = key;
    }
}
