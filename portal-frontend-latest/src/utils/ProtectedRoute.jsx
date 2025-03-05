import { Navigate } from "react-router-dom";
import PropTypes from "prop-types";

const ProtectedRoute = ({ children }) => {
  const token = sessionStorage.getItem("session_token");

  return token ? children : <Navigate to="/" />;
};

ProtectedRoute.propTypes = {
    children: PropTypes.node.isRequired, // Validate children prop
  };

export default ProtectedRoute;