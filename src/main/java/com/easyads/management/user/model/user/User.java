package com.easyads.management.user.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    private long id;
    private String userName;
    private String nickName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonIgnore
    private String passwordHash;
    private int roleType;
    private String roleTypeName;
    private int status;
}
