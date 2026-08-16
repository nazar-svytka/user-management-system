import { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

import userService from '../../services/UserService';
import Swal from "sweetalert2";
import { toast } from "react-toastify";

import {
    FaUserPlus,
    FaSignOutAlt,
    FaEdit,
    FaTrash,
    FaUsers,
    FaUserShield,
    FaUser,
    FaMoon,
    FaSun
} from "react-icons/fa";

function Dashboard() {
    const { logout, role: currentUserRole } = useAuth();
    const navigate = useNavigate();

    const [users, setUsers] = useState([]);

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const [showModal, setShowModal] = useState(false);

    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("USER");
    const [showEditModal, setShowEditModal] = useState(false);

const [editingUserId, setEditingUserId] = useState(null);

const [editUsername, setEditUsername] = useState("");
const [editEmail, setEditEmail] = useState("");
const [editRole, setEditRole] = useState("USER");
const [search, setSearch] = useState("");

const [sort, setSort] = useState("id");
const [direction, setDirection] = useState("asc");
const [loading, setLoading] = useState(false);
const [darkMode, setDarkMode] = useState(false);

    useEffect(() => {
    loadUsers(page);
}, [page, search, sort, direction]);

    const loadUsers = async (currentPage) => {

    setLoading(true);

    try {

        const response = await userService.getUsers(
            currentPage,
            5,
            search,
            `${sort},${direction}`
        );

        setUsers(response.data.content);
        setTotalPages(response.data.totalPages);

    } catch (error) {

        console.log(error);

    } finally {

        setLoading(false);

    }

};

    const createUser = async () => {
        try {
            await userService.createUser({
                username,
                email,
                password,
                role
        });

            toast.success("User created successfully!");

            setShowModal(false);

            setUsername("");
            setEmail("");
            setPassword("");
            setRole("USER");

            loadUsers(page);
    }catch (error) {
    console.error(error);

    toast.error(
        error.response?.data?.message ||
        "Failed to create user."
    );
}
        
    };
    const openEditModal = (user) => {
    setEditingUserId(user.id);
    setEditUsername(user.username);
    setEditEmail(user.email);
    setEditRole(user.role);
    setShowEditModal(true);
};

const updateUser = async () => {
    try {

        await userService.updateUser(editingUserId, {
            username: editUsername,
            email: editEmail,
            role: editRole
        });

        toast.success("User updated successfully!");

        setShowEditModal(false);

        loadUsers(page);

    } catch (error) {
    console.error(error);

    toast.error(
        error.response?.data?.message ||
        "Failed to update user."
    );
}
};

const deleteUser = async (id) => {

    const result = await Swal.fire({
        title: "Delete user?",
        text: "This action cannot be undone!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#dc3545",
        cancelButtonColor: "#6c757d",
        confirmButtonText: "Yes, delete",
        cancelButtonText: "Cancel"
    });

    if (!result.isConfirmed) {
        return;
    }

    try {

        await userService.deleteUser(id);

        toast.success("User deleted successfully!");

        loadUsers(page);

    } catch (error) {

        console.log(error);

        toast.error(
            error.response?.data?.message ||
            "Failed to delete user."
        );

    }
};

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const previousPage = () => {
        if (page > 0) {
            setPage(page - 1);
        }
    };

    const nextPage = () => {
        if (page < totalPages - 1) {
            setPage(page + 1);
        }
    };
    const changeSort = (column) => {

    if (sort === column) {

        setDirection(prev => prev === "asc" ? "desc" : "asc");

    } else {

        setSort(column);
        setDirection("asc");

    }

    setPage(0);

};


const getAvatarColor = (username) => {

    const colors = [
        "#2563eb",
        "#7c3aed",
        "#059669",
        "#ea580c",
        "#dc2626",
        "#0891b2"
    ];

    let hash = 0;

    for (let i = 0; i < username.length; i++) {
        hash += username.codePointAt(i) ?? 0;
    }

    return colors[hash % colors.length];
};

return (
    <div className={darkMode ? "dashboard-page dark-mode" : "dashboard-page"}>

        <div className="container mt-5">

            <div className="card shadow-lg border-0">

                <div className="card-body p-4">

                    <div className="dashboard-header">

                        <div className="dashboard-title">

                            <h2 className="mb-1">
                                Dashboard
                            </h2>

                            <p className="text-secondary mb-0">
                                Manage users and permissions
                            </p>

                        </div>

                        <div className="dashboard-actions">

                            {currentUserRole === "ADMIN" && (
                                <button
                                    className="btn btn-success d-inline-flex align-items-center gap-2"
                                    onClick={() => setShowModal(true)}
                                >
                                    <FaUserPlus />
                                    Add User
                                </button>
                            )}

                            <button
                                className="btn btn-dark d-inline-flex align-items-center gap-2"
                                onClick={() => setDarkMode(!darkMode)}
                            >
                                {darkMode ? <FaSun /> : <FaMoon />}
                                {darkMode ? "Light" : "Dark"}
                            </button>

                            <button
                                className="btn btn-danger d-inline-flex align-items-center gap-2"
                                onClick={handleLogout}
                            >
                                <FaSignOutAlt />
                                Logout
                            </button>

                        </div>

                    </div>

                    <div className="stats">

                        <div className="stat-card">

                            <div className="stat-icon stat-blue">
                                <FaUsers />
                            </div>

                            <div>
                                <h3>{users.length}</h3>
                                <p>Total Users</p>
                            </div>

                        </div>

                        <div className="stat-card">

                            <div className="stat-icon stat-red">
                                <FaUserShield />
                            </div>

                            <div>
                                <h3>
                                    {users.filter(user => user.role === "ADMIN").length}
                                </h3>
                                <p>Admins</p>
                            </div>

                        </div>

                        <div className="stat-card">

                            <div className="stat-icon stat-green">
                                <FaUser />
                            </div>

                            <div>
                                <h3>
                                    {users.filter(user => user.role === "USER").length}
                                </h3>
                                <p>Users</p>
                            </div>

                        </div>

                    </div>

                    <div className="search-wrapper">

                        <input
                            type="text"
                            className="form-control"
                            placeholder="🔍 Search by username or email..."
                            value={search}
                            onChange={(e) => {
                                setSearch(e.target.value);
                                setPage(0);
                            }}
                        />

                    </div>

                    <div className="table-wrapper">

                        <table className="table align-middle">

                            <thead>

                                <tr>

                                    <th>ID</th>

                                    <th
                                        style={{ cursor: "pointer" }}
                                        onClick={() => changeSort("username")}
                                    >
                                        Username {sort === "username" && (direction === "asc" ? "▲" : "▼")}
                                    </th>

                                    <th
                                        style={{ cursor: "pointer" }}
                                        onClick={() => changeSort("email")}
                                    >
                                        Email {sort === "email" && (direction === "asc" ? "▲" : "▼")}
                                    </th>

                                    <th
                                        style={{ cursor: "pointer" }}
                                        onClick={() => changeSort("role")}
                                    >
                                        Role {sort === "role" && (direction === "asc" ? "▲" : "▼")}
                                    </th>

                                    {currentUserRole === "ADMIN" && (
                                        <th>Actions</th>
                                    )}

                                </tr>

                            </thead>

                            <tbody>

    {loading ? (

        <tr>
            <td
                colSpan={currentUserRole === "ADMIN" ? 5 : 4}
                className="text-center py-5"
            >

                <div
                    className="spinner-border text-primary"
                    role="status"
                >
                    <span className="visually-hidden">
                        Loading...
                    </span>
                </div>

                <div className="mt-3">
                    Loading users...
                </div>

            </td>
        </tr>

    ) : (

        users.map((user) => (

            <tr key={user.id}>

                <td>{user.id}</td>

                <td>
                    <div className="user-cell">

                        <div
                            className="avatar"
                            style={{
                                backgroundColor: getAvatarColor(user.username)
                            }}
                        >
                            {user.username.charAt(0).toUpperCase()}
                        </div>

                        <span>{user.username}</span>

                    </div>
                </td>

                <td>{user.email}</td>

                <td>
                    {user.role === "ADMIN" ? (
                        <span className="badge bg-danger">
                            ADMIN
                        </span>
                    ) : (
                        <span className="badge bg-primary">
                            USER
                        </span>
                    )}
                </td>

                {currentUserRole === "ADMIN" && (

                    <td>

                        <button
                            className="btn btn-warning btn-sm me-2 d-inline-flex align-items-center gap-1"
                            onClick={() => openEditModal(user)}
                        >
                            <FaEdit />
                            Edit
                        </button>

                        <button
                            className="btn btn-danger btn-sm d-inline-flex align-items-center gap-1"
                            onClick={() => deleteUser(user.id)}
                        >
                            <FaTrash />
                            Delete
                        </button>

                    </td>

                )}

            </tr>

        ))

    )}

</tbody>

</table>

</div>
<div className="d-flex justify-content-center align-items-center gap-3 mt-4">

    <button
        className="btn btn-secondary"
        onClick={previousPage}
        disabled={page === 0}
    >
        Previous
    </button>

    <span>
        Page {page + 1} of {totalPages}
    </span>

    <button
        className="btn btn-secondary"
        onClick={nextPage}
        disabled={page === totalPages - 1 || totalPages === 0}
    >
        Next
    </button>

</div>

{showModal && currentUserRole === "ADMIN" && (

    <div
        className="modal fade show d-block"
        style={{ backgroundColor: "rgba(0,0,0,.5)" }}
    >

        <div className="modal-dialog">

            <div className="modal-content">

                <div className="modal-header">

                    <h5 className="modal-title">
                        Create User
                    </h5>

                    <button
                        className="btn-close"
                        onClick={() => setShowModal(false)}
                    />

                </div>

                <div className="modal-body">

                    <input
                        className="form-control mb-3"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />

                    <input
                        className="form-control mb-3"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />

                    <input
                        type="password"
                        className="form-control mb-3"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <select
                        className="form-select"
                        value={role}
                        onChange={(e) => setRole(e.target.value)}
                    >
                        <option value="USER">USER</option>
                        <option value="ADMIN">ADMIN</option>
                    </select>

                </div>

                <div className="modal-footer">

                    <button
                        className="btn btn-secondary"
                        onClick={() => setShowModal(false)}
                    >
                        Cancel
                    </button>

                    <button
                        className="btn btn-success"
                        onClick={createUser}
                    >
                        Save
                    </button>

                </div>

            </div>

        </div>

    </div>

)}
{showEditModal && currentUserRole === "ADMIN" && (

    <div
        className="modal fade show d-block"
        style={{ backgroundColor: "rgba(0,0,0,.5)" }}
    >

        <div className="modal-dialog">

            <div className="modal-content">

                <div className="modal-header">

                    <h5 className="modal-title">
                        Edit User
                    </h5>

                    <button
                        className="btn-close"
                        onClick={() => setShowEditModal(false)}
                    />

                </div>

                <div className="modal-body">

                    <input
                        className="form-control mb-3"
                        placeholder="Username"
                        value={editUsername}
                        onChange={(e) => setEditUsername(e.target.value)}
                    />

                    <input
                        className="form-control mb-3"
                        placeholder="Email"
                        value={editEmail}
                        onChange={(e) => setEditEmail(e.target.value)}
                    />

                    <select
                        className="form-select"
                        value={editRole}
                        onChange={(e) => setEditRole(e.target.value)}
                    >
                        <option value="USER">USER</option>
                        <option value="ADMIN">ADMIN</option>
                    </select>

                </div>

                <div className="modal-footer">

                    <button
                        className="btn btn-secondary"
                        onClick={() => setShowEditModal(false)}
                    >
                        Cancel
                    </button>

                    <button
                        className="btn btn-warning"
                        onClick={updateUser}
                    >
                        Update
                    </button>

                </div>

            </div>

        </div>

    </div>

)}

                </div>
            </div>
        </div>
    </div>
);

}

export default Dashboard;