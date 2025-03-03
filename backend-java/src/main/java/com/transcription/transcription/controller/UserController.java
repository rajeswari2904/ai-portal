package com.transcription.transcription.controller;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import com.mpatric.mp3agic.Mp3File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;

import java.util.Optional;
import java.util.logging.Logger;

import com.transcription.transcription.authentication.JwtUtil;
import com.transcription.transcription.model.User;
import com.transcription.transcription.service.UploadService;
import com.transcription.transcription.service.UserService;
import java.nio.file.Files;
import java.io.File;
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class.getName());
	private static final String AUDIO_FOLDER = "D:\\AI-Portal-FInal\\transcription\\transcription\\temp_audio\\";
	
	@Autowired
    private  UserService userService;
	
	@Autowired
    private UploadService uploadService;
    
	@Autowired
	private  JwtUtil jwtUtil;
    
    @Autowired
    private  PasswordEncoder passwordEncoder;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
        	
        	String hashedPassword = passwordEncoder.encode(user.getPassword());
        	System.out.println(hashedPassword);
            User createdUser = userService.createUser(user.getUsername(), hashedPassword);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Login API
    @PostMapping("/login")
    public String authenticateUser(@RequestParam String username, @RequestParam String password) {
        //return userService.findByUsername(username,password)
                //.map(user -> {
                    //if (user.getPassword().equals(password)) {
                        ////return ResponseEntity.ok("Authentication successful");
                    //} else {
                        //return ResponseEntity.status(401).body("Invalid credentials");
                   // }
                //})
                //.orElse(ResponseEntity.status(404).body("User not found"));

    	Optional<User> user = Optional.ofNullable(userService.findByUsername(username,password));
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            String token = jwtUtil.generateToken(username);
            return token;
        }
        throw new RuntimeException("Invalid username or password");
    }
    

    // File upload API
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("audioFile") MultipartFile audioFile,
                                        @RequestParam("saveTranscript") boolean saveTranscript,
                                        @RequestParam String username) throws Exception {
//        // Authenticate user
//        User user = userService.findByUsername(username, "password");
//        
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
//        }
    	String apiUrl = "http://127.0.0.1:8001/transcribe/";
    	File folder = new File(AUDIO_FOLDER);
        if (!folder.exists() && !folder.mkdirs()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not create directory: " + AUDIO_FOLDER);
        }

        // Save the file
        String filePath = AUDIO_FOLDER + audioFile.getOriginalFilename();
        Path actualPath = Paths.get(AUDIO_FOLDER, audioFile.getOriginalFilename());;
        System.out.println(filePath);
        System.out.println(actualPath);
        File destinationFile = new File(filePath);
        audioFile.transferTo(destinationFile);
    	LocalDateTime sentTime = LocalDateTime.now();
        try {
        	if (!Files.exists(actualPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + audioFile.getOriginalFilename());
            }

            // Read the file content as a string
            byte[] fileBytes = Files.readAllBytes(actualPath);

            // Create a unique boundary
            String boundary = "Boundary-" + System.currentTimeMillis();
            String fileName = Path.of(filePath).getFileName().toString();

            // Build the multipart body
            StringBuilder bodyBuilder = new StringBuilder();
            bodyBuilder.append("--").append(boundary).append("\r\n");
            bodyBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
            bodyBuilder.append("Content-Type: audio/wav\r\n\r\n"); // Adjust MIME type as needed
            String bodyPrefix = bodyBuilder.toString();
            String bodySuffix = "\r\n--" + boundary + "--\r\n";
            
            // Combine the multipart body components
            byte[] multipartBody = concatenate(
                bodyPrefix.getBytes(),
                fileBytes,
                bodySuffix.getBytes()
            );

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

            // Send the request and get the response
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LocalDateTime responseTime = LocalDateTime.now();
            String checksum = this.generateChecksum(actualPath);
            
            JSONObject jsonResponse = new JSONObject(response.body());
            System.out.println(jsonResponse);
            String transcriptionDuration = jsonResponse.getString("transcription_diarization_time");
            String audioDuration = jsonResponse.getString("audio_duration");
            String responseText = jsonResponse.toString();
            uploadService.logUpload(username, audioDuration , checksum, sentTime, responseTime, transcriptionDuration, saveTranscript);
            // Log the response
            System.out.println("Response Status: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return ResponseEntity.ok(responseText);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
        
    }
    
    // Helper method to concatenate byte arrays
    private static byte[] concatenate(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int currentPosition = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentPosition, array.length);
            currentPosition += array.length;
        }
        return result;
    }
    // Method to generate checksum (stub for real logic)
    private String generateChecksum(Path filePath) throws IOException {
        // Logic for checksum generation (e.g., MD5/SHA256)
    	
    	
        byte[] fileBytes = Files.readAllBytes(filePath);
        
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(fileBytes);
    }
    public static long getAudioDuration(String filePath) throws Exception {
        Mp3File mp3File = new Mp3File(filePath);

        if (mp3File.hasId3v2Tag() || mp3File.hasId3v1Tag()) {
            return mp3File.getLengthInSeconds();
        } else {
            throw new Exception("Invalid MP3 file or missing tags.");
        }
    }

    // Method to get audio duration (stub for real logic)
//    private int getAudioDuration(MultipartFile file) {
//    	// Convert MultipartFile to InputStream
//        InputStream audioStream = file.getInputStream();
//
//        // Create an AudioDispatcher that reads the audio data
//        AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioStream, 44100, 1024, 512);
//        
//        // Initialize a variable to track the number of audio frames
//        int totalFrames = 0;
//
//        // Loop through the audio data and count the frames
//        dispatcher.addAudioEventListener(new AudioEventListener() {
//            @Override
//            public void audioEvent(AudioEvent audioEvent) {
//                totalFrames += audioEvent.getBuffer().length;
//            }
//        });
//
//        // Start processing the audio stream
//        dispatcher.run();
//
//        // Get the sample rate (in Hz) - default for most audio files is 44100 Hz
//        int sampleRate = 44100;
//
//        // Calculate the duration in seconds
//        double durationInSeconds = totalFrames / (double) sampleRate;
//
//        // Convert to an integer (or use it as a double depending on your needs)
//        return (int) durationInSeconds;  // Duration in seconds
//    }
//   
//    // API for exporting the transcript
//    @GetMapping("/downloadTranscript")
//    public ResponseEntity<byte[]> downloadTranscript() {
//        try {
//            // Example of fetching transcript from DB or generating it on demand
//            byte[] transcript = "Generated Transcript".getBytes();
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transcript.txt")
//                    .body(transcript);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//    
    
}