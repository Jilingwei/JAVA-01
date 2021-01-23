import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OKHttpDemo {
    public static OkHttpClient client = new OkHttpClient();

    public static String getAsString(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "http://localhost:8801";
        String text = OKHttpDemo.getAsString(url);
        System.out.println("url: " + url + " ; response: \n" + text);
        OKHttpDemo.client = null;
    }
}