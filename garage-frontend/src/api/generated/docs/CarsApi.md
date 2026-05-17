# GarageManagementApi.CarsApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**createCar**](CarsApi.md#createCar) | **POST** /cars | Add a car to the garage
[**deleteCar**](CarsApi.md#deleteCar) | **DELETE** /cars/{id} | Remove a car from the garage
[**getCar**](CarsApi.md#getCar) | **GET** /cars/{id} | Get a car by ID
[**listCars**](CarsApi.md#listCars) | **GET** /cars | List all cars
[**updateCar**](CarsApi.md#updateCar) | **PUT** /cars/{id} | Update a car



## createCar

> CarResponse createCar(carRequest)

Add a car to the garage

### Example

```javascript
import GarageManagementApi from 'garage_management_api';

let apiInstance = new GarageManagementApi.CarsApi();
let carRequest = new GarageManagementApi.CarRequest(); // CarRequest | 
apiInstance.createCar(carRequest, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **carRequest** | [**CarRequest**](CarRequest.md)|  | 

### Return type

[**CarResponse**](CarResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


## deleteCar

> deleteCar(id)

Remove a car from the garage

### Example

```javascript
import GarageManagementApi from 'garage_management_api';

let apiInstance = new GarageManagementApi.CarsApi();
let id = 789; // Number | 
apiInstance.deleteCar(id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully.');
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


## getCar

> CarResponse getCar(id)

Get a car by ID

### Example

```javascript
import GarageManagementApi from 'garage_management_api';

let apiInstance = new GarageManagementApi.CarsApi();
let id = 789; // Number | 
apiInstance.getCar(id, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 

### Return type

[**CarResponse**](CarResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## listCars

> [CarResponse] listCars()

List all cars

### Example

```javascript
import GarageManagementApi from 'garage_management_api';

let apiInstance = new GarageManagementApi.CarsApi();
apiInstance.listCars((error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**[CarResponse]**](CarResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


## updateCar

> CarResponse updateCar(id, carRequest)

Update a car

### Example

```javascript
import GarageManagementApi from 'garage_management_api';

let apiInstance = new GarageManagementApi.CarsApi();
let id = 789; // Number | 
let carRequest = new GarageManagementApi.CarRequest(); // CarRequest | 
apiInstance.updateCar(id, carRequest, (error, data, response) => {
  if (error) {
    console.error(error);
  } else {
    console.log('API called successfully. Returned data: ' + data);
  }
});
```

### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **Number**|  | 
 **carRequest** | [**CarRequest**](CarRequest.md)|  | 

### Return type

[**CarResponse**](CarResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

