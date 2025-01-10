package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        //get filename of current file
        String originalFileName = file.getOriginalFilename();
        //generate a unique file name
        String randomId = UUID.randomUUID().toString();
        //mat.jgp -> 1234 -> 1234.jpg
        String fileName = randomId.concat(originalFileName.substring(originalFileName.indexOf('.')));
        String filePath = path + File.separator + fileName;
        //check if path exixts or create if not

        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdir();
        }

        // Check if directory is writable
        if (!folder.canWrite()) {
            throw new IOException("Cannot write to the directory: " + folder.getAbsolutePath());
        }
        //Upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        //return file name
        return fileName;
    }

}
