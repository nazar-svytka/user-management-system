import axiosClient from "../api/axiosClient";

const getUsers = (
    page = 0,
    size = 5,
    search = "",
    sort = "id"
) => {

    return axiosClient.get("/users", {
        params: {
            page,
            size,
            search,
            sort
        }
    });

};

const createUser = (user) => {
    return axiosClient.post("/users", user);
};

const getUserById = (id) => {
    return axiosClient.get(`/users/${id}`);
};

const updateUser = (id, user) => {
    return axiosClient.put(`/users/${id}`, user);
};

const deleteUser = (id) => {
    return axiosClient.delete(`/users/${id}`);
};

const userService = {
    getUsers,
    createUser,
    getUserById,
    updateUser,
    deleteUser
};

export default userService;