package skywaysolutions.app.utils;

/**
 * Provides a class to do decimal arithmetic in.
 *
 * @author Alfred Manville
 */
public final class Decimal {
    private final long _value;
    private final int _decimals;

    /**
     * Creates a new decimal of the value 0.
     */
    public Decimal() {this(0,0);}

    /**
     * Creates a new decimal with the value to 2 decimal places.
     *
     * @param value The value of the decimal.
     */
    public Decimal(double value) {this(value, 2);}

    /**
     * Creates a new decimal with the value to the specified amount of decimal places.
     * @param value The value of the decimal.
     * @param decimals The number of decimal places to use.
     */
    public Decimal(double value, int decimals) {
        _value = (long) (value*(Math.pow(10, decimals)));
        _decimals = decimals;
    }
    private Decimal(long value, int decimals, Object dummy) {
        _value = value;
        _decimals = decimals;
    }

    /**
     * Gets a decimal from a stored value and a number of decimal places.
     *
     * @param value The value to store.
     * @param decimals The number of decimal places.
     * @return The decimal representing the parameters.
     */
    public static Decimal fromStored(long value, int decimals) {
        return new Decimal(value, decimals, null);
    }

    /**
     * Gets the value of the decimal as a double.
     *
     * @return The value as a double.
     */
    public double getValue() {
        return ((double) _value)/(Math.pow(10, _decimals));
    }

    /**
     * Gets the stored value in the decimal.
     *
     * @return The stored value.
     */
    public long getStoredValue() {
        return _value;
    }

    /**
     * Gets the number of decimal places.
     *
     * @return The number of decimal places stored.
     */
    public int getDecimalPlaces() {
        return _decimals;
    }

    /**
     * Adds a decimal to this decimal returning the result.
     *
     * @param dec The decimal to add.
     * @return The result.
     */
    public Decimal add(Decimal dec) {
        //Gets stored values at the largest amount of decimal places
        int targetDecimals = Math.max(dec.getDecimalPlaces(), _decimals);
        long current = _value * ((targetDecimals == _decimals) ? 1 : (long) Math.pow(10,(targetDecimals - _decimals)));
        long passed = dec.getStoredValue() * ((targetDecimals == dec.getDecimalPlaces()) ? 1 : (long) Math.pow(10,(targetDecimals - dec.getDecimalPlaces())));
        return Decimal.fromStored(current + passed, targetDecimals);
    }

    /**
     * Subtracts a decimal from this decimal returning the result.
     *
     * @param dec The decimal to subtract.
     * @return The result.
     */
    public Decimal sub(Decimal dec) {
        //Gets stored values at the largest amount of decimal places
        int targetDecimals = Math.max(dec.getDecimalPlaces(), _decimals);
        long current = _value * ((targetDecimals == _decimals) ? 1 : (long) Math.pow(10,(targetDecimals - _decimals)));
        long passed = dec.getStoredValue() * ((targetDecimals == dec.getDecimalPlaces()) ? 1 : (long) Math.pow(10,(targetDecimals - dec.getDecimalPlaces())));
        return Decimal.fromStored(current - passed, targetDecimals);
    }

    /**
     * Multiplies a decimal against this decimal returning the result.
     *
     * @param dec The decimal to multiply by.
     * @return The result.
     */
    public Decimal mul(Decimal dec) {
        return Decimal.fromStored(_value * dec.getStoredValue(), _decimals + dec.getDecimalPlaces());
    }

    /**
     * Divides a decimal against this decimal returning the result.
     *
     * @param dec The decimal to divide by.
     * @return The result.
     */
    public Decimal div(Decimal dec) {
        //Got to normalize the decimals first (No extra decimal places)
        int currentDecActual = _decimals;
        int passedDecActual = dec.getDecimalPlaces();
        long current = _value;
        long passed = dec.getStoredValue();
        if (passed != 0) {
            while (passed % 10 == 0) {
                passedDecActual--;
                passed /= 10;
            }
        }
        //Make sure current is not normalized more than the normalization of passed
        if (current != 0) {
            while (current % 10 == 0 && currentDecActual - passedDecActual > 0) {
                currentDecActual--;
                current /= 10;
            }
        }
        return Decimal.fromStored(current / passed, currentDecActual - passedDecActual);
    }

