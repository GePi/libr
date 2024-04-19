package com.github.gepi.libr.app;

import com.github.gepi.libr.entity.BookLang;
import com.github.gepi.libr.fileStorage.ExtFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FileStorageService {

    @Autowired
    private ExtFileStorage extFileStorage;

    public void save(byte[] file, String fileName, String objectName, BookLang bookLang) {
        String innerObjectName = bookLang == BookLang.EN ? objectName + "-en" : objectName + "-ru";
        extFileStorage.save(file,fileName,innerObjectName);
    }

    public void delete(String objectName, BookLang bookLang) {
        String innerObjectName = bookLang == BookLang.EN ? objectName + "-en" : objectName + "-ru";
        extFileStorage.delete(innerObjectName);
    }

    public InputStream load(String objectName, BookLang bookLang) {
        String innerObjectName = bookLang == BookLang.EN ? objectName + "-en" : objectName + "-ru";
        return  extFileStorage.load(innerObjectName);
    }
}
