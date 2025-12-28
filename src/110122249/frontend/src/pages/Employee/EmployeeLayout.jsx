import React, { useEffect } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import Sidebar from '../../components/Employee/Sidebar';
import { getUser } from '../../utils/jwt-helper';
import PageTitleUpdater from '../../components/common/PageTitleUpdater';

const EmployeeLayout = () => {
    const navigate = useNavigate();
    const user = getUser();

    useEffect(() => {
        if (!user || user.roleName !== 'Staff') {
            navigate('/');
        }
    }, [user, navigate]);

    if (!user || user.roleName !== 'Staff') {
        return null;
    }

    return (
        <div className="flex h-screen bg-gray-100 font-sans">
            <PageTitleUpdater />
            <Sidebar />
            <div className="flex-1 flex flex-col overflow-hidden">
                <main className="flex-1 overflow-x-hidden overflow-y-auto bg-gray-100">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default EmployeeLayout;
