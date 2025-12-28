import React, { useMemo, useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { addToCart } from '../../store/features/cartSlice';
import { toast } from 'react-toastify';
import { CartIcon } from '../../components/common/CartIcon';
import SvgFavourite from '../../components/common/SvgFavourite';
import { getProductById } from '../../api/fetchProductDetail';
import { getReviewsByProduct, getProductRating, createReview, updateReview, deleteReview } from '../../api/review';
import { API_BASE_URL } from '../../api/constant';
import { getUser } from '../../utils/jwt-helper';
import ConfirmModal from '../../components/common/ConfirmModal';

const ProductDetails = () => {
    const { productId } = useParams();
    const dispatch = useDispatch();
    const [selectedImage, setSelectedImage] = useState(0);
    const [quantity, setQuantity] = useState(1);
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [reviews, setReviews] = useState([]);
    const [rating, setRating] = useState({ averageRating: 0, reviewCount: 0 });
    const [newReview, setNewReview] = useState({ rating: 5, comment: '' });
    const [showReviewForm, setShowReviewForm] = useState(false);
    const [editingReviewId, setEditingReviewId] = useState(null);
    const [editReview, setEditReview] = useState({ rating: 5, comment: '' });
    const [showConfirm, setShowConfirm] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);
    const [confirmMessage, setConfirmMessage] = useState('');
    const user = getUser();

    useEffect(() => {
        const fetchProductDetail = async () => {
            try {
                setLoading(true);
                setError(null);
                const response = await getProductById(productId);
                
                if (response.status === 200 && response.data) {
                    setProduct(response.data);
                    
                    // Fetch reviews and rating
                    const reviewsResponse = await getReviewsByProduct(productId);
                    if (reviewsResponse.status === 200) {
                        setReviews(reviewsResponse.data || []);
                    }
                    
                    const ratingResponse = await getProductRating(productId);
                    if (ratingResponse.status === 200) {
                        setRating(ratingResponse.data || { averageRating: 0, reviewCount: 0 });
                    }
                } else {
                    setError('Không thể tải thông tin sản phẩm');
                }
            } catch (err) {
                console.error('Error fetching product:', err);
                setError('Sản phẩm không tồn tại hoặc đã xảy ra lỗi');
            } finally {
                setLoading(false);
            }
        };

        if (productId) {
            fetchProductDetail();
        }
    }, [productId]);

    if (loading) {
        return (
            <div className="container mx-auto px-4 py-16 text-center">
                <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900"></div>
                <p className="mt-4 text-gray-600">Đang tải...</p>
            </div>
        );
    }

    if (error || !product) {
        return (
            <div className="container mx-auto px-4 py-16 text-center">
                <h2 className="text-2xl font-bold mb-4">{error || 'Sản phẩm không tồn tại'}</h2>
                <Link to="/" className="text-blue-600 hover:underline">Quay lại trang chủ</Link>
            </div>
        );
    }

    const handleQuantityChange = (val) => {
        if (val < 1) return;
        if (val > product.quantityStock) {
            toast.warning(`Chỉ còn ${product.quantityStock} sản phẩm trong kho`);
            return;
        }
        setQuantity(val);
    };

    // Xử lý image URLs từ backend
    const productImages = product.images?.map(img => {
        const imageUrl = img.imageURL || img.imageUrl;
        return imageUrl?.startsWith('http') 
            ? imageUrl 
            : `${API_BASE_URL}${imageUrl}`;
    }) || [];

    const mainImage = productImages.length > 0 
        ? productImages[selectedImage] 
        : `${API_BASE_URL}/uploads/placeholder.jpg`;

    const handleAddToCart = () => {
        if (!product) return;
        
        const cartItem = {
            id: product.productId,
            name: product.productName,
            price: product.productPrice,
            quantity: quantity,
            image: mainImage,
        };
        
        dispatch(addToCart(cartItem));
        toast.success(`Đã thêm ${quantity} sản phẩm vào giỏ hàng`);
    };

    const renderStars = (rating) => {
        const stars = [];
        for (let i = 1; i <= 5; i++) {
            stars.push(
                <span key={i} className={i <= rating ? 'text-yellow-500' : 'text-gray-300'}>
                    ★
                </span>
            );
        }
        return stars;
    };

    const handleSubmitReview = async (e) => {
        e.preventDefault();
        if (!user) {
            toast.error('Vui lòng đăng nhập để đánh giá');
            return;
        }

        try {
            const response = await createReview({
                productId: parseInt(productId),
                rating: newReview.rating,
                comment: newReview.comment
            });

            if (response.status === 200 || response.status === 201) {
                toast.success('Đánh giá thành công!');
                setNewReview({ rating: 5, comment: '' });
                setShowReviewForm(false);
                
                // Refresh reviews and rating
                const reviewsResponse = await getReviewsByProduct(productId);
                if (reviewsResponse.status === 200) {
                    setReviews(reviewsResponse.data || []);
                }
                
                const ratingResponse = await getProductRating(productId);
                if (ratingResponse.status === 200) {
                    setRating(ratingResponse.data || { averageRating: 0, reviewCount: 0 });
                }
            }
        } catch (error) {
            console.error('Error submitting review:', error);
            toast.error(error.response?.data?.message || 'Không thể gửi đánh giá');
        }
    };

    const handleEditReview = async (reviewId) => {
        try {
            const response = await updateReview(reviewId, {
                productId: parseInt(productId),
                rating: editReview.rating,
                comment: editReview.comment
            });

            if (response.status === 200) {
                toast.success('Cập nhật đánh giá thành công!');
                setEditingReviewId(null);
                
                // Refresh reviews
                const reviewsResponse = await getReviewsByProduct(productId);
                if (reviewsResponse.status === 200) {
                    setReviews(reviewsResponse.data || []);
                }
                
                const ratingResponse = await getProductRating(productId);
                if (ratingResponse.status === 200) {
                    setRating(ratingResponse.data || { averageRating: 0, reviewCount: 0 });
                }
            }
        } catch (error) {
            console.error('Error updating review:', error);
            toast.error(error.response?.data?.message || 'Không thể cập nhật đánh giá');
        }
    };

    const handleDeleteReview = (reviewId) => {
        setConfirmMessage('Bạn có chắc muốn xóa đánh giá này?');
        setConfirmAction(() => async () => {
            try {
                const response = await deleteReview(reviewId);
                if (response.status === 200) {
                    toast.success('Xóa đánh giá thành công!');
                    
                    // Refresh reviews
                    const reviewsResponse = await getReviewsByProduct(productId);
                    if (reviewsResponse.status === 200) {
                        setReviews(reviewsResponse.data || []);
                    }
                    
                    const ratingResponse = await getProductRating(productId);
                    if (ratingResponse.status === 200) {
                        setRating(ratingResponse.data || { averageRating: 0, reviewCount: 0 });
                    }
                }
            } catch (error) {
                console.error('Error deleting review:', error);
                toast.error(error.response?.data?.message || 'Không thể xóa đánh giá');
            }
        });
        setShowConfirm(true);
    };

    const startEditReview = (review) => {
        setEditingReviewId(review.reviewId);
        setEditReview({
            rating: review.rating,
            comment: review.comment
        });
    };

    return (
        <div className="container mx-auto px-4 py-8 font-sans">
            <div className="flex flex-col md:flex-row gap-8">
                {/* Image Gallery */}
                <div className="w-full md:w-1/2">
                    <div className="mb-4 bg-white rounded-lg border border-gray-100 overflow-hidden">
                        <img 
                            src={mainImage} 
                            alt={product.productName} 
                            className="w-full h-[500px] object-contain"
                            onError={(e) => {
                                e.target.src = 'https://via.placeholder.com/500x500?text=No+Image';
                            }}
                        />
                    </div>
                    {productImages.length > 1 && (
                        <div className="flex gap-4 overflow-x-auto pb-2">
                            {productImages.map((img, index) => (
                                <img 
                                    key={index}
                                    src={img}
                                    alt={`${product.productName} ${index + 1}`}
                                    className={`w-20 h-20 object-contain bg-white rounded-md cursor-pointer border-2 ${selectedImage === index ? 'border-black' : 'border-transparent'}`}
                                    onClick={() => setSelectedImage(index)}
                                    onError={(e) => {
                                        e.target.src = 'https://via.placeholder.com/80x80?text=No+Image';
                                    }}
                                />
                            ))}
                        </div>
                    )}
                </div>

                {/* Product Info */}
                <div className="w-full md:w-1/2">
                    <div className="mb-2">
                        <span className="text-sm text-gray-500 uppercase tracking-wider">
                            {product.brandName || 'Không có thương hiệu'}
                        </span>
                    </div>
                    <h1 className="text-3xl font-bold text-gray-900 mb-4">{product.productName}</h1>
                    <div className="flex items-center gap-4 mb-6">
                        <span className="text-2xl font-bold text-black">
                            {product.productPrice?.toLocaleString('vi-VN')} VND
                        </span>
                        <div className="flex items-center gap-1">
                            <div className="flex text-lg">
                                {renderStars(Math.round(rating.averageRating))}
                            </div>
                            <span className="text-gray-400 text-sm ml-2">
                                ({rating.averageRating ? rating.averageRating.toFixed(1) : '0.0'} - {rating.reviewCount} đánh giá)
                            </span>
                        </div>
                    </div>

                    <p className="text-gray-600 mb-4 leading-relaxed">
                        {product.productDescription || 'Không có mô tả'}
                    </p>

                    <div className="mb-6">
                        <div className="flex items-center gap-2">
                            <span className="text-sm text-gray-600">Mã sản phẩm:</span>
                            <span className="text-sm font-medium">{product.productCode}</span>
                        </div>
                        <div className="flex items-center gap-2 mt-2">
                            <span className="text-sm text-gray-600">Tình trạng:</span>
                            <span className={`text-sm font-medium ${product.quantityStock > 0 ? 'text-green-600' : 'text-red-600'}`}>
                                {product.quantityStock > 0 ? `Còn ${product.quantityStock} sản phẩm` : 'Hết hàng'}
                            </span>
                        </div>
                        <div className="flex items-center gap-2 mt-2">
                            <span className="text-sm text-gray-600">Trạng thái:</span>
                            <span className={`text-sm font-medium px-2 py-1 rounded ${
                                product.productStatus === 'SELLING' ? 'bg-green-100 text-green-700' :
                                product.productStatus === 'OUT_OF_STOCK' ? 'bg-yellow-100 text-yellow-700' :
                                product.productStatus === 'NOT_SOLD' ? 'bg-gray-100 text-gray-700' :
                                'bg-red-100 text-red-700'
                            }`}>
                                {product.productStatus === 'SELLING' ? 'Đang bán' :
                                 product.productStatus === 'OUT_OF_STOCK' ? 'Hết hàng' :
                                 product.productStatus === 'NOT_SOLD' ? 'Chưa bán' :
                                 'Ngừng kinh doanh'}
                            </span>
                        </div>
                    </div>

                    <div className="mb-8">
                        <label className="block text-sm font-medium text-gray-700 mb-2">Số lượng</label>
                        <div className="flex items-center gap-4">
                            <div className="flex items-center border border-gray-300 rounded-lg">
                                <button 
                                    onClick={() => handleQuantityChange(quantity - 1)}
                                    className="px-4 py-2 hover:bg-gray-100 transition-colors"
                                    disabled={quantity <= 1}
                                >
                                    -
                                </button>
                                <span className="px-4 py-2 font-medium min-w-[3rem] text-center">{quantity}</span>
                                <button 
                                    onClick={() => handleQuantityChange(quantity + 1)}
                                    className="px-4 py-2 hover:bg-gray-100 transition-colors"
                                    disabled={quantity >= product.quantityStock}
                                >
                                    +
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="flex gap-4 mb-8">
                        <button 
                            onClick={handleAddToCart}
                            className="flex-1 bg-black text-white py-4 rounded-lg font-bold hover:bg-gray-800 transition-colors flex items-center justify-center gap-2 disabled:bg-gray-400 disabled:cursor-not-allowed"
                            disabled={product.quantityStock === 0 || product.productStatus !== 'SELLING'}
                        >
                            <CartIcon className="w-5 h-5 fill-current" />
                            {product.quantityStock === 0 ? 'Hết hàng' : 'Thêm vào giỏ hàng'}
                        </button>
                        <button className="w-14 h-14 border border-gray-300 rounded-lg flex items-center justify-center hover:bg-gray-50 transition-colors">
                            <SvgFavourite />
                        </button>
                    </div>

                    <div className="border-t border-gray-100 pt-6 space-y-4">
                        <div className="flex gap-4">
                            <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">🚚</div>
                            <div>
                                <h4 className="font-bold text-sm">Miễn phí vận chuyển</h4>
                                <p className="text-sm text-gray-500">Cho đơn hàng trên 500k</p>
                            </div>
                        </div>
                        <div className="flex gap-4">
                            <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center">↩️</div>
                            <div>
                                <h4 className="font-bold text-sm">Đổi trả dễ dàng</h4>
                                <p className="text-sm text-gray-500">Trong vòng 30 ngày</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Reviews Section */}
            <div className="mt-16 border-t border-gray-200 pt-12">
                <h2 className="text-2xl font-bold mb-8">Đánh giá sản phẩm</h2>
                
                {/* Rating Summary */}
                <div className="bg-gray-50 rounded-lg p-6 mb-8">
                    <div className="flex items-center gap-8">
                        <div className="text-center">
                            <div className="text-5xl font-bold text-gray-900">
                                {rating.averageRating ? rating.averageRating.toFixed(1) : '0.0'}
                            </div>
                            <div className="flex justify-center text-2xl mt-2">
                                {renderStars(Math.round(rating.averageRating))}
                            </div>
                            <div className="text-sm text-gray-500 mt-2">
                                {rating.reviewCount} đánh giá
                            </div>
                        </div>
                    </div>
                </div>

                {/* Write Review Button */}
                {user && (
                    <div className="mb-8">
                        {!showReviewForm ? (
                            <button
                                onClick={() => setShowReviewForm(true)}
                                className="bg-black text-white px-6 py-3 rounded-lg font-medium hover:bg-gray-800 transition-colors"
                            >
                                Viết đánh giá
                            </button>
                        ) : (
                            <div className="bg-white border border-gray-200 rounded-lg p-6">
                                <h3 className="text-lg font-bold mb-4">Đánh giá của bạn</h3>
                                <form onSubmit={handleSubmitReview}>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Đánh giá
                                        </label>
                                        <div className="flex gap-2">
                                            {[1, 2, 3, 4, 5].map((star) => (
                                                <button
                                                    key={star}
                                                    type="button"
                                                    onClick={() => setNewReview({ ...newReview, rating: star })}
                                                    className="text-3xl focus:outline-none"
                                                >
                                                    <span className={star <= newReview.rating ? 'text-yellow-500' : 'text-gray-300'}>
                                                        ★
                                                    </span>
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-2">
                                            Nhận xét
                                        </label>
                                        <textarea
                                            value={newReview.comment}
                                            onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-black focus:border-transparent"
                                            rows="4"
                                            placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm..."
                                        />
                                    </div>
                                    <div className="flex gap-3">
                                        <button
                                            type="submit"
                                            className="bg-black text-white px-6 py-2 rounded-lg font-medium hover:bg-gray-800 transition-colors"
                                        >
                                            Gửi đánh giá
                                        </button>
                                        <button
                                            type="button"
                                            onClick={() => {
                                                setShowReviewForm(false);
                                                setNewReview({ rating: 5, comment: '' });
                                            }}
                                            className="bg-gray-200 text-gray-700 px-6 py-2 rounded-lg font-medium hover:bg-gray-300 transition-colors"
                                        >
                                            Hủy
                                        </button>
                                    </div>
                                </form>
                            </div>
                        )}
                    </div>
                )}

                {/* Reviews List */}
                <div className="space-y-6">
                    {reviews.length === 0 ? (
                        <div className="text-center py-12 text-gray-500">
                            <p className="text-lg">Chưa có đánh giá nào</p>
                            <p className="text-sm mt-2">Hãy là người đầu tiên đánh giá sản phẩm này!</p>
                        </div>
                    ) : (
                        reviews.map((review) => (
                            <div key={review.reviewId} className="bg-white border border-gray-200 rounded-lg p-6">
                                {editingReviewId === review.reviewId ? (
                                    // Edit Mode
                                    <div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                                Đánh giá
                                            </label>
                                            <div className="flex gap-2">
                                                {[1, 2, 3, 4, 5].map((star) => (
                                                    <button
                                                        key={star}
                                                        type="button"
                                                        onClick={() => setEditReview({ ...editReview, rating: star })}
                                                        className="text-3xl focus:outline-none"
                                                    >
                                                        <span className={star <= editReview.rating ? 'text-yellow-500' : 'text-gray-300'}>
                                                            ★
                                                        </span>
                                                    </button>
                                                ))}
                                            </div>
                                        </div>
                                        <div className="mb-4">
                                            <label className="block text-sm font-medium text-gray-700 mb-2">
                                                Nhận xét
                                            </label>
                                            <textarea
                                                value={editReview.comment}
                                                onChange={(e) => setEditReview({ ...editReview, comment: e.target.value })}
                                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-black focus:border-transparent"
                                                rows="4"
                                            />
                                        </div>
                                        <div className="flex gap-3">
                                            <button
                                                onClick={() => handleEditReview(review.reviewId)}
                                                className="bg-black text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-gray-800 transition-colors"
                                            >
                                                Lưu
                                            </button>
                                            <button
                                                onClick={() => setEditingReviewId(null)}
                                                className="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg text-sm font-medium hover:bg-gray-300 transition-colors"
                                            >
                                                Hủy
                                            </button>
                                        </div>
                                    </div>
                                ) : (
                                    // View Mode
                                    <div>
                                        <div className="flex items-start justify-between mb-2">
                                            <div>
                                                <div className="font-medium text-gray-900">{review.userName}</div>
                                                <div className="flex items-center gap-2 mt-1">
                                                    <div className="flex text-lg">
                                                        {renderStars(review.rating)}
                                                    </div>
                                                    <span className="text-sm text-gray-500">
                                                        {new Date(review.createdAt).toLocaleDateString('vi-VN')}
                                                    </span>
                                                </div>
                                            </div>
                                            {user && user.userId === review.userId && (
                                                <div className="flex gap-2">
                                                    <button
                                                        onClick={() => startEditReview(review)}
                                                        className="text-sm text-blue-600 hover:text-blue-800"
                                                    >
                                                        Sửa
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteReview(review.reviewId)}
                                                        className="text-sm text-red-600 hover:text-red-800"
                                                    >
                                                        Xóa
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                        {review.comment && (
                                            <p className="text-gray-700 mt-3 leading-relaxed">
                                                {review.comment}
                                            </p>
                                        )}
                                    </div>
                                )}
                            </div>
                        ))
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

export default ProductDetails;
