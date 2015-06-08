package pl.tajchert.tablicarejestracyjna.api;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;

import de.greenrobot.event.EventBus;
import pl.tajchert.tablicarejestracyjna.APIConstants;
import retrofit.Callback;
import retrofit.RestAdapter;

/**
 * Created by michaltajchert on 29/12/14.
 */
public class APIConnect {
    public static void addComment(String author, String plateId, String content, String photoPath, Context context) {
        //TODO UPDATE IT TO LATEST HTTPMIME FIXME
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(APIConstants.TABLICE_INFO_PLATE + plateId + APIConstants.TABLICE_INFO_COMMENT_ADD);
            httppost.setHeader(APIConstants.TABLICE_INFO_COMMENT_FIELD_AGENT,APIConstants.TABLICE_INFO_COMMENT_AGENT);

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            entity.addPart(APIConstants.TABLICE_INFO_COMMENT_FIELD_SECRET, new StringBody(APIConstants.TABLICE_INFO_COMMENT_SECRET));
            entity.addPart(APIConstants.TABLICE_INFO_COMMENT_FIELD_NICK, new StringBody(author));
            entity.addPart(APIConstants.TABLICE_INFO_COMMENT_FIELD_CONTENT, new StringBody(content));
            if (photoPath != null) {
                entity.addPart(APIConstants.TABLICE_INFO_COMMENT_FIELD_PHOTOS, new FileBody(new File(photoPath)));
            }
            httppost.setEntity(entity);

            HttpResponse response = httpClient.execute(httppost);
            if(response.getStatusLine().getStatusCode() == 200){
                EventBus.getDefault().post(new EventPostCommentResult(true, "Poprawnie dodano komentarz o " + plateId));
            } else {
                EventBus.getDefault().post(new EventPostCommentResult(false, ""));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            EventBus.getDefault().post(new EventPostCommentResult(false, ""));
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    public static void getTopDrivers(Callback<ApiTopRegistrationPlates> topDriversCallback){
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(APIConstants.API_URL_IMPORTIO)
                .build();
        ITopRegistrationPlates topRegistrationPlates = restAdapter.create(ITopRegistrationPlates.class);
        topRegistrationPlates.getTopDrivers(APIConstants.IMPORTIO_API_USER_KEY, APIConstants.IMPORTIO_API_KEY, topDriversCallback);
    }
}
