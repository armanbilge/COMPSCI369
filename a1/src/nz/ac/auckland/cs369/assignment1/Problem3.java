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
import nz.ac.auckland.cs369.assignment1.Table.Alignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Code for problem 3.
 *
 * @author Arman Bilge
 */
public final class Problem3 {

    private static final NumberFormat nf = DecimalFormat.getInstance();
    static {
        nf.setMaximumFractionDigits(3);
    }

    private Problem3() {}

    /**
     * Reads a matrix from a whitespace-delimited plain text file.
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

    /**
     * Writes a matrix to a whitespace-delimited plain text file.
     * @param A the matrix to write
     * @param file the file to write to
     * @throws FileNotFoundException
     */
    private static void writeMatrix(final Matrix A, final File file) throws FileNotFoundException {
        try (final PrintWriter pw = new PrintWriter(file)) {
            for (int i = 0; i < A.getRowDimension(); ++i) {
                for (int j = 0; j < A.getColumnDimension(); ++j) {
                    pw.print(A.get(i, j));
                    if (j + 1 < A.getColumnDimension())
                        pw.print(" ");
                }
                pw.println();
            }
        }
    }

    /**
     * Creates all necessary files for problem 3.
     * @param args path to matrix, number of rows, number of columns
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {

        final File dataFile = new File(args[0]);
        final Matrix A = readMatrix(dataFile, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        final PrincipalComponentsAnalysis PCA = new PrincipalComponentsAnalysis(A);

        final File pcaDir = new File("PCA");
        pcaDir.mkdir();

        final Variables vars = new Variables();
        vars.put("sv", "$" + String.join(",\\allowbreak",
                (Iterable<String>) IntStream.range(0, PCA.getPrincipalComponentCount())
                        .mapToDouble(PCA::getSingularValue)
                        .mapToObj(nf::format)::iterator) + "$");
        vars.put("pc", "$" + String.join(",\\allowbreak",
                (Iterable<String>) IntStream.range(0, 5)
                        .mapToObj(PCA::getPrincipalComponent)
                        .map(v -> "\\left[" + String.join(",",
                                (Iterable<String>) IntStream.range(0, 5)
                                        .mapToDouble(i -> v.get(i, 0))
                                        .mapToObj(nf::format)::iterator)
                                + "\\ldots\\right]^\\intercal")::iterator) + "$");

        final Matrix L = PCA.getProjectedData(2);
        writeMatrix(L, new File(pcaDir, "locations.txt"));

        final Table tableL = new Table(50, 3);
        tableL.setAlignment(0, Alignment.LEFT);
        tableL.setHeader(0, "Individual");
        for (int i = 1; i < 3; ++i) {
            tableL.setAlignment(i, Alignment.RIGHT);
            tableL.setHeader(i, "PC" + (i));
        }
        for (int i = 0; i < 50; ++i) {
            tableL.setContent(i, 0, Integer.toString(i+1));
            for (int j = 0; j < 2; ++j)
                tableL.setContent(i, j+1, nf.format(L.get(i, j)));
        }
        tableL.write(new File(pcaDir, "locations1.tex"));
        for (int i = 50; i < 100; ++i) {
            tableL.setContent(i-50, 0, Integer.toString(i+1));
            for (int j = 0; j < 2; ++j)
                tableL.setContent(i-50, j+1, nf.format(L.get(i, j)));
        }
        tableL.write(new File(pcaDir, "locations2.tex"));

        vars.put("biggest", "$" + String.join(",\\allowbreak",
                (Iterable<String>) IntStream.range(0, 100)
                        .filter(i -> L.get(i, 0) > 0)
                        .mapToObj(Integer::toString)::iterator) + "$");

        vars.write(new File(pcaDir, "values.tex"));
    }

}