    /**
     * Modulo a decimal against this decimal returning the result.
     *
     * @param dec The decimal to modulo by.
     * @return The result.
     */
    public Decimal mod(Decimal dec) {
        //Got to normalize the decimals first (No extra decimal places)
        int currentDecActual = _decimals;
        int passedDecActual = dec.getDecimalPlaces();
        long current = _value;
        long passed = dec.getStoredValue();
        if (passed != 0) {
            while (passed % 10 == 0) {
                passedDecActual--;
                passed /= 10;
            }
        }
        //Make sure current is not normalized more than the normalization of passed
        if (current != 0) {
            while (current % 10 == 0 && currentDecActual - passedDecActual > 0) {
                currentDecActual--;
                current /= 10;
            }
        }
        return Decimal.fromStored(current % passed, currentDecActual - passedDecActual);
    }

    /**
     * Gets if this decimal is less than the passed decimal.
     *
     * @param dec The decimal to check against.
     * @return If this decimal is less than the passed decimal.
     */
    public boolean lessThan(Decimal dec) {
        //Gets stored values at the largest amount of decimal places
        int targetDecimals = Math.max(dec.getDecimalPlaces(), _decimals);
        long current = _value * ((targetDecimals == _decimals) ? 1 : (long) Math.pow(10,(targetDecimals - _decimals)));
        long passed = dec.getStoredValue() * ((targetDecimals == dec.getDecimalPlaces()) ? 1 : (long) Math.pow(10,(targetDecimals - dec.getDecimalPlaces())));
        return current < passed;
    }

    /**
     * Gets if this decimal is less than or equal to the passed decimal.
     *
     * @param dec The decimal to check against.
     * @return If this decimal is less than or equal to the passed decimal.
     */
    public boolean lessThanOrEqualTo(Decimal dec) {
        //Gets stored values at the largest amount of decimal places
        int targetDecimals = Math.max(dec.getDecimalPlaces(), _decimals);
        long current = _value * ((targetDecimals == _decimals) ? 1 : (long) Math.pow(10,(targetDecimals - _decimals)));
        long passed = dec.getStoredValue() * ((targetDecimals == dec.getDecimalPlaces()) ? 1 : (long) Math.pow(10, (targetDecimals - dec.getDecimalPlaces())));
        return current <= passed;
    }

    /**
     * Gets if this decimal is greater than the passed decimal.
     *
     * @param dec The decimal to check against.
     * @return If this decimal is greater than the passed decimal.
     */
    public boolean greaterThan(Decimal dec) {
        //Gets stored values at the largest amount of decimal places
        int targetDecimals = Math.max(dec.getDecimalPlaces(), _decimals);
        long current = _value * ((targetDecimals == _decimals) ? 1 : (long) Math.pow(10,(targetDecimals - _decimals)));
        long passed = dec.getStoredValue() * ((targetDecimals == dec.getDecimalPlaces()) ? 1 : (long) Math.pow(10,(targetDecimals - dec.getDecimalPlaces())));
        return current > passed;
    }

    /**
     * Gets if this decimal is greater than or equal to the passed decimal.
     *
     * @param dec The decimal to check against.
     * @return If this decimal is greater than or equal to the passed decimal.
     */
    public boolean greaterThanOrEqualTo(Decimal dec) {
        //Gets stored values at the largest amount of decimal places
        int targetDecimals = Math.max(dec.getDecimalPlaces(), _decimals);
        long current = _value * ((targetDecimals == _decimals) ? 1 : (long) Math.pow(10,(targetDecimals - _decimals)));
        long passed = dec.getStoredValue() * ((targetDecimals == dec.getDecimalPlaces()) ? 1 : (long) Math.pow(10,(targetDecimals - dec.getDecimalPlaces())));
        return current >= passed;
    }

    /**
     * Gets a string representation of this decimal.
     *
     * @return A string representation of this decimal.
     */
    @Override
    public String toString() {
        if (_decimals < 0) return String.valueOf(_value*Math.pow(10,(-1*_decimals)));
        if (_value == 0) {
            String toret = "0";
            if (_decimals > 0) {
                toret += ".";
                for (int i = 0; i < _decimals; i++) toret += "0";
            }
            return toret;
        }
        String sValue = String.valueOf(_value);
        return ((sValue.length() - _decimals == 0) ? "0" : sValue.substring(0, sValue.length() - _decimals)) + ((_decimals > 0) ? "." + sValue.substring(sValue.length() - _decimals) : "");
    }

    /**
     * Checks if this object is equal to another object.
     *
     * @param obj The object to compare.
     * @return If the objects are equivalent.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Number num) return new Decimal(num.doubleValue(), _decimals).getStoredValue() == _value;
        if (!(obj instanceof Decimal)) return false;
        return ((Decimal) obj)._decimals == _decimals && ((Decimal) obj)._value == _value;
    }
}
