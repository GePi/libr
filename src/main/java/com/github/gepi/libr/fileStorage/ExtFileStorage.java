package com.github.gepi.libr.fileStorage;

import java.io.InputStream;

public interface ExtFileStorage {
    void save(byte[] file, String fileName, String objectName);
    void delete(String objectName);
    InputStream load(String objectName);
}
