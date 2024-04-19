package com.github.gepi.libr.fileStorage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class S3Connection {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(S3Connection.class);
    private final MinioProperties minioProperties;

    public MinioClient getMinioClient() {
        return minioClient;
    }

    private MinioClient minioClient;

    public S3Connection(MinioProperties minioProperties) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        this.minioProperties = minioProperties;
        try {
            minioClient =
                    MinioClient.builder()
                            .endpoint(minioProperties.getEndpoint())
                            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                            .build();
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            } else {
                log.info(String.format("Bucket %s already exists.", minioProperties.getBucketName()));
            }
        } catch (MinioException e) {
            log.error("Error occurred: " + e);
            log.error("HTTP trace: " + e.httpTrace());
        }
    }
}
