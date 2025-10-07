package co.com.anfega.api.helper.client;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T content;
}
