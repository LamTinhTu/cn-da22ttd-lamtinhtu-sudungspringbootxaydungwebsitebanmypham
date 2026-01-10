import React, { useState, useEffect } from 'react';
import AdminHeader from '../../components/Admin/AdminHeader';
import { getAllProducts, createProduct, updateProduct, deleteProduct } from '../../api/product';
import { getAllBrands } from '../../api/brand';
import { API_BASE_URL, getHeaders } from '../../api/constant';
import { toast } from 'react-toastify';
import ConfirmModal from '../../components/common/ConfirmModal';

const Products = () => {
    const [products, setProducts] = useState([]);
    const [brands, setBrands] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [showConfirm, setShowConfirm] = useState(false);
    const [confirmAction, setConfirmAction] = useState(null);
    const [confirmMessage, setConfirmMessage] = useState('');
    const [formData, setFormData] = useState({
        productName: '',
        brandId: '',
        productDescription: '',
        productPrice: '',
        quantityStock: 0,
        productStatus: 'SELLING',
        productCategory: ''
    });
    const [imageFile, setImageFile] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [detailImages, setDetailImages] = useState([]);
    const [detailImagesPreview, setDetailImagesPreview] = useState([]);
    const [currentImages, setCurrentImages] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [editMode, setEditMode] = useState(false);
    const [editingId, setEditingId] = useState(null);
    const [searchKeyword, setSearchKeyword] = useState('');

    const fetchProducts = async (keyword = '') => {
        try {
            setLoading(true);
            const params = { size: 100 };
            if (keyword) {
                params.keyword = keyword;
            }
            const response = await getAllProducts(params);
            console.log('Products response:', response);
            if (response && response.data && response.data.items) {
                setProducts(response.data.items);
            }
        } catch (err) {
            setError('Không thể tải danh sách sản phẩm');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const fetchBrands = async () => {
        try {
            console.log('Fetching brands...');
            const response = await getAllBrands();
            console.log('Brands response:', response);
            if (response && response.data) {
                setBrands(response.data);
                console.log('Brands set:', response.data);
            }
        } catch (err) {
            console.error('Không thể tải danh sách thương hiệu:', err);
        }
    };

    useEffect(() => {
        fetchProducts();
        fetchBrands();
    }, []);

    // Refresh brands when page becomes visible
    useEffect(() => {
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                fetchBrands();
            }
        };
        document.addEventListener('visibilitychange', handleVisibilityChange);
        return () => {
            document.removeEventListener('visibilitychange', handleVisibilityChange);
        };
    }, []);

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        console.log('Image file selected:', file);
        if (file) {
            setImageFile(file);
            const reader = new FileReader();
            reader.onloadend = () => {
                console.log('Image preview loaded');
                setImagePreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleDetailImagesChange = (e) => {
        const files = Array.from(e.target.files);
        if (files.length > 0) {
            setDetailImages([...detailImages, ...files]);
            
            const newPreviews = [];
            files.forEach(file => {
                const reader = new FileReader();
                reader.onloadend = () => {
                    setDetailImagesPreview(prev => [...prev, reader.result]);
                };
                reader.readAsDataURL(file);
            });
        }
    };

    const removeDetailImage = (index) => {
        const newImages = [...detailImages];
        newImages.splice(index, 1);
        setDetailImages(newImages);

        const newPreviews = [...detailImagesPreview];
        newPreviews.splice(index, 1);
        setDetailImagesPreview(newPreviews);
    };

    const handleDeleteImage = async (imageId) => {
        if (window.confirm('Bạn có chắc chắn muốn xóa ảnh này?')) {
            try {
                const headers = getHeaders();
                const response = await fetch(`${API_BASE_URL}/api/v1/images/${imageId}`, {
                    method: 'DELETE',
                    headers: headers
                });
                
                if (response.ok) {
                    setCurrentImages(currentImages.filter(img => img.imageId !== imageId));
                    toast.success('Xóa ảnh thành công');
                } else {
                    toast.error('Không thể xóa ảnh');
                }
            } catch (error) {
                console.error('Error deleting image:', error);
                toast.error('Lỗi khi xóa ảnh');
            }
        }
    };

    const uploadProductImage = async (productId, file) => {
        if (!file) {
            console.log('No image file to upload');
            return null;
        }
        
        console.log('Uploading image for product:', productId);
        const headers = getHeaders();
        
        const formData = new FormData();
        formData.append('file', file);
        formData.append('productId', productId);
        
        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/images`, {
                method: 'POST',
                headers: headers,
                body: formData
            });
            
            if (!response.ok) {
                throw new Error(`Upload failed with status ${response.status}`);
            }
            
            const result = await response.json();
            return result;
        } catch (error) {
            console.error('Error uploading image:', error);
            toast.error('Lỗi khi upload ảnh: ' + error.message);
            return null;
        }
    };

    const deleteProductImages = async (productId) => {
        const headers = getHeaders();
        try {
            const response = await fetch(`${API_BASE_URL}/api/v1/images/product/${productId}`, {
                method: 'DELETE',
                headers: headers
            });
            if (!response.ok) {
                throw new Error('Failed to delete old images');
            }
            return true;
        } catch (error) {
            console.error('Error deleting images:', error);
            return false;
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const productData = {
                productName: formData.productName.trim(),
                productDescription: formData.productDescription?.trim() || '',
                productPrice: parseFloat(formData.productPrice),
                quantityStock: parseInt(formData.quantityStock),
                brandId: parseInt(formData.brandId),
                productStatus: formData.productStatus,
                productCategory: formData.productCategory || null
            };

            console.log('Submitting product data:', productData);

            if (editMode) {
                await updateProduct(editingId, productData);
                
                // Upload main image if changed
                if (imageFile) {
                    await uploadProductImage(editingId, imageFile);
                }
                
                // Upload detail images
                if (detailImages.length > 0) {
                    for (const file of detailImages) {
                        await uploadProductImage(editingId, file);
                    }
                }
                
                toast.success('Cập nhật sản phẩm thành công!');
            } else {
                const result = await createProduct(productData);
                if (result && result.data && result.data.productId) {
                    const newProductId = result.data.productId;
                    
                    // Upload main image
                    if (imageFile) {
                        await uploadProductImage(newProductId, imageFile);
                    }
                    
                    // Upload detail images
                    if (detailImages.length > 0) {
                        for (const file of detailImages) {
                            await uploadProductImage(newProductId, file);
                        }
                    }
                }
                toast.success('Thêm sản phẩm thành công!');
            }
            handleCloseForm();
            fetchProducts();
        } catch (err) {
            console.error(err);
            console.error('Error response:', err.response?.data);
            const errorMessage = err.response?.data?.message || 
                               err.response?.data?.error || 
                               (editMode ? 'Có lỗi xảy ra khi cập nhật sản phẩm' : 'Có lỗi xảy ra khi thêm sản phẩm');
            toast.error(errorMessage);
        }
    };

    const handleEdit = (product) => {
        console.log('Editing product:', product);
        console.log('Product status from backend:', product.productStatus);
        fetchBrands(); // Ensure brands are loaded
        
        // Validate productStatus - ensure it's one of the valid values
        const validStatuses = ['NOT_SOLD', 'SELLING', 'OUT_OF_STOCK', 'DISCONTINUED'];
        const productStatus = validStatuses.includes(product.productStatus) 
            ? product.productStatus 
            : 'SELLING';
        
        console.log('Setting productStatus to:', productStatus);
        
        setFormData({
            productName: product.productName,
            productDescription: product.productDescription || '',
            productPrice: product.productPrice.toString(),
            quantityStock: product.quantityStock,
            brandId: product.brandId?.toString() || '',
            productStatus: productStatus,
            productCategory: product.productCategory || ''
        });
        setImageFile(null);
        setImagePreview(product.images?.[0]?.imageURL ? `${API_BASE_URL}${product.images[0].imageURL}` : null);
        setCurrentImages(product.images || []);
        setDetailImages([]);
        setDetailImagesPreview([]);
        setEditingId(product.productId);
        setEditMode(true);
        setShowForm(true);
    };

    const handleDelete = (productId) => {
        setConfirmMessage('Bạn có chắc chắn muốn xóa sản phẩm này?');
        setConfirmAction(() => async () => {
            try {
                await deleteProduct(productId);
                toast.success('Xóa sản phẩm thành công!');
                fetchProducts();
            } catch (err) {
                console.error('Delete error:', err);
                console.error('Error response full:', err.response);
                console.error('Error response data:', err.response?.data);
                console.error('Error response status:', err.response?.status);
                const errorMessage = err.response?.data?.message || 
                                   err.response?.data?.error || 
                                   'Có lỗi xảy ra khi xóa sản phẩm';
                toast.error(errorMessage);
            }
        });
        setShowConfirm(true);
    };

    const handleCloseForm = () => {
        setShowForm(false);
        setEditMode(false);
        setEditingId(null);
        setImageFile(null);
        setImagePreview(null);
        setDetailImages([]);
        setDetailImagesPreview([]);
        setCurrentImages([]);
        setFormData({
            productName: '',
            brandId: '',
            productDescription: '',
            productPrice: '',
            quantityStock: 0,
            productStatus: 'SELLING',
            productCategory: ''
        });
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <AdminHeader title="Quản lý Sản phẩm" />
            
            <div className="p-8">
                <div className="sticky top-4 bg-gray-50 z-10 pb-6 flex justify-between items-center">
                    <h2 className="text-xl font-bold text-gray-800">Danh sách Sản phẩm</h2>
                    <div className="flex gap-3">
                        <input
                            type="text"
                            placeholder="Tìm kiếm sản phẩm..."
                            className="border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:border-pink-500"
                            value={searchKeyword}
                            onChange={(e) => {
                                setSearchKeyword(e.target.value);
                                fetchProducts(e.target.value);
                            }}
                        />
                        <button 
                            onClick={() => {
                                setShowForm(true);
                                fetchBrands(); // Refresh brands when opening form
                            }}
                            className="bg-pink-600 text-white px-4 py-2 rounded-lg hover:bg-pink-700 transition-colors"
                        >
                            + Thêm Sản phẩm
                        </button>
                    </div>
                </div>

                {/* Form Modal */}
                {showForm && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
                            <h3 className="text-lg font-bold mb-4">{editMode ? 'Cập nhật Sản phẩm' : 'Thêm Sản phẩm mới'}</h3>
                            <form onSubmit={handleSubmit}>
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Tên sản phẩm</label>
                                        <input
                                            type="text"
                                            required
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.productName}
                                            onChange={(e) => setFormData({...formData, productName: e.target.value})}
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Thương hiệu</label>
                                        <select
                                            required
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.brandId}
                                            onChange={(e) => setFormData({...formData, brandId: e.target.value})}
                                        >
                                            <option value="">Chọn thương hiệu ({brands.length} available)</option>
                                            {brands.map(brand => (
                                                <option key={brand.brandId} value={brand.brandId}>{brand.brandName}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Giá (VND)</label>
                                        <input
                                            type="number"
                                            required
                                            step="0.01"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.productPrice}
                                            onChange={(e) => setFormData({...formData, productPrice: e.target.value})}
                                        />
                                    </div>
                                    <div className="mb-4">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Số lượng</label>
                                        <input
                                            type="number"
                                            required
                                            min="0"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.quantityStock}
                                            onChange={(e) => setFormData({...formData, quantityStock: e.target.value})}
                                        />
                                    </div>
                                    <div className="mb-4 col-span-2">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Loại sản phẩm</label>
                                        <select
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.productCategory}
                                            onChange={(e) => setFormData({...formData, productCategory: e.target.value})}
                                        >
                                            <option value="">-- Chọn loại sản phẩm --</option>
                                            <option value="MAKEUP">Trang điểm</option>
                                            <option value="SKINCARE">Dưỡng da</option>
                                            <option value="HAIRCARE">Chăm sóc tóc</option>
                                        </select>
                                    </div>
                                    <div className="mb-4 col-span-2">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Trạng thái</label>
                                        <select
                                            required
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            value={formData.productStatus}
                                            onChange={(e) => setFormData({...formData, productStatus: e.target.value})}
                                        >
                                            <option value="NOT_SOLD">Chưa bán</option>
                                            <option value="SELLING">Đang bán</option>
                                            <option value="OUT_OF_STOCK">Hết hàng</option>
                                            <option value="DISCONTINUED">Ngừng kinh doanh</option>
                                        </select>
                                    </div>
                                    <div className="mb-4 col-span-2">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Ảnh đại diện (Main)</label>
                                        <input
                                            type="file"
                                            accept="image/*"
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            onChange={handleImageChange}
                                        />
                                        {imagePreview && (
                                            <div className="mt-3">
                                                <img src={imagePreview} alt="Preview" className="w-32 h-32 object-cover rounded-lg border" />
                                            </div>
                                        )}
                                    </div>

                                    <div className="mb-4 col-span-2">
                                        <label className="block text-sm font-medium text-gray-700 mb-1">Ảnh chi tiết (Chọn nhiều)</label>
                                        <input
                                            type="file"
                                            accept="image/*"
                                            multiple
                                            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                            onChange={handleDetailImagesChange}
                                        />
                                        
                                        {/* Preview new detail images */}
                                        {detailImagesPreview.length > 0 && (
                                            <div className="mt-3 grid grid-cols-4 gap-2">
                                                {detailImagesPreview.map((preview, index) => (
                                                    <div key={index} className="relative group">
                                                        <img src={preview} alt={`Detail Preview ${index}`} className="w-full h-24 object-cover rounded-lg border" />
                                                        <button
                                                            type="button"
                                                            onClick={() => removeDetailImage(index)}
                                                            className="absolute top-1 right-1 bg-red-500 text-white rounded-full p-1 w-6 h-6 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
                                                        >
                                                            ×
                                                        </button>
                                                    </div>
                                                ))}
                                            </div>
                                        )}

                                        {/* Show existing images in Edit mode */}
                                        {editMode && currentImages.length > 0 && (
                                            <div className="mt-4">
                                                <p className="text-sm font-medium text-gray-700 mb-2">Ảnh hiện tại trên hệ thống:</p>
                                                <div className="grid grid-cols-4 gap-2">
                                                    {currentImages.map((img) => (
                                                        <div key={img.imageId} className="relative group">
                                                            <img 
                                                                src={`${API_BASE_URL}${img.imageURL}`} 
                                                                alt={img.imageName} 
                                                                className="w-full h-24 object-cover rounded-lg border" 
                                                            />
                                                            <button
                                                                type="button"
                                                                onClick={() => handleDeleteImage(img.imageId)}
                                                                className="absolute top-1 right-1 bg-red-600 text-white rounded-full p-1 w-6 h-6 flex items-center justify-center text-xs shadow-sm hover:bg-red-700"
                                                                title="Xóa ảnh này"
                                                            >
                                                                ×
                                                            </button>
                                                        </div>
                                                    ))}
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </div>
                                <div className="mb-6">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
                                    <textarea
                                        className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:border-blue-500"
                                        rows="4"
                                        value={formData.productDescription}
                                        onChange={(e) => setFormData({...formData, productDescription: e.target.value})}
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
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 font-semibold text-gray-600">ID</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Ảnh</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Mã</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Tên sản phẩm</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Thương hiệu</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Loại</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Giá</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Số lượng</th>
                                <th className="px-6 py-4 font-semibold text-gray-600">Trạng thái</th>
                                <th className="px-6 py-4 font-semibold text-gray-600 whitespace-nowrap">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {products.map((item) => (
                                <tr key={item.productId} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 text-gray-800">#{item.productId}</td>
                                    <td className="px-6 py-4">
                                        <div className="w-16 h-16 bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center">
                                            {item.images && item.images.length > 0 ? (
                                                <img 
                                                    src={`${API_BASE_URL}${item.images[0].imageURL}`} 
                                                    alt={item.productName} 
                                                    className="w-full h-full object-cover"
                                                    onError={(e) => { 
                                                        e.target.style.display = 'none';
                                                        e.target.parentElement.innerHTML = '<div class="text-xs text-gray-400">No Image</div>';
                                                    }}
                                                />
                                            ) : (
                                                <div className="text-xs text-gray-400">No Image</div>
                                            )}
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 text-gray-600">{item.productCode}</td>
                                    <td className="px-6 py-4 font-medium text-gray-800">{item.productName}</td>
                                    <td className="px-6 py-4 text-gray-600">{item.brandName || 'N/A'}</td>
                                    <td className="px-6 py-4 text-gray-600">
                                        {item.productCategory === 'MAKEUP' ? 'Trang điểm' :
                                         item.productCategory === 'SKINCARE' ? 'Dưỡng da' :
                                         item.productCategory === 'HAIRCARE' ? 'Chăm sóc tóc' : '-'}
                                    </td>
                                    <td className="px-6 py-4 font-medium text-gray-800">{parseFloat(item.productPrice).toLocaleString('vi-VN')} ₫</td>
                                    <td className="px-6 py-4 text-gray-600">{item.quantityStock}</td>
                                    <td className="px-6 py-4">
                                        <span className={`px-2 py-1 rounded-full text-xs ${
                                            item.productStatus === 'SELLING' ? 'bg-green-100 text-green-700' :
                                            item.productStatus === 'OUT_OF_STOCK' ? 'bg-yellow-100 text-yellow-700' :
                                            'bg-red-100 text-red-700'
                                        }`}>
                                            {item.productStatus === 'SELLING' ? 'Đang bán' :
                                             item.productStatus === 'OUT_OF_STOCK' ? 'Hết hàng' :
                                             'Ngừng kinh doanh'}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex items-center gap-2">
                                            <button 
                                                onClick={() => handleEdit(item)}
                                                className="bg-pink-600 text-white px-3 py-1 rounded hover:bg-pink-700 transition-colors"
                                            >
                                                Sửa
                                            </button>
                                            <button 
                                                onClick={() => handleDelete(item.productId)}
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
                
                <div className="mt-4 flex items-center justify-between">
                    <p className="text-sm text-gray-700">
                        Hiển thị <span className="font-medium">{products.length}</span> kết quả
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

export default Products;
