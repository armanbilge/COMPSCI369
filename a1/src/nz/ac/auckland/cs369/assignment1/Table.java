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

import java.util.Arrays;

/**
 * Represents a table.
 *
 * @author Arman Bilge
 */
public class Table {

    public enum Alignment {
        LEFT, RIGHT, CENTER
    }

    private final Alignment[] alignment;
    private final String[] header;
    private final String[][] content;

    /**
     * Create a table. All columns default to center alignment.
     * @param rows the number of rows
     * @param cols the number of columns
     */
    public Table(final int rows, final int cols) {
        alignment = new Alignment[cols];
        Arrays.fill(alignment, Alignment.CENTER);
        header = new String[cols];
        content = new String[rows][cols];
    }

    /**
     * Set the alignment for a column.
     * @param col the column number
     * @param align the desired justification
     */
    public void setAlignment(final int col, final Alignment align) {
        alignment[col] = align;
    }

    /**
     * Set the header for a column.
     * @param col the column number
     * @param header the header
     */
    public void setHeader(final int col, final String header) {
        this.header[col] = header;
    }

    /**
     * Set the content for a cell.
     * @param row the row number
     * @param col the column number
     * @param content the content
     */
    public void setContent(final int row, final int col, final String content) {
        this.content[row][col] = content;
    }

    // TODO Implement method for LaTeX and/or Markdown output

}
