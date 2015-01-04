package pl.tajchert.tablicarejestracyjna;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by michaltajchert on 04/01/15.
 */
public class ToolConstants {
    public final static int ACTION_TAKE_PICTURE= 1;
    public final static int ACTION_GALLERY_PICTURE= 2;

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    public static void saveBitmap(Bitmap bitmap, File file) {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
