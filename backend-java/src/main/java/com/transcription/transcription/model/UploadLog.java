package com.transcription.transcription.model;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UploadLog {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String checksum;
    private String audioDuration;
    private LocalDateTime sentTime;
    private LocalDateTime responseTime;
    private String transcriptionDuration;

    // Getters and Setters
    public Long getId() { return id; }
    public String getTranscriptionDuration() {
		return transcriptionDuration;
	}
    public UploadLog(String username, String audioDuration,String checksum, LocalDateTime sentTime, LocalDateTime responseTime,  String transcriptionDuration) {
        this.username = username;
        this.checksum = checksum;
        this.sentTime = sentTime;
        this.responseTime = responseTime;
        
        this.transcriptionDuration = transcriptionDuration;
    }
	public UploadLog() {
		// TODO Auto-generated constructor stub
	}
	public void setTranscriptionDuration(String transcriptionDuration) {
		this.transcriptionDuration = transcriptionDuration;
	}
	public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getChecksum() {
    	return checksum; 
    	}
    public void setChecksum(String checksum) { 
    	this.checksum = checksum; 
    	}

    
	public String getAudioDuration() {
		return audioDuration;
	}
	public void setAudioDuration(String audioDuration) {
		this.audioDuration = audioDuration;
	}
	public LocalDateTime getSentTime() {
		return sentTime;
	}
	public void setSentTime(LocalDateTime sentTime) {
		this.sentTime = sentTime;
	}
	public LocalDateTime getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(LocalDateTime responseTime) {
		this.responseTime = responseTime;
	}

    
}
