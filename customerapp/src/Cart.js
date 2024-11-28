import React, { useState } from 'react';
import { database } from './Firebase';
import { ref, push, set } from "firebase/database";

function Cart({ cart, onClose, onRemoveFromCart }) {
  const [email, setEmail] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const bookingsRef = ref(database, 'bookings');
      const newBookingRef = push(bookingsRef);

      await set(newBookingRef, {
        email: email,
        classes: cart
      });

      setEmail('');
      onClose();
      
    } catch (error) {
      console.error("Error submitting booking:", error);
      
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white rounded-lg p-4 w-full max-w-md">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-bold">Shopping Cart</h2>
          <button onClick={onClose} className="text-gray-600 hover:text-gray-800">
            Close
          </button>
        </div>

        {cart.length === 0 ? (
          <p>Your cart is empty.</p>
        ) : (
          <ul>
            {cart.map((item) => (
              <li key={item.id} className="flex justify-between items-center mb-2">
                <span>{item.dayOfWeek} - {item.time} - {item.classType}</span>
                <button onClick={() => onRemoveFromCart(item)} className="text-red-500 hover:text-red-700">
                  Remove
                </button>
              </li>
            ))}
          </ul>
        )}

        <form onSubmit={handleSubmit} className="mt-4">
          <input 
            type="email" 
            placeholder="Email" 
            className="border border-gray-400 rounded-md p-2 mb-2 w-full"
            value={email} 
            onChange={(e) => setEmail(e.target.value)} 
          />
          <button type="submit" className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-full">
            Submit
          </button>
        </form>
      </div>
    </div>
  );
}

export default Cart;
