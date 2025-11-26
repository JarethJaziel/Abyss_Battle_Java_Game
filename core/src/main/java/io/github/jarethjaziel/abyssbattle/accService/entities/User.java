package io.github.jarethjaziel.abyssbattle.accService.entities;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;

    public User() {

    }

    public User(int id, String username, String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public int getId() { 
        return id; 
        }
    public String getUsername() { 
        return username;
         }
    public String getPasswordHash() { 
        return passwordHash;
         }
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
        }

    public void setId(int id) { 
        this.id = id; 
        }
    public void setUsername(String username) { 
        this.username = username; 
        }
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
        }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
        }
}
