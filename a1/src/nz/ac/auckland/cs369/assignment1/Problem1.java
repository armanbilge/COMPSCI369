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
import nz.ac.auckland.cs369.assignment1.Table.Alignment;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Code for problem 1.
 *
 * @author Arman Bilge
 */
public final class Problem1 {

    private static final NumberFormat nf = DecimalFormat.getInstance();
    static {
        nf.setMaximumFractionDigits(2);
    }

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

        final int[][] image = PGMIO.read(new File(args[0]));

        final Matrix A = new Matrix(ImageMatrixUtils.byteToDouble(image));
        final SingularValueDecomposition SVD = A.svd();

        final File svdDir = new File("SVD");
        svdDir.mkdir();

        final Matrix U = SVD.getU();
        final int[][] imageU = ImageMatrixUtils.doubleToByte(U.getArray(),
                new ImageMatrixUtils.LinearMap(ImageMatrixUtils.min(U.getArray()), ImageMatrixUtils.max(U.getArray())));
        PGMIO.write(imageU, new File(svdDir, "U.pgm"));

        final Matrix D = SVD.getS();
        final int[][] imageD = ImageMatrixUtils.doubleToByte(D.getArray(),
                new ImageMatrixUtils.LinearMap(ImageMatrixUtils.min(D.getArray()), ImageMatrixUtils.max(D.getArray())));
        PGMIO.write(imageD, new File(svdDir, "D.pgm"));

        final Matrix V = SVD.getV();
        final int[][] imageV = ImageMatrixUtils.doubleToByte(V.getArray(),
                new ImageMatrixUtils.LinearMap(ImageMatrixUtils.min(V.getArray()), ImageMatrixUtils.max(V.getArray())));
        PGMIO.write(imageV, new File(svdDir, "V.pgm"));

        final File aHatDir = new File("Ahat");
        aHatDir.mkdir();

        final int[] rhos = {1, 2, 3, 4, 5, 10, 20, 30, 40, 90};
        final Table table = new Table(4, rhos.length + 1);
        table.setHeader(0, "$\\rho$");
        table.setAlignment(0, Alignment.LEFT);
        int r = 0;
        table.setContent(r++, 0, "$\\widehat{\\mathbf{A}}_\\rho$");
        table.setContent(r++, 0, "\\textbf{Max error}");
        table.setContent(r++, 0, "\\textbf{Mean error}");
        table.setContent(r++, 0, "\\textbf{\\% Compression}");

        int c = 1;
        for (int rho : rhos) {
            final Matrix A_hat = computeAHat(SVD, rho);

            final int[][] imageAHat = ImageMatrixUtils.doubleToByte(A_hat.getArray(), ImageMatrixUtils.TRUNCATING_MAP);
            final File imageFile = new File(aHatDir, rho + ".pgm");
            PGMIO.write(imageAHat, imageFile);

            table.setHeader(c, "$" + (rho > 5 ? "\\ldots" : "") + Integer.toString(rho) + "$");
            r = 0;
            table.setContent(r++, c, "\\includegraphics{" + imageFile.getPath() + "}");
            table.setContent(r++, c, "$" + nf.format(ImageMatrixUtils.computeMaxError(A, A_hat)) + "$");
            table.setContent(r++, c, "$" + nf.format(ImageMatrixUtils.computeMeanError(A, A_hat)) + "$");
            final double compression = computeCompression(A, rho);
            table.setContent(r++, c, compression > 0 ? "$" + nf.format(compression * 100) + "$" : "n/a");
            ++c;
        }

        table.write(new File(aHatDir, "compression.tex"));

    }

}
