import React, { useState } from 'react';
import { database } from './Firebase';
import { ref, get } from "firebase/database";

function Login() {
  const [email, setEmail] = useState('');
  const [bookings, setBookings] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMessage(null);

    try {
      const bookingsRef = ref(database, 'bookings');
      const snapshot = await get(bookingsRef);

      if (snapshot.exists()) {
        const bookingsData = snapshot.val();
        const userBookings = Object.values(bookingsData).filter(booking => booking.email === email);

        if (userBookings.length > 0) {
          setBookings(userBookings);
        } else {
          setErrorMessage('This Email dont have any oder.');
        }
      } else {
        setErrorMessage('didnt find any order.');
      }
    } catch (error) {
      console.error("Error fetching bookings:", error);
      setErrorMessage('Error fetching bookings.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-r from-purple-500 via-blue-500 to-pink-500 relative overflow-hidden">
      {/* Hologram effect */}
      <div className="absolute inset-0 bg-gradient-to-r from-purple-500 via-blue-500 to-pink-500 opacity-50 z-0"></div>
      <div className="absolute inset-0 bg-noise opacity-10 z-10"></div>

      <div className="p-6 max-w-xl mx-auto bg-white bg-opacity-80 rounded-lg shadow-lg relative z-20">
        <h1 className="text-3xl font-bold text-center mb-6 animate__animated animate__fadeIn">
          Login
        </h1>
        {errorMessage && <div className="text-red-500 mb-4 animate__animated animate__shakeX">{errorMessage}</div>}

        <form onSubmit={handleSubmit} className="animate__animated animate__fadeInUp">
          <input
            type="email"
            placeholder="Email"
            className="border-2 border-gray-300 p-3 mb-4 w-full rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <button
            type="submit"
            className={`w-full py-3 rounded-md text-white font-semibold ${isLoading ? 'bg-gray-500 cursor-not-allowed' : 'bg-blue-500 hover:bg-blue-700'} transition-all`}
            disabled={isLoading}
          >
            {isLoading ? 'Loading...' : 'Login'}
          </button>
        </form>

        {/* Add Back Button */}
        <button
          onClick={() => window.history.back()}
          className="w-full py-3 mt-4 rounded-md text-white font-semibold bg-gray-500 hover:bg-gray-700 transition-all"
        >
          Back
        </button>

        {bookings.length > 0 && (
          <div className="mt-6 animate__animated animate__fadeInUp">
            <h2 className="text-2xl font-semibold mb-3">Your Bookings</h2>
            <ul>
              {bookings.map((booking, index) => (
                <li key={index} className="bg-gray-100 rounded-md p-4 mb-3 shadow-lg hover:shadow-xl transition-all">
                  <h3 className="font-medium text-xl">Booking {index + 1}</h3>
                  <p className="text-gray-600">Email: {booking.email}</p>
                  <ul className="mt-2">
                    {booking.classes?.map((classItem, classIndex) => (
                      <li key={classIndex} className="text-gray-700">
                        {classItem.dayOfWeek} - {classItem.time} - {classItem.classType}
                      </li>
                    ))}
                  </ul>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}

export default Login;
