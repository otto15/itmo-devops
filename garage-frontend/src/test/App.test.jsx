import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor, fireEvent } from '@testing-library/react'
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
    global.alert = vi.fn()
    global.confirm = vi.fn()
  })

  it('renders app title', () => {
    render(<App />)
    expect(screen.getByText(/Garage Management/i)).toBeInTheDocument()
  })

  // tests temporarily commented out to test Quality Gate coverage threshold
  // it('shows loading state while fetching cars'...
  // it('displays empty state when no cars are in garage'...
  // it('loads and displays cars from API'...
  // it('adds a new car successfully'...

})