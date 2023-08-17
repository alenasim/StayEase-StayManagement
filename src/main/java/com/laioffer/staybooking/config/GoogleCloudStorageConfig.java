package com.laioffer.staybooking.config;


import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;

/*this configuration class sets up and provides a bean named storage that establishes a connection to Google Cloud Storage.
This bean can be injected into other components of the Spring application to access and interact with Google Cloud Storage services.
The credentials.json file (in resource folder) contains the necessary authentication information to securely access the cloud storage resources.*/
@Configuration
public class GoogleCloudStorageConfig {

    @Bean   // 提供一个bean去Storage folder里面把credentials读出来，创建storage
    public Storage storage() throws IOException {
        Credentials credentials = ServiceAccountCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("credentials.json"));
        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        /* The first line above retrieves the Google Cloud service account credentials from a file named "credentials.json" located in the
        resources folder of the classpath. These credentials are used to authenticate and authorize access to Google Cloud services.
        The second return statement creates a Storage instance using the provided credentials. It uses the StorageOptions class to
        configure and build the Storage service, and then it returns the built service instance.
        */
    }
}
