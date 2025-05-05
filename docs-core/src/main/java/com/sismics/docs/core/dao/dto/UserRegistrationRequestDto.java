package com.sismics.docs.core.dao.dto;

/**
 * User registration request DTO.
 *
 * @author jason
 */
public class UserRegistrationRequestDto {
    /**
     * Request ID.
     */
    private String id;
    
    /**
     * Username.
     */
    private String username;
    
    /**
     * Email.
     */
    private String email;
    
    /**
     * Create timestamp.
     */
    private Long createTimestamp;
    
    /**
     * Status.
     */
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 