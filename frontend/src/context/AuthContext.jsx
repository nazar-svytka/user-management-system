import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [accessToken, setAccessToken] = useState(
        localStorage.getItem("accessToken")
    );

    const [refreshToken, setRefreshToken] = useState(
        localStorage.getItem("refreshToken")
    );

    const [role, setRole] = useState(
        localStorage.getItem("role")
    );

    const login = (accessToken, refreshToken, role) => {
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);
        localStorage.setItem("role", role);

        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
        setRole(role);
    };

    const logout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("role");

        setAccessToken(null);
        setRefreshToken(null);
        setRole(null);
    };

    return (
        <AuthContext.Provider
            value={{
                accessToken,
                refreshToken,
                role,
                login,
                logout,
                setAccessToken,
                setRefreshToken,
                setRole
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}