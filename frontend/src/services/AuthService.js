import axiosClient from "../api/axiosClient";

const login = (username, password) => {
    return axiosClient.post("/auth/login", {
        username,
        password
    });
};

const refreshToken = (refreshToken) => {
    return axiosClient.post("/auth/refresh", {
        refreshToken
    });
};

const logout = (token) => {
    return axiosClient.post("/auth/logout", {
        token
    });
};

const authService = {
    login,
    refreshToken,
    logout
};

export default authService;