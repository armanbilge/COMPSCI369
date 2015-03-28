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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arman Bilge
 */
public final class PGMIO {

    private static final String MAGIC = "P5";

    private PGMIO() {}

    public static byte[][] read(final File file) throws IOException {
        try (final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            if (!next(stream).equals(MAGIC))
                throw new IOException("File " + file + " is not a binary PGM image.");
            final int col = Integer.parseInt(next(stream));
            final int row = Integer.parseInt(next(stream));
            final int max = Integer.parseInt(next(stream));
            if (max > 255)
                throw new IOException("Images with greater than 256 shades of gray are not supported.");
            final byte[][] image = new byte[row][col];
            for (int i = 0; i < row; ++i) {
                for (int j = 0; j < col; ++j) {
                    final int p = stream.read();
                    if (p >= max)
                        throw new IOException("Pixel value " + p + " greater than specified max " + max + ".");
                    image[i][j] = (byte) p;
                }
            }
            return image;
        }
    }

    private static String next(final InputStream stream) throws IOException {
        final List<Byte> bytes = new ArrayList<Byte>();
        int b;
        do {
            b = stream.read();
            if (!Character.isWhitespace((char) b)) bytes.add((byte) b);
        } while (b != -1 && !Character.isWhitespace((char) b));
        final byte[] bytesArray = new byte[bytes.size()];
        for (int i = 0; i < bytesArray.length; ++i)
            bytesArray[i] = bytes.get(i);
        return new String(bytesArray);
    }

    public static void write(final byte[][] image, final File file) throws IOException {
        try (final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file))) {
            stream.write(MAGIC.getBytes());
            stream.write("\n".getBytes());
            stream.write(Integer.toString(image[0].length).getBytes());
            stream.write(" ".getBytes());
            stream.write(Integer.toString(image.length).getBytes());
            stream.write("\n".getBytes());
            stream.write("255".getBytes());
            stream.write("\n".getBytes());
            for (int i = 0; i < image.length; ++i) {
                for (int j = 0; j < image[0].length; ++j) {
                    stream.write(image[i][j]);
                }
            }
        }
    }

}
