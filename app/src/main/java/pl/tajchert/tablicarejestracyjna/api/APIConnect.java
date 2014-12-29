package pl.tajchert.tablicarejestracyjna.api;

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

import pl.tajchert.tablicarejestracyjna.APIConstants;

/**
 * Created by michaltajchert on 29/12/14.
 */
public class APIConnect {
    public static void addComment(String author, String plateId, String content, String photoPath) {
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
