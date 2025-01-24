package com.transcription.transcription.service;

import com.transcription.transcription.repository.UploadLogRepository;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.transcription.transcription.model.UploadLog;
import com.transcription.transcription.model.User;

@Service
public class UploadService {
private final UploadLogRepository uploadLogRepository;
private final UserService authService;

public UploadService(UploadLogRepository uploadLogRepository, UserService authService) {
    this.uploadLogRepository = uploadLogRepository;
    this.authService = authService;
}

public void logUpload(String username,String audioDuration , String checksum, LocalDateTime sentTime,LocalDateTime responseTime,String transcriptionDuration, boolean saveTranscript) {
	try {
    UploadLog uploadLog = new UploadLog();
    uploadLog.setUsername(username);
    uploadLog.setChecksum(checksum);
    uploadLog.setAudioDuration(audioDuration);
    //uploadLog.setAudioDuration(duration);
    uploadLog.setSentTime(sentTime);
    uploadLog.setResponseTime(responseTime);
    uploadLog.setTranscriptionDuration(transcriptionDuration);
   
    // Simulating time for receiving transcript
    

    uploadLogRepository.save(uploadLog);
	}
	catch(Exception e) {
		System.err.println("Error logging transcription details: " + e.getMessage());
	}
}


}