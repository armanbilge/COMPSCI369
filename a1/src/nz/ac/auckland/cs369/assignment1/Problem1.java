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

import java.io.File;
import java.io.IOException;

/**
 * Code for problem 1.
 * @author Arman Bilge
 */
public final class Problem1 {

    private Problem1() {}

    /**
     * Returns the SVD approximation for a matrix.
     * @param SVD the singular value decomposition
     * @param rho the number of singular values to use
     * @return approximated matrix
     * @throws IllegalArgumentException when rho greater than rank of SVD
     */
    private static Matrix computeAHat(final SingularValueDecomposition SVD, final int rho) {
        if (rho > SVD.rank())
            throw new IllegalArgumentException("Rho " + rho + " greater than rank of SVD " + SVD.rank() + ".");
        final Matrix U = SVD.getU();
        final double[] s = SVD.getSingularValues();
        final Matrix V = SVD.getV();
        final Matrix AHat = new Matrix(U.getRowDimension(), U.getColumnDimension());
        for (int i = 0; i < rho; ++i) {
            final Matrix u_i = U.getMatrix(0, U.getRowDimension() - 1, i, i);
            final Matrix v_i_t = V.getMatrix(0, V.getRowDimension() - 1, i, i).transpose();
            AHat.plusEquals(u_i.timesEquals(s[i]).times(v_i_t));
        }
        return AHat;
    }

    /**
     * Computes the compression achieved by the SVD decomposition of a matrix.
     * @param A the matrix
     * @param rho the number of singular values used
     * @return the compression
     */
    private static double computeCompression(final Matrix A, final int rho) {
        final int rows = A.getRowDimension();
        final int cols = A.getColumnDimension();
        final double k_rho = rho * (1 + rows + cols);
        return 1 - k_rho / (rows * cols);
    }

    /**
     * Creates all necessary files for problem 1.
     * @param args path to the input image
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {

        final byte[][] image = PGMIO.read(new File(args[0]));

        final Matrix A = new Matrix(ImageMatrixUtils.byteToDouble(image));
        final SingularValueDecomposition SVD = A.svd();

        final File svdDir = new File("SVD");
        svdDir.mkdir();

        final Matrix U = SVD.getU();
        final byte[][] imageU = ImageMatrixUtils.doubleToByte(U.getArray(),
                new ImageMatrixUtils.LinearMap(ImageMatrixUtils.min(U.getArray()), ImageMatrixUtils.max(U.getArray())));
        PGMIO.write(imageU, new File(svdDir, "U.pgm"));

        final Matrix S = SVD.getS();
        final byte[][] imageS = ImageMatrixUtils.doubleToByte(S.getArray(),
                new ImageMatrixUtils.LinearMap(ImageMatrixUtils.min(S.getArray()), ImageMatrixUtils.max(S.getArray())));
        PGMIO.write(imageS, new File(svdDir, "S.pgm"));

        final Matrix V = SVD.getV();
        final byte[][] imageV = ImageMatrixUtils.doubleToByte(V.getArray(),
                new ImageMatrixUtils.LinearMap(ImageMatrixUtils.min(V.getArray()), ImageMatrixUtils.max(V.getArray())));
        PGMIO.write(imageV, new File(svdDir, "V.pgm"));

        final File aHatDir = new File("AHat");
        aHatDir.mkdir();

        final int[] rhos = {1, 2, 3, 4, 5, 10, 20, 30, 40, 80};
        final Table table = new Table(4, rhos.length + 1);
        table.setHeader(0, "$\\rho$");
        table.setContent(1, 0, "Max error");
        table.setContent(2, 0, "Mean error");
        table.setContent(3, 0, "% Compression");

        int c = 1;
        for (int rho : rhos) {
            final Matrix AHat = computeAHat(SVD, rho);

            final byte[][] imageAHat = ImageMatrixUtils.doubleToByte(AHat.getArray(), ImageMatrixUtils.TRUNCATING_MAP);
            PGMIO.write(imageAHat, new File(aHatDir, rho + ".pgm"));

            table.setHeader(c, Integer.toString(rho));
            table.setContent(1, c, Double.toString(ImageMatrixUtils.computeMaxError(A, AHat)));
            table.setContent(2, c, Double.toString(ImageMatrixUtils.computeMaxError(A, AHat)));
            table.setContent(3, c, Double.toString(computeCompression(A, rho) * 100));
            ++c;
        }

        // TODO Complete and write out table

    }

}
