import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import App from '../App'

vi.mock('../api/client', () => ({
  carsApi: {
    listCars: vi.fn(() => Promise.resolve({ data: [] })),
    createCar: vi.fn(),
    updateCar: vi.fn(),
    deleteCar: vi.fn(),
  }
}))

describe('App', () => {
  it('renders app title', () => {
    render(<App />)
    expect(screen.getByText(/Garage Management/i)).toBeInTheDocument()
  })
})
