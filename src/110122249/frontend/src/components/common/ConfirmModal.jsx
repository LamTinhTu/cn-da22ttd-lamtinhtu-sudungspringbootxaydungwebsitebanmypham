import React from 'react';

const ConfirmModal = ({ isOpen, onClose, onConfirm, title, message, confirmText = 'Xác nhận', cancelText = 'Hủy' }) => {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center overflow-x-hidden overflow-y-auto outline-none focus:outline-none bg-black bg-opacity-50 backdrop-blur-sm">
            <div className="relative w-full max-w-md mx-auto my-6">
                {/*content*/}
                <div className="relative flex flex-col w-full bg-white border-0 rounded-xl shadow-2xl outline-none focus:outline-none transform transition-all scale-100">
                    {/*header*/}
                    <div className="flex items-start justify-between p-5 border-b border-solid border-gray-200 rounded-t">
                        <h3 className="text-xl font-bold text-gray-800">
                            {title}
                        </h3>
                        <button
                            className="p-1 ml-auto bg-transparent border-0 text-gray-400 hover:text-gray-600 float-right text-3xl leading-none font-semibold outline-none focus:outline-none transition-colors"
                            onClick={onClose}
                        >
                            <span className="bg-transparent h-6 w-6 text-2xl block outline-none focus:outline-none">
                                ×
                            </span>
                        </button>
                    </div>
                    {/*body*/}
                    <div className="relative p-6 flex-auto">
                        <p className="my-4 text-gray-600 text-lg leading-relaxed">
                            {message}
                        </p>
                    </div>
                    {/*footer*/}
                    <div className="flex items-center justify-end p-6 border-t border-solid border-gray-200 rounded-b gap-3">
                        <button
                            className="text-gray-500 background-transparent font-bold uppercase px-6 py-3 text-sm outline-none focus:outline-none mr-1 mb-1 ease-linear transition-all duration-150 hover:bg-gray-100 rounded-lg"
                            type="button"
                            onClick={onClose}
                        >
                            {cancelText}
                        </button>
                        <button
                            className="bg-red-500 text-white active:bg-red-600 font-bold uppercase text-sm px-6 py-3 rounded-lg shadow hover:shadow-lg outline-none focus:outline-none mr-1 mb-1 ease-linear transition-all duration-150 hover:bg-red-600"
                            type="button"
                            onClick={() => {
                                onConfirm();
                                onClose();
                            }}
                        >
                            {confirmText}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;
