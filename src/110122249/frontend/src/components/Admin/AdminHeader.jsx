import React, { useEffect, useState } from 'react';

const AdminHeader = ({ title }) => {
    const [userAccount, setUserAccount] = useState('Admin');
    const [roleName, setRoleName] = useState('Administrator');

    useEffect(() => {
        try {
            const userInfo = localStorage.getItem('userInfo');
            if (userInfo) {
                const user = JSON.parse(userInfo);
                setUserAccount(user.userAccount || 'Admin');
                setRoleName(user.roleName || 'Administrator');
            }
        } catch (error) {
            console.error('Error loading user info:', error);
        }
    }, []);

    return (
        <header className="bg-white shadow-sm h-20 flex items-center justify-between px-8 sticky top-0 z-10">
            <h2 className="text-2xl font-bold text-gray-800">{title}</h2>
            <div className="flex items-center gap-4">
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-gray-600 font-bold">
                        {userAccount.substring(0, 2).toUpperCase()}
                    </div>
                    <div>
                        <p className="text-sm font-semibold text-gray-700">{userAccount}</p>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default AdminHeader;
