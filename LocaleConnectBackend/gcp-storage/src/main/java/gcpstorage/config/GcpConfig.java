package gcpstorage.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class GcpConfig {

    @Value("${gcp.credentials.path}")
    private String credentialsPath;

    @Bean
    public String getCredentialsPath() {
        return credentialsPath;
    }

    @Bean
    public Storage googleCloudStorage() throws IOException {

        FileInputStream serviceAccountStream = new FileInputStream(credentialsPath);
        // Authenticate with Google Cloud Storage using the service account key
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        return storage;
    }
}

