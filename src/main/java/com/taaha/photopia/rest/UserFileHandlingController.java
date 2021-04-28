package com.taaha.photopia.rest;


import com.taaha.photopia.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api")
public class UserFileHandlingController {

    @Autowired
    private StorageService storageService;

//    public StorageController(StorageService storageService) {
//        this.storageService = storageService;
//    }


    @PostMapping("/image")
    public ResponseEntity<?> uploadInvestigation(@RequestParam("file") MultipartFile theFile) throws Exception {
        //upload files
        String[] myFile = storageService.uploadFile(theFile);
        return new ResponseEntity<>(myFile, null, HttpStatus.OK);
    }


    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws Exception {
        return storageService.downloadFile(fileName, request);
    }

}
