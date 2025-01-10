package com.ecommerce.project.security.response;

import java.util.List;

public class UserInfoResponse {
    private Long Id;
    private String jwtToken;

    private String username;
    private List<String> roles;

    public UserInfoResponse(Long Id, String username, List<String> roles, String jwtToken) {
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
        this.Id = Id;
    }

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
        this.Id = id;
    }

    public Long getId() {
        return Id;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
