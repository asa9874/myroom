package com.example.myroom.global.util;

import java.lang.reflect.Type;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@SuppressWarnings("deprecation")
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    /**
     * https://stackoverflow.com/questions/16230291/requestpart-with-mixed-multipart-request-spring-mvc-3-2
     * Converter for support http request with header Content-Type: multipart/form-data
     */
    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canWrite(@NonNull Class<?> clazz,@Nullable MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(@Nullable Type type,@NonNull  Class<?> clazz,@Nullable  MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(@Nullable MediaType mediaType) {
        return false;
    }
}