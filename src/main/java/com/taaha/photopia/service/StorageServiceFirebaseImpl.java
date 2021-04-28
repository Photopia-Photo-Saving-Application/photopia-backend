package com.taaha.photopia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.*;
import com.taaha.photopia.model.FirebaseCredential;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.rmi.Naming.list;

@Service
public class StorageServiceFirebaseImpl implements StorageService {


        private final Environment environment;

        private StorageOptions storageOptions;
        private String bucketName;
        private String projectId;

        public StorageServiceFirebaseImpl(Environment environment) {
            this.environment = environment;
        }

        @PostConstruct
        private void initializeFirebase() throws Exception {
            bucketName = environment.getRequiredProperty("FIREBASE_BUCKET_NAME");
            projectId = environment.getRequiredProperty("FIREBASE_PROJECT_ID");

            InputStream firebaseCredential = createFirebaseCredential();
            this.storageOptions = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(firebaseCredential)).build();

        }

        @Override
        @Transactional
        public String uploadImage(MultipartFile theImage,String theUsername) throws IOException,Exception {
            String objectName = generateFileName(theImage);
            Storage storage = storageOptions.getService();
            BlobId blobId = BlobId.of(bucketName, theUsername+"/"+objectName);
            String imageName=theImage.getOriginalFilename();
            String imageExtension=(imageName.split("\\.")[1]).toLowerCase();
            if(!imageExtension.equals("png") && !imageExtension.equals("jpg") && !imageExtension.equals("jpeg")){
                throw new Exception("File type is not supported");
            }
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/"+imageExtension).build();
            Blob blob = storage.create(blobInfo, theImage.getBytes());
            return objectName;
            //System.out.println("File " + imageName+ " uploaded to bucket " + bucketName+"/"+theUsername + " as " + objectName);
        }


    @Override
    @Transactional
    public ArrayList<String> fetchUserImage(String theUsername) throws Exception {
        Storage storage = storageOptions.getService();
        Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.currentDirectory(), Storage.BlobListOption.prefix(theUsername+"/"));
        Iterable<Blob> blobIterator = blobs.iterateAll();
        ArrayList<String> imageURL=new ArrayList<String>();
        blobIterator.forEach(blob -> {
            if (!blob.isDirectory()) {
                //System.out.println(blob.getName());
                imageURL.add("https://storage.googleapis.com/"+bucketName+"/"+blob.getName());
            }
        });
        imageURL.remove(0);
        return imageURL;
    }

    @Override
        public ResponseEntity<Object> downloadFile(String fileName, HttpServletRequest request) throws Exception {
            Storage storage = storageOptions.getService();

            Blob blob = storage.get(BlobId.of(bucketName, fileName));
            ReadChannel reader = blob.reader();
            InputStream inputStream = Channels.newInputStream(reader);

            byte[] content = null;
            System.out.println("File downloaded successfully.");

            content = IOUtils.toByteArray(inputStream);

            final ByteArrayResource byteArrayResource = new ByteArrayResource(content);

            return ResponseEntity
                    .ok()
                    .contentLength(content.length)
                    .header("Content-type", "application/octet-stream")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(byteArrayResource);

        }


        private String generateFileName(MultipartFile multiPart) {
            return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
        }

        private InputStream createFirebaseCredential() throws Exception {
            FirebaseCredential firebaseCredential = new FirebaseCredential();
            //private key
            String privateKey = environment.getRequiredProperty("FIREBASE_PRIVATE_KEY").replace("\\n", "\n");

            firebaseCredential.setType(environment.getRequiredProperty("FIREBASE_TYPE"));
            firebaseCredential.setProject_id(projectId);
            firebaseCredential.setPrivate_key_id("FIREBASE_PRIVATE_KEY_ID");
            firebaseCredential.setPrivate_key(privateKey);
            firebaseCredential.setClient_email(environment.getRequiredProperty("FIREBASE_CLIENT_EMAIL"));
            firebaseCredential.setClient_id(environment.getRequiredProperty("FIREBASE_CLIENT_ID"));
            firebaseCredential.setAuth_uri(environment.getRequiredProperty("FIREBASE_AUTH_URI"));
            firebaseCredential.setToken_uri(environment.getRequiredProperty("FIREBASE_TOKEN_URI"));
            firebaseCredential.setAuth_provider_x509_cert_url(environment.getRequiredProperty("FIREBASE_AUTH_PROVIDER_X509_CERT_URL"));
            firebaseCredential.setClient_x509_cert_url(environment.getRequiredProperty("FIREBASE_CLIENT_X509_CERT_URL"));

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(firebaseCredential);

            //convert jsonString string to InputStream using Apache Commons
            return IOUtils.toInputStream(jsonString);
        }

}
