import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from '../App'

vi.mock('../api/client', () => ({
  carsApi: {
    listCars: vi.fn(() => Promise.resolve({ data: [] })),
    createCar: vi.fn(() => Promise.resolve({ data: {} })),
    updateCar: vi.fn(() => Promise.resolve({ data: {} })),
    deleteCar: vi.fn(() => Promise.resolve()),
  }
}))

import { carsApi } from '../api/client'

describe('App', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    globalThis.alert = vi.fn()
    globalThis.confirm = vi.fn()
  })

  it('renders app title', () => {
    render(<App />)
    expect(screen.getByText(/Garage Management/i)).toBeInTheDocument()
  })

  it('shows loading state while fetching cars', () => {
    render(<App />)
    expect(screen.getByText(/Loading cars/i)).toBeInTheDocument()
  })

  it('displays empty state when no cars are in garage', async () => {
    render(<App />)

    await waitFor(() => {
      expect(screen.getByText(/No cars in the garage yet/i)).toBeInTheDocument()
    })
  })

  it('loads and displays cars from API', async () => {
    const mockCars = [
      { id: 1, brand: 'Toyota', model: 'Camry', year: 2022, color: 'Red', licensePlate: 'ABC123' },
      { id: 2, brand: 'Honda', model: 'Civic', year: 2021, color: 'Blue', licensePlate: 'XYZ789' }
    ]

    carsApi.listCars.mockResolvedValueOnce({ data: mockCars })

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument()
      expect(screen.getByText('Honda Civic')).toBeInTheDocument()
      expect(screen.getByText('Red')).toBeInTheDocument()
      expect(screen.getByText('ABC123')).toBeInTheDocument()
    })
  })

  it('adds a new car successfully', async () => {
    const user = userEvent.setup()
    carsApi.listCars.mockResolvedValue({ data: [] })
    carsApi.createCar.mockResolvedValueOnce({ data: { id: 3 } })

    render(<App />)

    await waitFor(() => {
      expect(screen.queryByText(/Loading cars/i)).not.toBeInTheDocument()
    })

    const currentYear = new Date().getFullYear()
    await user.type(screen.getByPlaceholderText(/Chevrolet/i), 'BMW')
    await user.type(screen.getByPlaceholderText(/Impala/i), 'X5')
    await user.type(screen.getByPlaceholderText(/Black/i), 'Black')
    await user.type(screen.getByPlaceholderText(/AB123CD/i), 'BMW789')

    const submitButton = screen.getByRole('button', { name: /Add Car/i })
    await user.click(submitButton)

    expect(carsApi.createCar).toHaveBeenCalledWith({
      brand: 'BMW',
      model: 'X5',
      year: currentYear,
      color: 'Black',
      licensePlate: 'BMW789'
    })
  })

  it('populates form when edit button is clicked', async () => {
    const user = userEvent.setup()
    const mockCars = [
      { id: 1, brand: 'Toyota', model: 'Camry', year: 2022, color: 'Red', licensePlate: 'ABC123' }
    ]
    carsApi.listCars.mockResolvedValue({ data: mockCars })

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: /Edit/i }))

    expect(screen.getByDisplayValue('Toyota')).toBeInTheDocument()
    expect(screen.getByDisplayValue('Camry')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Update Car/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /Cancel/i })).toBeInTheDocument()
  })

  it('updates car when update form is submitted', async () => {
    const user = userEvent.setup()
    const mockCars = [
      { id: 1, brand: 'Toyota', model: 'Camry', year: 2022, color: 'Red', licensePlate: 'ABC123' }
    ]
    carsApi.listCars.mockResolvedValue({ data: mockCars })
    carsApi.updateCar.mockResolvedValueOnce({ data: {} })

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: /Edit/i }))
    await user.click(screen.getByRole('button', { name: /Update Car/i }))

    expect(carsApi.updateCar).toHaveBeenCalledWith(1, expect.any(Object))
  })

  it('cancels editing when cancel button is clicked', async () => {
    const user = userEvent.setup()
    const mockCars = [
      { id: 1, brand: 'Toyota', model: 'Camry', year: 2022, color: 'Red', licensePlate: 'ABC123' }
    ]
    carsApi.listCars.mockResolvedValue({ data: mockCars })

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: /Edit/i }))
    expect(screen.getByRole('button', { name: /Cancel/i })).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: /Cancel/i }))
    expect(screen.getByRole('button', { name: /Add Car/i })).toBeInTheDocument()
  })

  it('deletes car when confirmed', async () => {
    const user = userEvent.setup()
    const mockCars = [
      { id: 1, brand: 'Toyota', model: 'Camry', year: 2022, color: 'Red', licensePlate: 'ABC123' }
    ]
    carsApi.listCars.mockResolvedValue({ data: mockCars })
    carsApi.deleteCar.mockResolvedValueOnce({})
    global.confirm.mockReturnValue(true)

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: /Delete/i }))

    expect(carsApi.deleteCar).toHaveBeenCalledWith(1)
  })

  it('does not delete car when confirmation is cancelled', async () => {
    const user = userEvent.setup()
    const mockCars = [
      { id: 1, brand: 'Toyota', model: 'Camry', year: 2022, color: 'Red', licensePlate: 'ABC123' }
    ]
    carsApi.listCars.mockResolvedValue({ data: mockCars })
    global.confirm.mockReturnValue(false)

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('Toyota Camry')).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: /Delete/i }))

    expect(carsApi.deleteCar).not.toHaveBeenCalled()
  })

})