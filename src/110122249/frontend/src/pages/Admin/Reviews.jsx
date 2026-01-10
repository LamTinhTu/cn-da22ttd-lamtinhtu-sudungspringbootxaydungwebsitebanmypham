import React, { useState, useEffect } from 'react';
import AdminHeader from '../../components/Admin/AdminHeader';
import { getAllReviews, deleteReview } from '../../api/review';
import { toast } from 'react-toastify';
import ConfirmModal from '../../components/common/ConfirmModal';

const Reviews = () => {
    const [reviews, setReviews] = useState([]);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showConfirm, setShowConfirm] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);
    const [confirmMessage, setConfirmMessage] = useState('');

    const fetchReviews = async (keyword = '') => {
        try {
            setLoading(true);
            const params = { size: 100 };
            if (keyword) {
                params.keyword = keyword;
            }
            const response = await getAllReviews(params);
            console.log('Reviews response:', response);
            if (response && response.data) {
                setReviews(response.data.items || response.data);
            }
        } catch (err) {
            setError('Không thể tải danh sách đánh giá');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchReviews();
    }, []);

    const handleDelete = (reviewId) => {
        setConfirmMessage('Bạn có chắc chắn muốn xóa đánh giá này?');
        setConfirmAction(() => async () => {
            try {
                await deleteReview(reviewId);
                toast.success('Xóa đánh giá thành công!');
                fetchReviews();
            } catch (err) {
                console.error('Delete error:', err);
                const errorMessage = err.response?.data?.message || 'Có lỗi xảy ra khi xóa đánh giá';
                toast.error(errorMessage);
            }
        });
        setShowConfirm(true);
    };

    const getRatingStars = (rating) => {
        return '⭐'.repeat(rating);
    };

    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN');
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <AdminHeader title="Quản lý Bài viết đánh giá" />
            
            <div className="p-8">
                <div className="sticky top-4 bg-gray-50 z-10 pb-6 flex justify-between items-center">
                    <h2 className="text-xl font-bold text-gray-800">Danh sách Đánh giá</h2>
                    <input
                        type="text"
                        placeholder="Tìm kiếm"
                        value={searchKeyword}
                        onChange={(e) => {
                            setSearchKeyword(e.target.value);
                            fetchReviews(e.target.value);
                        }}
                        className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:border-pink-500 w-64"
                    />
                </div>

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
                                        <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap w-24">Mã bài viết</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap w-40">Tài khoản</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600 min-w-[200px]">Sản phẩm</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap w-32">Đánh giá</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600 min-w-[300px]">Nội dung</th>
                                        <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap w-24 text-center">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {reviews.map((review) => (
                                        <tr key={review.reviewId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 text-gray-800 whitespace-nowrap">#{review.reviewId}</td>
                                            <td className="px-6 py-4 text-gray-600 whitespace-nowrap">{review.userName || 'N/A'}</td>
                                            <td className="px-6 py-4 font-medium text-gray-800">{review.productName || 'N/A'}</td>
                                            <td className="px-6 py-4 whitespace-nowrap">
                                                <span className="text-yellow-500">{getRatingStars(review.rating)}</span>
                                                <span className="text-gray-600 text-sm ml-1">({review.rating})</span>
                                            </td>
                                            <td className="px-6 py-4 text-gray-600">
                                                <div className="line-clamp-2" title={review.comment}>
                                                    {review.comment || 'Không có nội dung'}
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 whitespace-nowrap text-center">
                                                <button 
                                                    onClick={() => handleDelete(review.reviewId)}
                                                    className="bg-red-100 text-red-600 px-3 py-1 rounded hover:bg-red-200 transition-colors font-medium text-sm"
                                                >
                                                    Xóa
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
                
                <div className="mt-4 flex items-center justify-between">
                    <p className="text-sm text-gray-700">
                        Hiển thị <span className="font-medium">{reviews.length}</span> kết quả
                    </p>
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

export default Reviews;
