import React, { useState, useEffect } from 'react';
import { database } from './Firebase';
import { ref, onValue } from "firebase/database";
import { Link } from 'react-router-dom';
import Cart from './Cart';
import { FaSignInAlt } from 'react-icons/fa';
function App() {
  const [yogaClasses, setYogaClasses] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedTime, setSelectedTime] = useState('');
  const [sortByPrice, setSortByPrice] = useState(false);
  const [cart, setCart] = useState([]);
  const [isCartOpen, setIsCartOpen] = useState(false);

  useEffect(() => {
    const yogaClassesRef = ref(database, 'yoga_classes');

    onValue(yogaClassesRef, (snapshot) => {
      const data = snapshot.val();
      const classesArray = Object.values(data);
      setYogaClasses(classesArray);
    });
  }, []);

  const convertTimeToNumber = (time) => {
    const [hour, minute] = time.split(':').map(Number);
    return hour + minute / 60;
  };

  const filteredYogaClasses = yogaClasses
    .filter((yogaClass) => {
      const search = searchTerm.toLowerCase();
      const timeMatch = selectedTime === '' || yogaClass.time === selectedTime;
      return (
        timeMatch && 
        (
          yogaClass.dayOfWeek.toLowerCase().includes(search) ||
          yogaClass.time.toLowerCase().includes(search) ||
          yogaClass.classType.toLowerCase().includes(search)
        )
      );
    })
    .sort((a, b) => {
      if (sortByPrice) {
        return b.price - a.price;
      } else {
        const timeA = convertTimeToNumber(a.time);
        const timeB = convertTimeToNumber(b.time);
        return timeA - timeB;
      }
    });

  const handleAddToCart = (yogaClass) => {
    setCart([...cart, yogaClass]);
  };

  const handleRemoveFromCart = (yogaClass) => {
    setCart(cart.filter(item => item.id !== yogaClass.id));
  };

  const handleOpenCart = () => {
    setIsCartOpen(true);
  };

  const handleCloseCart = () => {
    setIsCartOpen(false);
  };

  return (
    <div className="App p-4 bg-gradient-to-r from-blue-200 to-purple-200 min-h-screen">
      <h1 className="text-3xl font-bold mb-4 text-center text-gray-800">Yoga Classes</h1>

      <div className="flex flex-col md:flex-row mb-4">
  <input
    type="text"
    placeholder="Search..."
    className="border border-gray-400 rounded-md p-2 mr-2 mb-2 md:mb-0 w-full md:w-[35%] transition-transform duration-200 focus:scale-105"
    value={searchTerm}
    onChange={(e) => setSearchTerm(e.target.value)}
  />

  <select
    className="border border-gray-400 rounded-md p-2 mr-2 mb-2 md:mb-0 w-full md:w-1/4"
    value={selectedTime}
    onChange={(e) => setSelectedTime(e.target.value)}
  >
    <option value="">All Times</option>
    <option value="7:00">7:00 AM</option>
    <option value="8:00">8:00 AM</option>
    <option value="9:00">9:00 AM</option>
    <option value="10:00">10:00 AM</option>
    <option value="11:00">11:00 AM</option>
    <option value="12:00">12:00 PM</option>
    <option value="13:00">1:00 PM</option>
    <option value="14:00">2:00 PM</option>
    <option value="15:00">3:00 PM</option>
    <option value="16:00">4:00 PM</option>
    <option value="17:00">5:00 PM</option>
    <option value="18:00">6:00 PM</option>
    <option value="19:00">7:00 PM</option>
    <option value="20:00">8:00 PM</option>
  </select>

  <button
    className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-full md:w-auto transition-colors duration-300"
    onClick={() => setSortByPrice(!sortByPrice)}
  >
    {sortByPrice ? 'Price: High to Low' : 'Sort by Price'}
  </button>

  <div className="flex items-center mt-2 md:mt-0">
    <button
      className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded ml-2 w-full md:w-auto transition-colors duration-300"
      onClick={handleOpenCart}
    >
      View Cart ({cart.length})
    </button>
    <Link to="/login" className="ml-2">
      <FaSignInAlt className="text-2xl text-blue-500 hover:text-blue-700" />
    </Link>
  </div>
</div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
        {filteredYogaClasses.map((yogaClass) => (
          <div
            key={yogaClass.id}
            className="bg-white rounded-lg shadow-md p-4 transform hover:scale-105 active:bg-gray-100 transition-all duration-200"
          >
            <h2 className="text-lg font-medium mb-2">
              {yogaClass.dayOfWeek} - {yogaClass.time}
            </h2>
            <p className="text-gray-600 mb-2">{yogaClass.classType}</p>
            <p className="mb-2">{yogaClass.description}</p>
            <div className="flex flex-col md:flex-row justify-between items-center">
              <span className="text-lg font-medium text-green-500 mb-2 md:mb-0">
                ${yogaClass.price}
              </span>
              <div className="text-gray-500 text-sm flex flex-col md:flex-row">
                <span className="mb-1 md:mb-0">{yogaClass.duration} minutes</span>
                <span className="mx-2 hidden md:inline">|</span>
                <span>Capacity: {yogaClass.capacity}</span>
              </div>
            </div>
            <button onClick={() => handleAddToCart(yogaClass)} className="mt-2 bg-blue-500 hover:bg-blue-700 text-white font-bold py-1 px-2 rounded">
              Add to Cart
            </button>
          </div>
        ))}
      </div>

      {isCartOpen && (
        <Cart
          cart={cart}
          onClose={handleCloseCart}
          onRemoveFromCart={handleRemoveFromCart}
        />
      )}
    </div>
  );
}

export default App;