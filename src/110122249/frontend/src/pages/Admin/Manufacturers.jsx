import React, { useState, useEffect } from 'react';
import AdminHeader from '../../components/Admin/AdminHeader';
import { getAllBrands, createBrand, updateBrand, deleteBrand } from '../../api/brand';
import { toast } from 'react-toastify';
import ConfirmModal from '../../components/common/ConfirmModal';

const Manufacturers = () => {
    const [manufacturers, setManufacturers] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [formData, setFormData] = useState({ brandName: '', brandDescription: '' });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [showConfirm, setShowConfirm] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);
    const [confirmMessage, setConfirmMessage] = useState('');

    const fetchBrands = async (keyword = '') => {
        try {
            setLoading(true);
            const response = await getAllBrands({ keyword });
            if (response && response.data) {
                setManufacturers(response.data);
            }
        } catch (err) {
            setError('Không thể tải danh sách nhà sản xuất');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBrands();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editMode) {
                await updateBrand(editingId, formData);
                toast.success('Cập nhật nhà sản xuất thành công!');
            } else {
                await createBrand(formData);
                toast.success('Thêm nhà sản xuất thành công!');
            }
            setFormData({ brandName: '', brandDescription: '' });
            setShowForm(false);
            setEditMode(false);
            setEditingId(null);
            fetchBrands();
        } catch (err) {
            console.error(err);
            toast.error(editMode ? 'Có lỗi xảy ra khi cập nhật nhà sản xuất' : 'Có lỗi xảy ra khi thêm nhà sản xuất');
        }
    };

    const handleEdit = (brand) => {
        setFormData({
            brandName: brand.brandName,
            brandDescription: brand.brandDescription || ''
        });
        setEditingId(brand.brandId);
        setEditMode(true);
        setShowForm(true);
    };

    const handleDelete = (brandId) => {
        setConfirmMessage('Bạn có chắc chắn muốn xóa nhà sản xuất này?');
        setConfirmAction(() => async () => {
            try {
                await deleteBrand(brandId);
                toast.success('Xóa nhà sản xuất thành công!');
                fetchBrands();
            } catch (err) {
                console.error(err);
                // Hiển thị thông báo lỗi từ backend nếu có
                const errorMessage = err.response?.data?.message || 
                                   err.response?.data?.error || 
                                   'Có lỗi xảy ra khi xóa nhà sản xuất';
                toast.error(errorMessage);
            }
        });
        setShowConfirm(true);
    };

    const handleCloseForm = () => {
        setShowForm(false);
        setEditMode(false);
        setEditingId(null);
        setFormData({ brandName: '', brandDescription: '' });
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <AdminHeader title="Quản lý Nhà sản xuất" />
            
            <div className="p-8">
                <div className="sticky top-4 bg-gray-50 z-10 pb-6 flex justify-between items-center">
                    <h2 className="text-xl font-bold text-gray-800">Danh sách Nhà sản xuất</h2>
                    <div className="flex items-center gap-3">
                        <input
                            type="text"
                            placeholder="Tìm kiếm"
                            value={searchKeyword}
                            onChange={(e) => {
                                setSearchKeyword(e.target.value);
                                fetchBrands(e.target.value);
                            }}
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:border-pink-500 w-64"
                        />
                        <button 
                            onClick={() => setShowForm(true)}
                            className="bg-pink-600 text-white px-4 py-2 rounded-lg hover:bg-pink-700 transition-colors"
                        >
                            + Thêm Nhà sản xuất
                        </button>
                    </div>
                </div>

                {/* Form Modal */}
                {showForm && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white rounded-xl p-6 w-full max-w-md">
                            <h3 className="text-lg font-bold mb-4">{editMode ? 'Cập nhật Nhà sản xuất' : 'Thêm Nhà sản xuất mới'}</h3>
                            <form onSubmit={handleSubmit}>
                                <div className="mb-4">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Tên Nhà sản xuất</label>
                                    <input
                                        type="text"
                                        required
                                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-pink-500"
                                        value={formData.brandName}
                                        onChange={(e) => setFormData({...formData, brandName: e.target.value})}
                                    />
                                </div>
                                <div className="mb-6">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
                                    <textarea
                                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-pink-500"
                                        rows="3"
                                        value={formData.brandDescription}
                                        onChange={(e) => setFormData({...formData, brandDescription: e.target.value})}
                                    ></textarea>
                                </div>
                                <div className="flex justify-end gap-3">
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
                        <table className="w-full text-left table-fixed">
                            <thead className="bg-gray-50 border-b border-gray-100">
                                <tr>
                                    <th className="px-6 py-4 font-semibold text-gray-600 w-20">ID</th>
                                    <th className="px-6 py-4 font-semibold text-gray-600 w-32">Mã</th>
                                    <th className="px-6 py-4 font-semibold text-gray-600 w-48">Tên Nhà sản xuất</th>
                                    <th className="px-6 py-4 font-semibold text-gray-600">Mô tả</th>
                                    <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap w-40">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {manufacturers.map((item) => (
                                    <tr key={item.brandId} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 text-gray-800">#{item.brandId}</td>
                                        <td className="px-6 py-4 text-gray-600">{item.brandCode}</td>
                                        <td className="px-6 py-4 font-medium text-gray-800">{item.brandName}</td>
                                        <td className="px-6 py-4 text-gray-600 break-words">{item.brandDescription}</td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <div className="flex items-center gap-2">
                                                <button 
                                                    onClick={() => handleEdit(item)}
                                                    className="bg-pink-600 text-white px-3 py-1 rounded hover:bg-pink-700 transition-colors"
                                                >
                                                    Sửa
                                                </button>
                                                <button 
                                                    onClick={() => handleDelete(item.brandId)}
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

export default Manufacturers;
