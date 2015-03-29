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
public class Problem4 {

    private static final NumberFormat nf = DecimalFormat.getInstance();
    static {
        nf.setMaximumFractionDigits(5);
    }

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
        double x_i = x_0;
        while (true) {
            final double x_ip1 = iterateNewtonsMethod(x_i, f, dfdx);
            xs.add(x_ip1);
            if (Math.abs(x_i - x_ip1) <= 0.0001)
                break;
            x_i = x_ip1;
        }
        return x_i;
    }

    /**
     * Computes an iteration of Newton's method.
     * @param x_i
     * @param f the function
     * @param dfdx the derivative of the function with respect to x
     * @return x_(i+1)
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
        table.setHeader(0, "Step $i$");
        table.setHeader(1, "$x_i$");
        table.setHeader(2, "$" + name + "(x_i)$");
        table.setHeader(3, "$\\frac{d" + name + "(x)}{dx}\\big|_{x = x_i}");
        table.setHeader(4, "$x_{i+1}$");
        for (int i = 0; i < x.size() - 1; ++i) {
            table.setContent(i, 0, Integer.toString(i));
            final double x_i = x.get(i);
            table.setContent(i, 1, nf.format(x.get(i)));
            table.setContent(i, 2, nf.format(f.applyAsDouble(x_i)));
            table.setContent(i, 3, nf.format(dfdx.applyAsDouble(x_i)));
            table.setContent(i, 4, nf.format(x.get(i+1)));
        }
        return table;
    }

    public static void main(final String... args) {

        final DoubleUnaryOperator f = x -> 2 * x * x * x - 15 * x * x + 36 * x - 23;
        final DoubleUnaryOperator dfdx = x -> 6 * x * x - 30 * x + 36;
        final List<Double> fxs = new ArrayList<>();
        newtonsMethod(0, f, dfdx, fxs);
        final Table tableF = createTable(fxs, f, dfdx, "f");

        final DoubleUnaryOperator g = x -> Math.exp(0.1 * x) - Math.exp(-0.4 * x) - 1;
        final DoubleUnaryOperator dgdx = x -> 0.1 * Math.exp(0.1 * x) + 0.4 * Math.exp(-0.4 * x);
        final List<Double> gxs = new ArrayList<>();
        newtonsMethod(0, g, dgdx, gxs);
        final Table tableG = createTable(gxs, g, dgdx, "g");

        // TODO Write out tables
    }

}
