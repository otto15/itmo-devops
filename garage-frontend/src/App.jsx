import { useState, useEffect } from 'react';
import { carsApi } from './api/client';
import './App.css';

function App() {
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingCar, setEditingCar] = useState(null);
  const [formData, setFormData] = useState({
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    color: '',
    licensePlate: ''
  });

  const loadCars = async () => {
    try {
      setLoading(true);
      const response = await carsApi.listCars();
      setCars(response.data);
    } catch (error) {
      console.error('Error:', error);
      alert('Backend not running on port 8080\n\nMake sure to start the backend first');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCars();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCar) {
        await carsApi.updateCar(editingCar.id, formData);
        alert('Car updated successfully!');
      } else {
        await carsApi.createCar(formData);
        alert('Car added successfully!');
      }
      setFormData({ brand: '', model: '', year: 2024, color: '', licensePlate: '' });
      setEditingCar(null);
      loadCars();
    } catch (error) {
      console.error('Error:', error);
      alert('Error saving car. Check console for details.');
    }
  };

  const handleEdit = (car) => {
    setEditingCar(car);
    setFormData({
      brand: car.brand,
      model: car.model,
      year: car.year,
      color: car.color,
      licensePlate: car.licensePlate
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this car?')) return;
    try {
      await carsApi.deleteCar(id);
      alert('Car deleted successfully!');
      loadCars();
    } catch (error) {
      console.error('Error:', error);
      alert('Error deleting car');
    }
  };

  return (
    <div className="app">
      <header className="header">
        <h1>🏎️ Garage Management</h1>
      </header>

      <div className="container">
        {/* Add/edit form */}
        <div className="card form-card">
          <h2>{editingCar ? '✏️ Edit Car' : '➕ Add New Car'}</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>Brand *</label>
                <input
                  type="text"
                  value={formData.brand}
                  onChange={(e) => setFormData({...formData, brand: e.target.value})}
                  required
                  placeholder="Chevrolet"
                />
              </div>

              <div className="form-group">
                <label>Model *</label>
                <input
                  type="text"
                  value={formData.model}
                  onChange={(e) => setFormData({...formData, model: e.target.value})}
                  required
                  placeholder="Impala"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Year *</label>
                <input
                  type="number"
                  value={formData.year}
                  onChange={(e) => setFormData({...formData, year: parseInt(e.target.value)})}
                  required
                  min="1886"
                  max="2100"
                />
              </div>

              <div className="form-group">
                <label>Color *</label>
                <input
                  type="text"
                  value={formData.color}
                  onChange={(e) => setFormData({...formData, color: e.target.value})}
                  required
                  placeholder="Black"
                />
              </div>
            </div>

            <div className="form-group">
              <label>License Plate *</label>
              <input
                type="text"
                value={formData.licensePlate}
                onChange={(e) => setFormData({...formData, licensePlate: e.target.value.toUpperCase()})}
                required
                placeholder="AB123CD"
              />
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-primary">
                {editingCar ? 'Update Car' : 'Add Car'}
              </button>
              {editingCar && (
                <button type="button" onClick={() => {
                  setEditingCar(null);
                  setFormData({ brand: '', model: '', year: 2024, color: '', licensePlate: '' });
                }} className="btn-secondary">
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>

        {/* Cars list */}
        <div className="card list-card">
          <h2>📋 My Cars ({cars.length})</h2>
          
          {loading ? (
            <div className="loading">Loading cars...</div>
          ) : cars.length === 0 ? (
            <div className="empty-state">
              <p>🚗 No cars in the garage yet.</p>
              <p>Add your first car using the form above!</p>
            </div>
          ) : (
            <div className="cars-grid">
              {cars.map(car => (
                <div key={car.id} className="car-card">
                  <div className="car-header">
                    <h3>{car.brand} {car.model}</h3>
                    <span className="car-year">{car.year}</span>
                  </div>
                  <div className="car-details">
                    <p><strong>Color:</strong> {car.color}</p>
                    <p><strong>License Plate:</strong> {car.licensePlate}</p>
                  </div>
                  <div className="car-actions">
                    <button onClick={() => handleEdit(car)} className="btn-edit">
                      Edit
                    </button>
                    <button onClick={() => handleDelete(car.id)} className="btn-delete">
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;