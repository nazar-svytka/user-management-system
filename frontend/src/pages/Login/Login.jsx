import { useState } from 'react';
import authService from '../../services/AuthService';
import { useAuth } from '../../context/AuthContext';

function Login() {
    const { login } = useAuth();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (event) => {
        event.preventDefault();

        setLoading(true);
        setError('');

        try {
            const response = await authService.login(username, password);

            console.log(response.data);

            login(
                response.data.accessToken,
                response.data.refreshToken,
                response.data.role
            );

            window.location.href = '/dashboard';
        } catch (err) {
            console.log(err);

            setError(
                err.response?.data?.errors?.message ||
                'Invalid username or password.'
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container">
            <div
                className="row justify-content-center align-items-center"
                style={{ minHeight: '100vh' }}
            >
                <div className="col-md-5">
                    <div className="card shadow">
                        <div className="card-body p-4">

                            <h2 className="text-center mb-4">
                                User Management System
                            </h2>

                            {error && (
                                <div className="alert alert-danger">
                                    {error}
                                </div>
                            )}

                            <form onSubmit={handleSubmit}>

                                <div className="mb-3">
                                    <label
                                        htmlFor="username"
                                        className="form-label"
                                    >
                                        Username
                                    </label>

                                    <input
                                        id="username"
                                        type="text"
                                        className="form-control"
                                        placeholder="Enter your username"
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                        required
                                    />
                                </div>

                                <div className="mb-4">
                                    <label
                                        htmlFor="password"
                                        className="form-label"
                                    >
                                        Password
                                    </label>

                                    <input
                                        id="password"
                                        type="password"
                                        className="form-control"
                                        placeholder="Enter your password"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required
                                    />
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary w-100"
                                    disabled={loading}
                                >
                                    {loading ? 'Signing In...' : 'Sign In'}
                                </button>

                            </form>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;