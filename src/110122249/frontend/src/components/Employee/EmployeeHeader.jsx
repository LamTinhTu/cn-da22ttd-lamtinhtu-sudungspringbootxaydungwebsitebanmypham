import React from 'react';

const EmployeeHeader = ({ title }) => {
    return (
        <header className="bg-white shadow-sm h-20 flex items-center justify-between px-8 sticky top-0 z-10">
            <h2 className="text-2xl font-bold text-gray-800">{title}</h2>
            <div className="flex items-center gap-4">
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
                        NV
                    </div>
                    <div>
                        <p className="text-sm font-semibold text-gray-700">Nhân viên</p>
                        <p className="text-xs text-gray-500">Staff</p>
                    </div>
                </div>
            </div>
        </header>
    );
};

export default EmployeeHeader;
