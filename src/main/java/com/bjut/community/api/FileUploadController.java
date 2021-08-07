package com.bjut.community.api;

import com.bjut.community.entity.UploadFile;
import com.bjut.community.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: 褚真
 * @date: 2021/8/7
 * @time: 11:29
 * @description:
 */
@RestController
public class FileUploadController {
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload-file")
    public ResponseEntity upload(@RequestParam("files") MultipartFile[] files) {
        ArrayList<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            fileStorageService.save(file);
            paths.add(file.getOriginalFilename());
        }
        return ResponseEntity.ok(paths);
    }

    @GetMapping("/files")
    public ResponseEntity<List<UploadFile>> files() {
        List<UploadFile> files = fileStorageService.load()
                .map(path -> {
                    String fileName = path.getFileName().toString();
                    String url = MvcUriComponentsBuilder
                            .fromMethodName(FileUploadController.class,
                                    "getFile",
                                    path.getFileName().toString()
                            ).build().toString();
                    return new UploadFile(fileName, url);
                }).collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) {
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
