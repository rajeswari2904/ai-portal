package com.transcription.transcription.repository;

import org.springframework.stereotype.Repository;

import com.transcription.transcription.model.UploadLog;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UploadLogRepository extends JpaRepository<UploadLog, Long>{

}
