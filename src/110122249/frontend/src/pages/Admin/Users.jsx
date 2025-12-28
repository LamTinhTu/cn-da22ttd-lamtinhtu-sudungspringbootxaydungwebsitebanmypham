import React, { useState, useEffect } from 'react';
import AdminHeader from '../../components/Admin/AdminHeader';
import { getAllUsers, createUser, updateUser, deleteUser } from '../../api/user';
import { toast } from 'react-toastify';
import ConfirmModal from '../../components/common/ConfirmModal';

const Users = () => {
    const [users, setUsers] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);
    const [confirmMessage, setConfirmMessage] = useState('');
    const [searchKeyword, setSearchKeyword] = useState('');
    const [formData, setFormData] = useState({
        userName: '',
        userGender: 'MALE',
        userBirthDate: '',
        userAddress: '',
        userPhone: '',
        userAccount: '',
        userPassword: '',
        roleId: 3 // Default to Customer
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [editingId, setEditingId] = useState(null);

    const fetchUsers = async (keyword = '') => {
        try {
            setLoading(true);
            const response = await getAllUsers({ size: 100, keyword });
            console.log('Users response:', response);
            if (response && response.data && response.data.items) {
                setUsers(response.data.items);
            }
        } catch (err) {
            setError('Không thể tải danh sách người dùng');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            let userData;
            
            if (editMode) {
                // For update: send all fields but keep account/role/password unchanged
                userData = {
                    userName: formData.userName.trim(),
                    userGender: formData.userGender,
                    userBirthDate: formData.userBirthDate || null,
                    userAddress: formData.userAddress?.trim() || '',
                    userPhone: formData.userPhone.trim(),
                    userAccount: formData.userAccount.trim(), // Keep original
                    userPassword: formData.userPassword || 'UNCHANGED_PASSWORD_PLACEHOLDER', // Keep original
                    roleId: parseInt(formData.roleId) // Keep original
                };
            } else {
                // Include all fields for new user creation
                userData = {
                    userName: formData.userName.trim(),
                    userGender: formData.userGender,
                    userBirthDate: formData.userBirthDate || null,
                    userAddress: formData.userAddress?.trim() || '',
                    userPhone: formData.userPhone.trim(),
                    userAccount: formData.userAccount.trim(),
                    userPassword: formData.userPassword.trim(),
                    roleId: parseInt(formData.roleId)
                };
            }

            console.log('Submitting user data:', userData);
            console.log('Edit mode:', editMode);

            if (editMode) {
                await updateUser(editingId, userData);
                toast.success('Cập nhật người dùng thành công!');
            } else {
                await createUser(userData);
                toast.success('Thêm người dùng thành công!');
            }
            handleCloseForm();
            fetchUsers();
        } catch (err) {
            console.error('Submit error:', err);
            console.error('Error response full:', err.response);
            console.error('Error response data:', err.response?.data);
            const errorMessage = err.response?.data?.message || 
                               err.response?.data?.error || 
                               (editMode ? 'Có lỗi xảy ra khi cập nhật người dùng' : 'Có lỗi xảy ra khi thêm người dùng');
            toast.error(errorMessage);
        }
    };

    const handleEdit = (user) => {
        console.log('Editing user:', user);
        setFormData({
            userName: user.userName,
            userGender: user.userGender || 'MALE',
            userBirthDate: user.userBirthDate || '',
            userAddress: user.userAddress || '',
            userPhone: user.userPhone,
            userAccount: user.userAccount,
            userPassword: '', // Don't pre-fill password
            roleId: user.roleId
        });
        setEditingId(user.userId);
        setEditMode(true);
        setShowForm(true);
    };

    const getGenderText = (gender) => {
        switch(gender) {
            case 'MALE': return 'Nam';
            case 'FEMALE': return 'Nữ';
            case 'OTHER': return 'Khác';
            default: return gender;
        }
    };

    const handleDelete = (userId) => {
        setConfirmMessage('Bạn có chắc chắn muốn xóa người dùng này?');
        setConfirmAction(() => async () => {
            try {
                await deleteUser(userId);
                toast.success('Xóa người dùng thành công!');
                fetchUsers();
            } catch (err) {
                console.error('Delete error:', err);
                console.error('Error response:', err.response?.data);
                const errorMessage = err.response?.data?.message || 
                                   err.response?.data?.error || 
                                   'Có lỗi xảy ra khi xóa người dùng';
                toast.error(errorMessage);
            }
        });
        setShowConfirm(true);
    };

    const handleCloseForm = () => {
        setShowForm(false);
        setEditMode(false);
        setEditingId(null);
        setFormData({
            userName: '',
            userGender: 'MALE',
            userBirthDate: '',
            userAddress: '',
            userPhone: '',
            userAccount: '',
            userPassword: '',
            roleId: 3
        });
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <AdminHeader title="Quản lý Tài khoản người dùng" />
            
            <div className="p-8">
                <div className="sticky top-4 bg-gray-50 z-10 pb-6 flex justify-between items-center">
                    <h2 className="text-xl font-bold text-gray-800">Danh sách Người dùng</h2>
                    <div className="flex items-center gap-3">
                        <input
                            type="text"
                            placeholder="Tìm kiếm"
                            value={searchKeyword}
                            onChange={(e) => {
                                setSearchKeyword(e.target.value);
                                fetchUsers(e.target.value);
                            }}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:border-pink-500 w-64"
                        />
                        <button 
                            onClick={() => setShowForm(true)}
                            className="bg-pink-600 text-white px-4 py-2 rounded-lg hover:bg-pink-700 transition-colors"
                        >
                            + Thêm Người dùng
                        </button>
                    </div>
                </div>

                {/* Form Modal */}
                {showForm && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                            <h3 className="text-lg font-bold mb-4">{editMode ? 'Cập nhật Người dùng' : 'Thêm Người dùng mới'}</h3>
                            <form onSubmit={handleSubmit}>
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Họ và tên</label>
                                        <input
                                            type="text"
                                            required
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.userName}
                                            onChange={(e) => setFormData({...formData, userName: e.target.value})}
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Giới tính</label>
                                        <select
                                            required
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.userGender}
                                            onChange={(e) => setFormData({...formData, userGender: e.target.value})}
                                        >
                                            <option value="MALE">Nam</option>
                                            <option value="FEMALE">Nữ</option>
                                            <option value="OTHER">Khác</option>
                                        </select>
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Ngày sinh</label>
                                        <input
                                            type="date"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.userBirthDate}
                                            onChange={(e) => setFormData({...formData, userBirthDate: e.target.value})}
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Số điện thoại</label>
                                        <input
                                            type="tel"
                                            required
                                            pattern="[0-9]{10}"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.userPhone}
                                            onChange={(e) => setFormData({...formData, userPhone: e.target.value})}
                                        />
                                    </div>
                                    <div className="mb-4 col-span-2">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
                                        <input
                                            type="text"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.userAddress}
                                            onChange={(e) => setFormData({...formData, userAddress: e.target.value})}
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Tài khoản</label>
                                        <input
                                            type="text"
                                            required
                                            disabled={editMode}
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500 disabled:bg-gray-100"
                                            value={formData.userAccount}
                                            onChange={(e) => setFormData({...formData, userAccount: e.target.value})}
                                        />
                                    </div>
                                    {!editMode && (
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium text-gray-700 mb-1">Mật khẩu</label>
                                            <input
                                                type="password"
                                                required
                                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                                value={formData.userPassword}
                                                onChange={(e) => setFormData({...formData, userPassword: e.target.value})}
                                            />
                                        </div>
                                    )}
                                    {!editMode && (
                                        <div className="mb-4 col-span-2">
                                            <label className="block text-sm font-medium text-gray-700 mb-1">Vai trò</label>
                                            <select
                                                required
                                                className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                                value={formData.roleId}
                                                onChange={(e) => setFormData({...formData, roleId: e.target.value})}
                                            >
                                                <option value={1}>Administrator</option>
                                                <option value={2}>Staff</option>
                                                <option value={3}>Customer</option>
                                            </select>
                                        </div>
                                    )}
                                </div>
                                <div className="flex justify-end gap-3 mt-4">
                                    <button
                                        type="button"
                                        onClick={handleCloseForm}
                                        className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
                                    >
                                        Hủy
                                    </button>
                                    <button
                                        type="submit"
                                        className="px-4 py-2 bg-pink-600 text-white rounded-lg hover:bg-pink-700"
                                    >
                                        {editMode ? 'Cập nhật' : 'Lưu'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}

                {/* Table */}
                <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                    {loading ? (
                        <div className="p-6 text-center text-gray-500">Đang tải...</div>
                    ) : error ? (
                        <div className="p-6 text-center text-red-500">{error}</div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-left">
                                <thead className="bg-gray-50 border-b border-gray-100">
                                    <tr>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Mã KH</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Họ tên</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Giới tính</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Số điện thoại</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Tài khoản</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600">Vai trò</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {users.map((user) => (
                                        <tr key={user.userId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 text-gray-800">{user.userCode}</td>
                                            <td className="px-6 py-4 font-medium text-gray-800">{user.userName}</td>
                                            <td className="px-6 py-4 text-gray-600">{getGenderText(user.userGender)}</td>
                                            <td className="px-6 py-4 text-gray-600">{user.userPhone}</td>
                                            <td className="px-6 py-4 text-gray-600">{user.userAccount}</td>
                                            <td className="px-6 py-4">
                                                <span className={`px-2 py-1 rounded-full text-xs ${
                                                    user.roleName === 'Administrator' ? 'bg-red-100 text-red-700' :
                                                    user.roleName === 'Staff' ? 'bg-blue-100 text-blue-700' :
                                                    'bg-green-100 text-green-700'
                                                }`}>
                                                    {user.roleName === 'Administrator' ? 'Quản trị viên' :
                                                     user.roleName === 'Staff' ? 'Nhân viên' :
                                                     'Khách hàng'}
                                                </span>
                                            </td>
                                            <td className="px-6 py-4">
                                                <div className="flex items-center gap-2">
                                                    <button 
                                                        onClick={() => handleEdit(user)}
                                                        className="bg-pink-600 text-white px-3 py-1 rounded hover:bg-pink-700 transition-colors"
                                                    >
                                                        Sửa
                                                    </button>
                                                    <button 
                                                        onClick={() => handleDelete(user.userId)}
                                                        className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700 transition-colors"
                                                    >
                                                        Xóa
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
            <ConfirmModal
                isOpen={showConfirm}
                onClose={() => setShowConfirm(false)}
                onConfirm={confirmAction}
                title="Xác nhận xóa"
                message={confirmMessage}
            />
        </div>
    );
};

export default Users;
