import { useState } from "react";
import "./Home.css";
import logo from "./../../assets/logo.png";
import { CgProfile } from "react-icons/cg";
//import background from "./../../assets/background.webp"
import axios from "axios";
import { useNavigate } from "react-router-dom";

function Home() {
  const navigate = useNavigate();

  const [load, setLoad] = useState(false);
  const [audioFile, setAudioFile] = useState(null);
  const [saveTranscript, setSaveTranscript] = useState(false);
  const [transcript, setTranscript] = useState(null);

  const username = localStorage.getItem("username");
  const name = username.split("@")[0];

  const handleFileChange = (e) => {
    setAudioFile(e.target.files[0]);
  };

  const handleFileUpload = async (e) => {
    e.preventDefault();
    setLoad(true);
    const formData = new FormData();

    // Append the audio file to formData
    formData.append("audioFile", audioFile); // 'audioFile' is your file input
    formData.append("saveTranscript", saveTranscript); // Append saveTranscript data
    formData.append("username", username); // Append username
    console.log(audioFile.name);
    try {
      const response = await axios.post(
        `http://localhost:8080/api/upload`, // Your backend endpoint
        formData, // Sending the form data
        {
          headers: {
            "Content-Type": "multipart/form-data", // This ensures the backend understands it's multipart
          },
        }
      );

      console.log(response);
      setTranscript(response.data.speaker_labels); // Assuming this is the response data format
      setLoad(false);
    } catch (err) {
      console.log(err);
    }
  };
  const handleExport = (transcript) => {
    if (!transcript) {
      alert("No transcript to export!");
      return;
    }

    // Create a blob with the transcript text
    const blob = new Blob([transcript], { type: "text/plain" });

    // Create a temporary anchor element for downloading
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = audioFile.name.split(".")[0] + ".txt"; // Set the file name

    // Trigger the download
    link.click();

    // Clean up the URL object
    URL.revokeObjectURL(link.href);
  };

  const handleLogout = () => {
    localStorage.removeItem("username");
    navigate("/");
  };

  return (
    <div className="home">
      {load && (
        <>
          <div className="loader-container">
            <div className="loader"></div>
            <div className="loader-text">Transcripting the audio file..</div>
          </div>
        </>
      )}
      <div className="top-nav">
        <div className="d-flex mb-4">
          <img src={logo} alt="" width={100} />
        </div>
        <div className="user-welcome">
          <h5>Welcome, {name}</h5>
        </div>
        <div className="profile">
          <button
            className="dropdown-toggle profile-btn"
            type="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            <CgProfile style={{ color: "blue", fontSize: "30px" }} />
          </button>
          <ul className="dropdown-menu">
            <li>
              <a className="dropdown-item" onClick={handleLogout}>
                Logout
              </a>
            </li>
          </ul>
        </div>
      </div>
      <h2 style={{ marginBottom: "50px" }}>
        AI-POWERED AUDIO TRANSCRIPTION TOOL
      </h2>
      <div className="audio-wrapper">
        <div className="audio-box">
          <div className="upload">
            <h4 style={{ margin: "10px 0 25px 0" }}>Upload your audio file</h4>
            <input
              type="file"
              id="audio-upload"
              className="form-control"
              accept="audio/*"
              onChange={handleFileChange}
            />
            <button
              type="button"
              className="btn btn-primary mt-2"
              onClick={handleFileUpload}
            >
              Upload
            </button>
            <p style={{ color: "gray", fontSize: "15px" }}>
              Supported File Formats mp3, m4a, mpeg
            </p>
            <p style={{ color: "gray", fontSize: "15px" }}>Max Size:25MB</p>
          </div>
          <div className="form-check mt-3" style={{ display: "flex" }}>
            <input
              className="form-check-input"
              style={{ borderColor: "black", marginRight: "5px" }}
              type="checkbox"
              id="save-data"
              checked={saveTranscript}
              onChange={() => setSaveTranscript(!saveTranscript)}
            />
            <label className="form-check-label" htmlFor="flexCheckDefault">
              Save audio file and transcript
            </label>
          </div>
        </div>
        <div className="transcript-box">
          <label htmlFor="transcript" className="form-label label">
            Transcript
          </label>
          <textarea
            id="transcript"
            className="form-control"
            rows="5"
            readOnly
            value={transcript}
          ></textarea>
          <button
            type="button"
            className="btn btn-primary mt-2 transcript-btn"
            onClick={() => handleExport(transcript)}
            disabled={!transcript} // Disable if no transcript
          >
            Export Transcript
          </button>
        </div>
      </div>
    </div>
  );
}

export default Home;
