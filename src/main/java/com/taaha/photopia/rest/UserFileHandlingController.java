package com.taaha.photopia.rest;


import com.taaha.photopia.filter.JwtRequestFilter;
import com.taaha.photopia.model.Response;
import com.taaha.photopia.model.Response2;
import com.taaha.photopia.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping("/api")
public class UserFileHandlingController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;


    @PostMapping("/images")
    public ResponseEntity<Object> uploadImage(@RequestParam("file") MultipartFile theFile) throws Exception {

        String image=storageService.uploadImage(theFile,jwtRequestFilter.getUsername());
        Map<String,String> payload=new HashMap<>();
        payload.put("image",image);
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "api/image: image upload successful",payload),HttpStatus.OK);
    }

    @GetMapping("/images")
    public ResponseEntity<Object> fetchUserImage() throws Exception {

        ArrayList<String> imageList=storageService.fetchUserImage(jwtRequestFilter.getUsername());
        Map<String, ArrayList<String>> payload=new HashMap<>();
        payload.put("imageList",imageList);
        return new ResponseEntity(new Response2(new Date(), HttpStatus.OK.value(), "api/image: image fetch successful",payload),HttpStatus.OK);
    }

    @DeleteMapping("/images/{image}")
    public ResponseEntity<Object> deleteImage(@PathVariable("image") String theImage) throws Exception {

        System.out.println("inside delete/api/images");
        storageService.deleteImage(theImage, jwtRequestFilter.getUsername());
        Map<String, String> payload=new HashMap<>();
        return new ResponseEntity(new Response(new Date(), HttpStatus.OK.value(), "api/image: image deletion successful",payload),HttpStatus.OK);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
        return storageService.downloadFile(fileName, request);
    }

}
