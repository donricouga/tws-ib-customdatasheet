package ca.riveros.ib.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Created by ricardo on 7/11/16.
 */
public class GoogleDriveUtil {

    private static Drive drive;

    static {
        try {
            GoogleDriveUtil.getDriveService();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    private static void getDriveService() throws GeneralSecurityException, IOException, URISyntaxException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(new java.io.File("/Users/ricardo/Documents/credentials.json")), httpTransport, jsonFactory)
                .createScoped(new ArrayList<>(DriveScopes.all()));

        drive = new Drive.Builder( httpTransport, jsonFactory, null ).setHttpRequestInitializer( credential ).setApplicationName( "versatile-gist-136923" ).build();
    }



//    public static File uploadFile(String fileName) throws IOException {
//        java.io.File uploadFile = new java.io.File(fileName);
//        File fileMetadata = new File();
//        fileMetadata.setTitle(uploadFile.getName());
//
//        FileContent mediaContent = new FileContent("image/jpeg", uploadFile);
//
//        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
//        MediaHttpUploader uploader = insert.getMediaHttpUploader();
//        uploader.setDirectUploadEnabled(true);
//        uploader.setProgressListener(new FileUploadProgressListener());
//        return insert.execute();
//    }

    /** Uploads a file using either resumable or direct media upload. */
    private static File uploadFile(String fileName) throws IOException {
        File fileMetadata = new File();
        java.io.File uploadFile = new java.io.File(fileName);
        fileMetadata.setTitle(uploadFile.getName());

        FileContent mediaContent = new FileContent("image/jpeg", uploadFile);

        Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(true);
        uploader.setProgressListener(new FileUploadProgressListener());
        return insert.execute();
    }

    public static void main(String ...args) {
        try {
            GoogleDriveUtil.uploadFile("/Users/ricardo/Documents/Scenario.rtf");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
