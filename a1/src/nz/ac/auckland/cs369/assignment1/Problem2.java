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
import Jama.SingularValueDecomposition;
import nz.ac.auckland.cs369.assignment1.ImageMatrixUtils.LinearMap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Code for problem 2.
 *
 * @author Arman Bilge
 */
public final class Problem2 {

    private Problem2() {}

    /**
     * Computes the pseudoinverse of a matrix.
     * @param A the matrix to invert
     * @return the pseudoinverse of A
     */
    private static Matrix computePseudoInverse(final Matrix A) {
        final SingularValueDecomposition SVD = A.svd();
        final Matrix U = SVD.getU();
        final Matrix D = SVD.getS();
        final Matrix D_plus = computePseudoInverseDiagonal(D);
        final Matrix V = SVD.getV();
        return V.times(D_plus).times(U.transpose());
    }

    /**
     * Computes the pseudoinverse of a diagonal matrix.
     * @param D the diagonal matrix to invert
     * @return the pseudoinverse of D
     */
    private static Matrix computePseudoInverseDiagonal(final Matrix D) {
        final Matrix D_plus = D.copy();
        for (int i = 0; i < D_plus.getRowDimension(); ++i) {
            final double d_i = D.get(i, i);
            D_plus.set(i, i, d_i != 0 ? 1 / d_i : 0);
        }
        return D_plus;
    }

    /**
     * Computes the mean of the values in a matrix.
     * @param A the matrix
     * @return the mean of the matrix elements
     */
    private static double mean(final Matrix A) {
        return Arrays.stream(A.getArray()).flatMapToDouble(Arrays::stream).sum() /
                (A.getRowDimension() * A.getColumnDimension());
    }

    /**
     * Computes the standard deviation of the values in a matrix.
     * @param A the matrix
     * @return the standard deviation of the matrix elements
     */
    private static double stdev(final Matrix A) {
        final double Ex2 = Arrays.stream(A.getArray()).flatMapToDouble(Arrays::stream).map(x -> x*x).sum() /
                (A.getRowDimension() * A.getColumnDimension());
        final double Ex = mean(A);
        return Math.sqrt(Ex2 - Ex * Ex);
    }

    /**
     * Creates all necessary files for problem 2.
     * @param args path to the input image
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {

        final int[][] image = PGMIO.read(new File(args[0]));

        final Matrix A = new Matrix(ImageMatrixUtils.byteToDouble(image));
        final Matrix P_inv = computePseudoInverse(A);
        final Matrix I_hat = P_inv.times(A);

        final File inverseDir = new File("inverse");
        inverseDir.mkdir();

        final int[][] imagePinv = ImageMatrixUtils.doubleToByte(P_inv.getArray(),
                new LinearMap(ImageMatrixUtils.min(P_inv.getArray()), ImageMatrixUtils.max(P_inv.getArray())));
        PGMIO.write(imagePinv, new File(inverseDir, "Pinv.pgm"));

        final int[][] imageIhat = ImageMatrixUtils.doubleToByte(I_hat.getArray(),
                new LinearMap(ImageMatrixUtils.min(I_hat.getArray()), ImageMatrixUtils.max(I_hat.getArray())));
        PGMIO.write(imageIhat, new File(inverseDir, "Ihat.pgm"));

        final Matrix I = Matrix.identity(I_hat.getRowDimension(), I_hat.getColumnDimension());
        final Matrix E = I.minus(I_hat);

        final Variables var = new Variables();
        var.put("range", (ImageMatrixUtils.max(E.getArray()) - ImageMatrixUtils.min(E.getArray())));
        var.put("mean", mean(E));
        var.put("stdev", stdev(E));
        var.write(new File(inverseDir, "error.tex"));

        final Matrix B = A.times(P_inv);
        final int[][] imageB = ImageMatrixUtils.doubleToByte(B.getArray(),
                new LinearMap(ImageMatrixUtils.min(B.getArray()), ImageMatrixUtils.max(B.getArray())));
        PGMIO.write(imageB, new File(inverseDir, "B.pgm"));

    }

}
