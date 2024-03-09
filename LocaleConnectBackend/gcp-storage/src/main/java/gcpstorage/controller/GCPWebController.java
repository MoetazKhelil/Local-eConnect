package gcpstorage.controller;

import gcpstorage.response_handler.ResponseHandler;
import gcpstorage.service.GCPService;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/gcp")
@AllArgsConstructor
public class GCPWebController {
    private final GCPService gcpService;

    @GetMapping(value = "/")
    public ResponseEntity<Object> readGcsFile(@RequestParam(value = "filename", required = true) String filename)
            throws IOException {
        String base64Encoded = gcpService.getFileFromGCS(filename);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, base64Encoded, null);
    }

    @PostMapping(value = "/")
    public ResponseEntity<Object> writeGcs(
            @RequestBody String data, @RequestParam(value = "filename", required = true) String filename) {
        String blobId = gcpService.uploadFileToGCS(filename, data);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, blobId, null);
    }

}
