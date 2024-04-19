package com.github.gepi.libr.app.exception;

public class S3ObjectNameIsUnfilled extends RuntimeException{
    public S3ObjectNameIsUnfilled() {
        super("Не заполнено имя объекта файла для S3");
    }
}
