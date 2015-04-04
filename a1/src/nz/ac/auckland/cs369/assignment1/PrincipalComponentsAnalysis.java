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
import java.util.Arrays;

/**
 * Represents the principal components analysis for a matrix.
 *
 * @author Arman Bilge
 */
public class PrincipalComponentsAnalysis {

    private final Matrix A;
    private final Matrix U;
    private final Matrix D;

    /**
     * Constructs the principal components analysis for the given matrix.
     * Assumes that rows of the matrix are independent samples and that columns represent variables.
     * @param A the matrix
     */
    public PrincipalComponentsAnalysis(final Matrix A) {

        this.A = A;
        final Matrix B = centerColumns(A);
        final Matrix S;

        final int rows = A.getRowDimension();
        final int cols = A.getColumnDimension();

        if (rows > cols) {
            final SingularValueDecomposition SVD = B.svd();
            U = SVD.getV();
            S = SVD.getS();
        } else {
            final SingularValueDecomposition SVD = B.transpose().svd();
            S = SVD.getS();
            U = SVD.getV().times(S);
        }

        D = S.arrayTimes(S).times(1.0 / (rows));
    }

    /**
     * Centers the rows of a matrix.
     * @param A the matrix
     * @return the centered matrix
     */
    private Matrix centerRows(final Matrix A) {
        final Matrix B = A.copy();
        final int rows = B.getRowDimension();
        final int cols = B.getColumnDimension();
        for (int i = 0; i < rows; ++i) {
            final Matrix row = B.getMatrix(i, i, 0, cols - 1);
            final double[][] mean = new double[1][cols];
            Arrays.fill(mean[0], Arrays.stream(row.getRowPackedCopy()).sum() / cols);
            row.minusEquals(new Matrix(mean));
            B.setMatrix(i, i, 0, cols - 1, row);
        }
        return B;
    }

    /**
     * Centers the columns of a matrix.
     * @param A the matrix
     * @return the centered matrix
     */
    private Matrix centerColumns(final Matrix A) {
        final Matrix B = A.copy();
        final int rows = B.getRowDimension();
        final int cols = B.getColumnDimension();
        for (int i = 0; i < cols; ++i) {
            final Matrix column = B.getMatrix(0, rows - 1, i, i);
            final double[][] mean = new double[rows][];
            Arrays.fill(mean, new double[]{Arrays.stream(column.getColumnPackedCopy()).sum() / rows});
            column.minusEquals(new Matrix(mean));
            B.setMatrix(0, rows - 1, i, i, column);
        }
        return B;
    }

    public double getPrincipalComponent(final int i) {
        return D.get(i, i);
    }

    public Matrix getPrincipalComponentVector(final int i) {
        return U.getMatrix(0, U.getRowDimension() - 1, i, i);
    }

    public Matrix getProjectionMatrix(final int k) {
        final Matrix U_k = U.getMatrix(0, U.getRowDimension() - 1, 0, k - 1);
        return U_k.times(U_k.transpose());
    }

    public Matrix getProjectedData(final int k) {
        final Matrix P_k = getProjectionMatrix(k);
        return P_k.times(A).transpose();
    }

    public static void main(final String... args) {

        final Matrix A = new Matrix(new double[][]{
                {-4, 3, -5, 18, 6, -5},
                {2, 6, -2, 10, 1, -1},
                {7, 11, 3, 6, 9, 3}
        });

        final PrincipalComponentsAnalysis PCA = new PrincipalComponentsAnalysis(A.transpose());
        final Matrix P = PCA.getProjectionMatrix(2);
    }

}
