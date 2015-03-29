/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arman Bilge
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nz.ac.auckland.cs369.assignment1;

import Jama.Matrix;

import java.util.Arrays;
import java.util.function.DoubleToIntFunction;

/**
 * A collection of utility functions for manipulating matrices that represent images.
 *
 * @author Arman Bilge
 */
public final class ImageMatrixUtils {

    private ImageMatrixUtils() {}

    /**
     * Maps doubles to bytes by truncating values outside the byte range.
     */
    public static final DoubleToIntFunction TRUNCATING_MAP = (d) -> {
        final int i = (int) Math.round(d);
        if (i < 0)
            return 0;
        else if (i > 255)
            return 255;
        else
            return (byte) i;
    };

    /**
     * Represents a linear map from a given range to the byte range.
     */
    public static class LinearMap implements DoubleToIntFunction {
        final double min;
        final double max;
        public LinearMap(final double min, final double max) {
            this.min = min;
            this.max = max;
        }
        @Override
        public int applyAsInt(final double d) {
            return (int) Math.round(255 * (d - min) / (max - min));
        }
    }

    /**
     * Converts a two-dimensional byte array to a two-dimensional double array.
     * @param b a 2D byte array
     * @return a 2D double array
     */
    public static double[][] byteToDouble(int[][] b) {
        final double[][] d = new double[b.length][b[0].length];
        for (int i = 0; i < d.length; ++i) {
            for (int j = 0; j < d[0].length; ++j) {
                d[i][j] = b[i][j];
            }
        }
        return d;
    }

    /**
     * Converts a two-dimensional double array to a two-dimensional double array using the given mapping function.
     * @param d a 2D double array
     * @param f a mapping function
     * @return a 2D byte array
     */
    public static int[][] doubleToByte(double[][] d, DoubleToIntFunction f) {
        final int[][] b = new int[d.length][d[0].length];
        for (int i = 0; i < b.length; ++i) {
            for (int j = 0; j < b[0].length; ++j) {
                b[i][j] = f.applyAsInt(d[i][j]);
            }
        }
        return b;
    }

    /**
     * Finds the maximum value in a two-dimensional double array.
     * @param d 2D array of doubles
     * @return maximum value
     */
    public static double max(final double[][] d) {
        return Arrays.stream(d).flatMapToDouble(Arrays::stream).max().getAsDouble();
    }

    /**
     * Finds the minimum value in a two-dimensional double array.
     * @param d 2D array of doubles
     * @return minimum value
     */
    public static double min(final double[][] d) {
        return Arrays.stream(d).flatMapToDouble(Arrays::stream).min().getAsDouble();
    }

    /**
     * Computes the maximum error between a matrix and its approximation.
     * @param A the matrix
     * @param AHat the approximation
     * @return the maximum error
     */
    public static double computeMaxError(final Matrix A, final Matrix AHat) {
        return Arrays.stream(A.minus(AHat).getArray()).flatMapToDouble(Arrays::stream).map(Math::abs).max().getAsDouble();
    }

    /**
     * Computes the mean error between a matrix and its approximation.
     * @param A the matrix
     * @param AHat the approximation
     * @return the mean error
     */
    public static double computeMeanError(final Matrix A, final Matrix AHat) {
        return Arrays.stream(A.minus(AHat).getArray()).flatMapToDouble(Arrays::stream).map(Math::abs).sum()
                / (A.getColumnDimension() * A.getRowDimension());
    }

}
