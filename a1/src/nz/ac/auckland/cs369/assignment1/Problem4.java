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

import nz.ac.auckland.cs369.assignment1.Table.Alignment;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Code for problem 4.
 *
 * @author Arman Bilge
 */
public final class Problem4 {

    private static final NumberFormat nf = DecimalFormat.getInstance();
    static {
        nf.setMaximumFractionDigits(5);
    }

    private Problem4() {}

    /**
     * Uses Newton's method to find a root of a function.
     * @param x_0 the initial value
     * @param f the function
     * @param dfdx the derivative of the function with respect to x
     * @param xs a log of x_i values
     * @return a root of the function
     */
    private static double newtonsMethod(final double x_0,
                                        final DoubleUnaryOperator f,
                                        final DoubleUnaryOperator dfdx,
                                        final List<Double> xs) {
        xs.add(x_0);
        double x_im1 = x_0;
        double x_i;
        while (true) {
            x_i = iterateNewtonsMethod(x_im1, f, dfdx);
            xs.add(x_i);
            if (Math.abs(x_i - x_im1) <= 0.0001)
                break;
            x_im1 = x_i;
        }
        return x_i;
    }

    /**
     * Computes an iteration of Newton's method.
     * @param x_i the current value of x
     * @param f the function
     * @param dfdx the derivative of the function with respect to x
     * @return the next value of x
     */
    private static double iterateNewtonsMethod(final double x_i,
                                               final DoubleUnaryOperator f,
                                               final DoubleUnaryOperator dfdx) {
        return x_i - f.applyAsDouble(x_i) / dfdx.applyAsDouble(x_i);
    }

    /**
     * Creates a table documenting the steps of Newton's method.
     * @param x x_i from iterations of the method
     * @param f the function
     * @param dfdx the derivative of the function with respect to x
     * @param name the name of the function
     * @return table of steps
     */
    private static Table createTable(final List<Double> x,
                                     final DoubleUnaryOperator f,
                                     final DoubleUnaryOperator dfdx,
                                     final String name) {
        final Table table = new Table(x.size() - 1, 5);
        int c = 0;
        table.setHeader(c++, "Step $i$");
        table.setHeader(c++, "$x_i$");
        table.setHeader(c++, "$" + name + "\\left(x_i\\right)$");
        table.setHeader(c++, "$\\frac{d" + name + "\\left(x\\right)}{dx}\\big|_{x = x_i}$");
        for (int i = 0; i < 5; ++i) table.setAlignment(i, Alignment.RIGHT);
        table.setHeader(c++, "$x_{i+1}$");
        for (int i = 0; i < x.size() - 1; ++i) {
            c = 0;
            table.setContent(i, c++, Integer.toString(i));
            final double x_i = x.get(i);
            table.setContent(i, c++, "$" + nf.format(x.get(i)) + "$");
            table.setContent(i, c++, "$" + nf.format(f.applyAsDouble(x_i)) + "$");
            table.setContent(i, c++, "$" + nf.format(dfdx.applyAsDouble(x_i)) + "$");
            table.setContent(i, c++, "$" + nf.format(x.get(i+1)) + "$");
        }
        return table;
    }

    /**
     * Creates all necessary files for problem 4.
     * @param args No command line arguments
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {

        final Variables roots = new Variables();

        final DoubleUnaryOperator f = x -> 2 * x * x * x - 15 * x * x + 36 * x - 23;
        final DoubleUnaryOperator dfdx = x -> 6 * x * x - 30 * x + 36;
        final List<Double> fxs = new ArrayList<>();
        final double rf = newtonsMethod(0, f, dfdx, fxs);
        roots.put("rootf", nf.format(rf));
        final Table tableF = createTable(fxs, f, dfdx, "f");

        final DoubleUnaryOperator g = x -> Math.exp(0.1 * x) - Math.exp(-0.4 * x) - 1;
        final DoubleUnaryOperator dgdx = x -> 0.1 * Math.exp(0.1 * x) + 0.4 * Math.exp(-0.4 * x);
        final List<Double> gxs = new ArrayList<>();
        final double rg = newtonsMethod(0, g, dgdx, gxs);
        roots.put("rootg", nf.format(rg));
        final Table tableG = createTable(gxs, g, dgdx, "g");

        final File newtonDir = new File("newton");
        newtonDir.mkdir();
        roots.write(new File(newtonDir, "roots.tex"));
        tableF.write(new File(newtonDir, "f.tex"));
        tableG.write(new File(newtonDir, "g.tex"));

    }

}
