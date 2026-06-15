import ApiClient from './generated/src/ApiClient';
import CarsApi from './generated/src/api/CarsApi';

const apiClient = new ApiClient();
apiClient.basePath = import.meta.env.VITE_API_URL || '';
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

