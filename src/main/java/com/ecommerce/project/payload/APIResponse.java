package com.ecommerce.project.payload;

import lombok.Data;

@Data
public class APIResponse {
    public String message;
    private boolean status;

    public APIResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }

    public APIResponse() {
    }
}
