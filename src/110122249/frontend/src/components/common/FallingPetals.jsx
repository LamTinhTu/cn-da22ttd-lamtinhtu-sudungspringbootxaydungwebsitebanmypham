import React, { useEffect, useState } from 'react';
import './FallingPetals.css';

const FallingPetals = () => {
    const [petals, setPetals] = useState([]);
    const [visible, setVisible] = useState(true);

    useEffect(() => {
        const petalCount = 40;
        const newPetals = [];

        for (let i = 0; i < petalCount; i++) {
            newPetals.push({
                id: i,
                left: Math.random() * 100 + 'vw',
                animationDuration: Math.random() * 5 + 5 + 's', // 5-10 seconds
                animationDelay: Math.random() * 10 - 5 + 's', // -5 to +5 seconds: starts mid-fall or delayed
                width: Math.random() * 10 + 10 + 'px', // 10-20px
                height: Math.random() * 10 + 10 + 'px', // 10-20px
            });
        }
        setPetals(newPetals);

        const timer = setTimeout(() => {
            setVisible(false);
        }, 5000);

        return () => clearTimeout(timer);
    }, []);

    if (!visible && petals.length === 0) return null; // Logic adjustment: keep component until faded if needed, or just let CSS hide it

    return (
        <div className={`falling-petals-container ${visible ? '' : 'fade-out'}`}>
            {petals.map((petal) => (
                <div
                    key={petal.id}
                    className="petal"
                    style={{
                        left: petal.left,
                        animationDuration: petal.animationDuration,
                        animationDelay: petal.animationDelay,
                        width: petal.width,
                        height: petal.height,
                    }}
                ></div>
            ))}
        </div>
    );
};

export default FallingPetals;
