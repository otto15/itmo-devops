import { describe, it, expect } from 'vitest'
import { formatCarTitle } from '../utils'

describe('formatCarTitle', () => {
    it('formats car title correctly', () => {
        const car = { brand: 'Toyota', model: 'Corolla', year: 2020 }
        expect(formatCarTitle(car)).toBe('Toyota Corolla (2020)')
    })

    it('works with different car data', () => {
        const car = { brand: 'BMW', model: 'X5', year: 2023 }
        expect(formatCarTitle(car)).toBe('BMW X5 (2023)')
    })
})
