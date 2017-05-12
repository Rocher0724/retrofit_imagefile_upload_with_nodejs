package choongyul.android.com.retrofit2study;

/**
 * Created by myPC on 2017-05-12.
 */

public class Test {
    private Test() { }

    private static class TestHolder {
        public static final Test INSTANCE = new Test();
    }

    public static Test getInstance() {
        return TestHolder.INSTANCE;
    }
}
