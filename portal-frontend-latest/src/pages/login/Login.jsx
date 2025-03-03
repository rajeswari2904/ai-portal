import { useState } from "react";
import "./Login.css";
import { MdEmail } from "react-icons/md";
import { RiLockPasswordFill } from "react-icons/ri";
import validator from "validator";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import background from "./../../assets/bgLogin.jpg";
import logo from "./../../assets/logo.png";

function Login() {
  const navigate = useNavigate();

  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });

  const [error, setError] = useState({
    username: "",
    password: "",
  });

  const [loginError, setLoginError] = useState([]);

  const onInputChange = (e) => {
    const { name, value } = e.target;
    setCredentials((prev) => ({
      ...prev,
      [name]: value,
    }));
    validateInput(e);
  };

  const validateInput = (e) => {
    let { name, value } = e.target;
    setError((prev) => {
      const stateObj = { ...prev, [name]: "" };

      switch (name) {
        case "username":
          if (!value) {
            stateObj[name] = "Please enter username.";
          } else if (!validator.isEmail(value)) {
            stateObj[name] = "Invalid username.";
          }
          break;

        case "password":
          if (!value) {
            stateObj[name] = "Please enter Password.";
          }
          break;

        default:
          break;
      }

      return stateObj;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        `http://localhost:8080/api/login`,
        null, // No body, as params will be passed in the URL
        {
          params: {
            username: credentials.username,
            password: credentials.password,
          },
        }
      );
      console.log(response);

      if (response.status === 200) {

        const token = response.data; // Extract JWT token

        // Store token securely in sessionStorage
        sessionStorage.setItem("session_token", token);

        // Save username to session storage
        sessionStorage.setItem("username", credentials.username);

        // Redirect to the home page
        navigate("/home");
      } else {
        console.error("Unexpected response:", response.data);
      }
    } catch (err) {
      console.log(err);
      setLoginError("Incorrect credentials");
    }
  };

  return (
    <>
      <div
        className="bg-image"
        style={{ backgroundImage: `url(${background})` }}
      ></div>
      <div className="login-wrapper">
        <div className="login-box">
          <div className="d-flex justify-content-center mb-4">
            <img src={logo} alt="" width={100} />
          </div>
          <div className="login-content">
            <div className="text-center mb-5">
              <h3 className="fw-bold">Log In</h3>
              <p>Access your account</p>
            </div>
            <div className="log-form">
              <form onSubmit={handleSubmit}>
                <div className="input-group mb-4">
                  <span className="input-group-text">
                    <MdEmail style={{ marginRight: "2px", fontSize: "20px" }} />
                    <input
                      type="email"
                      className="form-control form-control-lg fs-6 ms-2"
                      placeholder="Username"
                      name="username"
                      onChange={onInputChange}
                    />
                  </span>
                  {error.username && (
                    <span className="err">{error.username}</span>
                  )}
                </div>
                <div className="input-group mb-4">
                  <span className="input-group-text">
                    <RiLockPasswordFill
                      style={{ marginRight: "2px", fontSize: "20px" }}
                    />
                    <input
                      type="password"
                      className="form-control form-control-lg fs-6 ms-2"
                      placeholder="Password"
                      name="password"
                      onChange={onInputChange}
                    />
                  </span>
                  {error.password && (
                    <span className="err">{error.password}</span>
                  )}
                </div>
                <button className="btn btn-primary mb-3">Login</button>
              </form>
              {loginError && <span className="loginerr">{loginError}</span>}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default Login;
