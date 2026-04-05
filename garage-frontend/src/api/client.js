import ApiClient from './generated/src/ApiClient';
import CarsApi from './generated/src/api/CarsApi';

const apiClient = new ApiClient();
apiClient.basePath = 'http://localhost:8080';
apiClient.defaultHeaders = {
  'Content-Type': 'application/json',
};

const carsApiInstance = new CarsApi(apiClient);

const promisify = (method, ...args) => {
  return new Promise((resolve, reject) => {
    const callback = (error, data, response) => {
      if (error) {
        reject(error);
      } else {
        resolve({ data, response });
      }
    };
    method(...args, callback);
  });
};

export const carsApi = {
  listCars: () => promisify(carsApiInstance.listCars.bind(carsApiInstance)),
  createCar: (carRequest) => promisify(carsApiInstance.createCar.bind(carsApiInstance), carRequest),
  getCar: (id) => promisify(carsApiInstance.getCar.bind(carsApiInstance), id),
  updateCar: (id, carRequest) => promisify(carsApiInstance.updateCar.bind(carsApiInstance), id, carRequest),
  deleteCar: (id) => promisify(carsApiInstance.deleteCar.bind(carsApiInstance), id),
};

// Mock-s
//
// let mockCars = [
//   { id: 1, brand: 'Toyota', model: 'Camry', year: 2022, color: 'Silver', licensePlate: 'ABC123' },
//   { id: 2, brand: 'Honda', model: 'Civic', year: 2021, color: 'Red', licensePlate: 'XYZ789' },
//   { id: 3, brand: 'BMW', model: 'X5', year: 2023, color: 'Black', licensePlate: 'BMW456' },
// ];

// const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// export const carsApi = {
//   listCars: async () => {
//     console.log('📡 MOCK GET /cars');
//     await delay(500);
//     return { data: [...mockCars] };
//   },
//   createCar: async (data) => {
//     console.log('📡 MOCK POST /cars', data);
//     await delay(500);
//     const newCar = { id: Date.now(), ...data };
//     mockCars.push(newCar);
//     return { data: newCar };
//   },
//   updateCar: async (id, data) => {
//     console.log('📡 MOCK PUT /cars/' + id, data);
//     await delay(500);
//     const index = mockCars.findIndex(c => c.id === id);
//     if (index !== -1) {
//       mockCars[index] = { ...mockCars[index], ...data };
//       return { data: mockCars[index] };
//     }
//     throw new Error('Car not found');
//   },
//   deleteCar: async (id) => {
//     console.log('📡 MOCK DELETE /cars/' + id);
//     await delay(500);
//     mockCars = mockCars.filter(c => c.id !== id);
//     return { data: null };
//   },
// };
