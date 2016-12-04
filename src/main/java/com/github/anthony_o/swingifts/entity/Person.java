package com.github.anthony_o.swingifts.entity;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Person {
    private Long id;
    private String name;
    private String login;
    private String email;
    private String password;

    private Boolean isUser;
    private Boolean hasLogin;
    private Boolean hasEmail;

    private byte[] passwordHash;
    private byte[] salt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
    }

    public Boolean getHasLogin() {
        return hasLogin;
    }

    public void setHasLogin(Boolean hasLogin) {
        this.hasLogin = hasLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @XmlTransient // in order to not send this field in the response, thanks to http://stackoverflow.com/a/13069987/535203
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    @XmlTransient
    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public Boolean getHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(Boolean hasEmail) {
        this.hasEmail = hasEmail;
    }
}
