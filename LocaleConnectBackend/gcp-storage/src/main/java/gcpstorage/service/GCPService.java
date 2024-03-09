package gcpstorage.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import gcpstorage.exceptions.LogicException;
import gcpstorage.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.storage.GoogleStorageResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GCPService {

    @Value("${gcs-resource-bucket}")
    private final String bucketName;

    private final Storage storage;

    public String uploadFileToGCS(String filename, String data) {

        String base64Image = data.split(",")[1];
        byte[] imageBytes = Base64.decodeBase64(base64Image);

        String contentType = detectResourceType(data);
        String regex = "^image/.*";
        // Check contentType is problematic
        if (!contentType.matches(regex)) {
            throw new LogicException("ONLY IMAGE FILES ARE ALLOWED!");
        }
        String fileExtension = contentType.split("/")[1];
        String generatedFilename = generateFilename(filename);
        String fullFileName = generatedFilename + "." + fileExtension;

        // Create a BlobId
        BlobId blobId = BlobId.of(bucketName, fullFileName);
        // Create BlobInfo
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();
        // Create a blob
        Blob blob = storage.create(blobInfo, new ByteArrayInputStream(imageBytes));

        return blobId.getName();
    }

    public String getFileFromGCS(String filename) throws IOException {
        Blob blob = storage.get(bucketName, filename);
        if (blob == null)
            throw new ResourceNotFoundException("No File Found with this id!");
        // Encode the file content into Base64
        byte[] fileBytes = blob.getContent();
        String encodedImg = Base64.encodeBase64String(fileBytes);
        System.out.println(blob.getContentType());

        return "data:" + blob.getContentType() + ";base64," + encodedImg;
    }

    private GoogleStorageResource fetchResource(String filename) {
        return new GoogleStorageResource(
                this.storage, String.format("gs://%s/%s", this.bucketName, filename));
    }

    private String generateFilename(String filename) {
        // Implement your logic to generate a unique filename
        return filename + "-" + UUID.randomUUID();
    }

    private String detectResourceType(String base64EncodedString) {
        String[] parts = base64EncodedString.split(";base64,");
        String contentType = parts[0].substring(5); // Extract content type
        return contentType;
    }
}
