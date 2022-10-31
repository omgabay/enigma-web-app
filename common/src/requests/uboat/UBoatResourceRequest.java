package requests.uboat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;

import static utils.Constants.GSON_INSTANCE;

public class UBoatResourceRequest {




    public static void requestUBoatWithParameter(String uboatName, Callback callback){
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                        .newBuilder()
                        .addQueryParameter(Constants.USERNAME,uboatName)
                        .build()
                        .toString();
        HttpClientUtil.runAsync(finalUrl, callback);

    }

    public static void requestUBoatFromSession(Callback callback){
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, callback);
    }




}
