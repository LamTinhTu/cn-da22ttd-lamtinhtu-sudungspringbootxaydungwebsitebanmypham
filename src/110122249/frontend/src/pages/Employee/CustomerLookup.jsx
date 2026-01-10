import React, { useState, useEffect } from 'react';
import EmployeeHeader from '../../components/Employee/EmployeeHeader';
import { getAllUsers } from '../../api/user';
import { toast } from 'react-toastify';

const CustomerLookup = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState('');
    const [pagination, setPagination] = useState({
        page: 0,
        size: 10,
        totalPages: 0,
        totalElements: 0
    });

    const fetchCustomers = async (page = 0, keyword = '') => {
        try {
            setLoading(true);
            const response = await getAllUsers({ 
                page, 
                size: 10, 
                keyword: keyword,
                roleName: 'Customer' 
            });
            if (response && response.data) {
                const data = response.data;
                setCustomers(data.items || []);
                setPagination({
                    page: data.page || 0,
                    size: data.size || 10,
                    totalPages: data.totalPages || 0,
                    totalElements: data.totalElements || 0
                });
            }
        } catch (err) {
            console.error('Error fetching customers:', err);
            if (err.response && err.response.status === 403) {
                toast.error('Bạn không có quyền truy cập danh sách khách hàng.');
            } else {
                toast.error('Không thể tải danh sách khách hàng.');
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            fetchCustomers(0, searchTerm);
        }, 500);

        return () => clearTimeout(delayDebounceFn);
    }, [searchTerm]);

    const handleSearch = (e) => {
        setSearchTerm(e.target.value);
    };

    const getGenderText = (gender) => {
        switch(gender) {
            case 'MALE': return 'Nam';
            case 'FEMALE': return 'Nữ';
            default: return 'Khác';
        }
    };

    // Removed client-side filtering, using server-side search results directly
    const filteredCustomers = customers;

    return (
        <div className="flex-1 bg-gray-50 min-h-screen font-sans">
            <EmployeeHeader title="Tra cứu khách hàng" />
            
            <div className="p-8">
                <div className="flex justify-between items-center mb-6">
                    <div className="relative w-96">
                        <input
                            type="text"
                            placeholder="Tìm kiếm khách hàng..."
                            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                            value={searchTerm}
                            onChange={handleSearch}
                        />
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </div>
                    </div>
                </div>

                <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                    {loading ? (
                        <div className="p-6 text-center text-gray-500">Đang tải dữ liệu...</div>
                    ) : filteredCustomers.length === 0 ? (
                        <div className="p-6 text-center text-gray-500">Không tìm thấy khách hàng nào</div>
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
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {filteredCustomers.map((customer) => (
                                        <tr key={customer.userId} className="hover:bg-gray-50">
                                            <td className="px-6 py-4 text-gray-800">{customer.userCode || 'N/A'}</td>
                                            <td className="px-6 py-4 font-medium text-gray-800">{customer.userName}</td>
                                            <td className="px-6 py-4 text-gray-600">{getGenderText(customer.userGender)}</td>
                                            <td className="px-6 py-4 text-gray-600">{customer.userPhone || 'N/A'}</td>
                                            <td className="px-6 py-4 text-gray-600">{customer.userAccount}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
                
                <div className="mt-4 flex items-center justify-between">
                    <p className="text-sm text-gray-700">
                        Hiển thị <span className="font-medium">{filteredCustomers.length}</span> kết quả
                    </p>
                </div>
            </div>
        </div>
    );
};

export default CustomerLookup;
