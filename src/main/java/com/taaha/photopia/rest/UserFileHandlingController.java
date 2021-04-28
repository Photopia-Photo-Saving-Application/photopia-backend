package com.taaha.photopia.rest;


import com.taaha.photopia.filter.JwtRequestFilter;
import com.taaha.photopia.model.Response;
import com.taaha.photopia.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class UserFileHandlingController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    @PostMapping("/image")
    public ResponseEntity<Object> uploadImage(@RequestParam("file") MultipartFile theFile) throws Exception {
        System.out.println("inside post/api/image");
        String image=storageService.uploadImage(theFile,jwtRequestFilter.getUsername());
        Map<String,String> payload=new HashMap<>();
        payload.put("image",image);
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "api/image: image upload successful",payload),HttpStatus.OK);
    }

//    @GetMapping("/image")
//    public ResponseEntity<?> fetchUserImage() throws Exception {
//        //upload files
//        return new ResponseEntity<>(myFile, null, HttpStatus.OK);
//    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
        return storageService.downloadFile(fileName, request);
    }

}
