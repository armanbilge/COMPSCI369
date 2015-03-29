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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Code for problem 3.
 *
 * @author Arman Bilge
 */
public class Problem3 {

    /**
     * Reads a matrix from whitespace-delimited plain text file.
     * @param file the file to read from
     * @param rows the number of rows
     * @param cols the number of columns
     * @return the matrix represented in the file
     * @throws IOException
     */
    private static Matrix readMatrix(final File file, final int rows, final int cols) throws IOException {
        final Matrix data = new Matrix(rows, cols);
        try (final Scanner sc = new Scanner(file)) {
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j)
                    data.set(i, j, sc.nextInt());
            }
        }
        return data;
    }

    private static void writeMatrix(final Matrix A, final File file) throws FileNotFoundException {
        final PrintWriter pw = new PrintWriter(file);
        for (int i = 0; i < A.getRowDimension(); ++i) {
            for (int j = 0; j < A.getColumnDimension(); ++j) {
                pw.print(A.get(i, j));
                if (j + 1 < A.getColumnDimension())
                    pw.print(" ");
            }
            pw.println();
        }
    }

    public static void main(final String... args) throws IOException {

        final File dataFile = new File(args[0]);
        final Matrix A = readMatrix(dataFile, Integer.parseInt(args[1]), Integer.parseInt(args[2])).transpose();
        final PrincipalComponentsAnalysis PCA = new PrincipalComponentsAnalysis(A);
        final Matrix L = PCA.getProjectedData(2);
        writeMatrix(L, new File("locations.txt"));
    }

}
