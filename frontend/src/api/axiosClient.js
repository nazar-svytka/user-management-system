import axios from "axios";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const axiosClient = axios.create({
    baseURL: apiBaseUrl,
    headers: {
        "Content-Type": "application/json"
    }
});

axiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("accessToken");

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

axiosClient.interceptors.response.use(
    (response) => response,
    async (error) => {

        const originalRequest = error.config;

        if (
            error.response?.status === 401 &&
            !originalRequest._retry
        ) {
            originalRequest._retry = true;

            const refreshToken = localStorage.getItem("refreshToken");

            if (!refreshToken) {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/login";
                return Promise.reject(error);
            }

            try {

                const response = await axios.post(
                    `${apiBaseUrl}/auth/refresh`,
                    {
                        refreshToken
                    }
                );

                const {
                    accessToken,
                    refreshToken: newRefreshToken
                } = response.data;

                localStorage.setItem(
                    "accessToken",
                    accessToken
                );

                localStorage.setItem(
                    "refreshToken",
                    newRefreshToken
                );

                originalRequest.headers.Authorization =
                    `Bearer ${accessToken}`;

                return axiosClient(originalRequest);

            } catch (refreshError) {

                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");

                window.location.href = "/login";

                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default axiosClient;
