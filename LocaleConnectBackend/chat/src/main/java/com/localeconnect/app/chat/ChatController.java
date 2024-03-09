package com.localeconnect.app.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @GetMapping("/test")
    public ResponseEntity<String> testRed(){
        return new ResponseEntity("RED", HttpStatus.OK);
    }
//    @GetMapping("/blue")
//    public ResponseEntity<String> testBlue(){
//        return new ResponseEntity("BLUE", HttpStatus.OK);
//    }
}
