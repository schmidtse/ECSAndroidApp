package com.schmidtse.apptests42;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Initial draft to collect the camera handling
 */
public class CameraHandler extends Service{

    public static final String S3_ACCESS_KEY_ID = "";
    public static final String S3_SECRET_KEY = "";
    public static final String S3_ENDPOINT = "https://object.ecstestdrive.com";
    public static final String S3_BUCKET = "test03";

    private String sendToECS(String bucketName, String keyName, Uri file) throws IOException {
        ClientConfiguration config = new ClientConfiguration();

        // set to your endpoint's protocol
        config.setProtocol(Protocol.HTTP);

        // for SDK 1.9.4 and above
        // Force use of v2 Signer.  ECS does not support v4 signatures yet.
        config.setSignerOverride("S3SignerType");

        AmazonS3Client client = new AmazonS3Client(new BasicAWSCredentials(S3_ACCESS_KEY_ID, S3_SECRET_KEY), config);

        // required only if using path-style buckets
        S3ClientOptions options = new S3ClientOptions();
        options.setPathStyleAccess(true);
        client.setS3ClientOptions(options);
        client.setEndpoint(S3_ENDPOINT);

        InputStream is = getContentResolver().openInputStream(file);
        long imageSize = getSizeForURI(file);


//        client.createBucket(bucketName);
        ObjectMetadata meta = new ObjectMetadata();
// explicitly set the content length here
        meta.setContentLength(imageSize);
//        meta.setUserMetadata(CouchsurfingApiUtils.getAwsUserMetadata(userId, albumId));
        client.putObject(new PutObjectRequest(bucketName, keyName, is, meta));

        return "done";
    }

    private long getSizeForURI(Uri returnUri)
    {
        Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
    /*
     * Get the column indexes of the data in the Cursor,
     * move to the first row in the Cursor, get the data,
     * and display it.
     */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        return returnCursor.getLong(sizeIndex);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
