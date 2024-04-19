package com.github.gepi.libr.fileStorage;

import com.github.gepi.libr.app.exception.S3ObjectNameIsUnfilled;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.apache.groovy.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


@Component
public class S3StorageImplExt implements ExtFileStorage {
    private static final Logger log = LoggerFactory.getLogger(S3StorageImplExt.class);
    private final MinioProperties minioProperties;

    private final S3Connection s3Connection;

    public S3StorageImplExt(MinioProperties minioProperties, S3Connection s3Connection) {
        this.minioProperties = minioProperties;
        this.s3Connection = s3Connection;
    }

    @Override
    public void save(byte[] file, String fileName, String objectName) {
        MinioClient minioClient = s3Connection.getMinioClient();
        if (minioClient == null) {
            return;
        }

        try {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(minioProperties.getBucketName()).object(objectName)
                            .stream(new ByteArrayInputStream(file), file.length, -1)
                            .contentType("application/octet-stream")
                            .tags(Maps.of(minioProperties.getTagOriginalFileName(),
                                    Base64.getUrlEncoder().withoutPadding().encodeToString(
                                            fileName.getBytes(StandardCharsets.UTF_8))))
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error occurred: " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String objectName) {
        MinioClient minioClient = s3Connection.getMinioClient();
        if (minioClient == null) {
            return;
        }
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(minioProperties.getBucketName()).object(objectName).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error occurred: " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream load(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            throw new S3ObjectNameIsUnfilled();
        }
        MinioClient minioClient = s3Connection.getMinioClient();
        if (minioClient == null) {
            return null;
        }
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.error("Error occurred: " + e);
            throw new RuntimeException(e);
        }
    }
}
