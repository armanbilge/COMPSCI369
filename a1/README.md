% COMPSCI 369 Assignment 1
% Arman Bilge (`abil933`\; 8079403)
% 6 April 2015

\frenchspacing

\newcommand{\imat}[2]{\left[\hspace{0.5em}\vcenter{\hbox{\includegraphics[#2]{#1}}}\hspace{0.5em}\right]}

\newcommand{\mat}[1]{\ensuremath{\mathbf{#1}}}

# Introduction

This document represents my written report for COMPSCI 369 Assignment 1.
To generate a PDF version of this report, simply type `rake` at the command line.
The generated report can be found at `doc/report.pdf`.
Please note that you will need the following software installed.

* ImageMagick
* Java JDK 1.8
* \LaTeX
* Pandoc
* R
* Rake

The `Rakefile` build script's default task automatically compiles and runs all code.
Generated data, tables and figures are placed in `out/`.
Alternatively, the specific tasks available can be viewed using `rake -T`.
```
rake clean     # Remove any temporary products
rake clobber   # Remove any generated file
rake default   # Run all code and generate PDF report
rake problem1  # Create problem 1 files
rake problem2  # Create problem 2 files
rake problem3  # Create problem 3 files
rake problem4  # Create problem 4 files
```

![The $120\times100$ pixel image of myself used for problems 1 and 2.](../data/arman.png)

\newpage

# Problem 1

The singular value decomposition for my photo is
\begin{equation*}
\overbrace{\imat{../data/arman.png}{width=1.5cm}}^\mat{A}
=
\overbrace{\imat{SVD/U.png}{width=1.5cm}}^\mat{U}
\overbrace{\imat{SVD/D.png}{width=1.5cm}}^\mat{D}
\overbrace{\imat{SVD/V.png}{width=1.5cm}^\intercal}^{\mat{V}^\intercal},
\end{equation*}
as computed using the JAMA SVD implementation.

Below is a table showing approximations, and their error, to this image using the first $\rho$ singular values
and columns of $\mat{U}$ and $\mat{V}$.
Errors were computed by constructing an error matrix $\mat{E} = \left|\mat{A} - \widehat{\mat{A}}_\rho\right|$,
whose values are the absolute values of the difference between the original and approximation.
Maximum errors and mean errors were then calculated accordingly.

\begin{table}[h]
\caption{Several SVD compressions of my photo and their error.}
\vspace{10pt}
\centering
\input{Ahat/compression.tex}
\end{table}

The number of real numbers $k_\rho$ used in the $\rho$th approximation is
\begin{equation*}
k_\rho = \left(1 + 120 + 100\right)\rho = 221\rho,
\end{equation*}
as there is $1$ real number per singular value,
$120$ real numbers per column of $\mat{U}$, and
$100$ real numbers per column of $\mat{V}$.
The compression is negative when
\begin{align*}
1 - \frac{k_\rho}{12000} < 0 &\iff 1 < \frac{k_\rho}{12000} \\
                            &\iff 12000 < k_\rho \\
                            &\iff 12000 < 221\rho \\
                            &\implies 54 < \rho.
\end{align*}
A negative value for compression corresponds to a greater number of real numbers
used than in the original image.
Therefore, for $\rho > 54$ we get reduced image quality at greater memory footprint!

I feel that the compressed approximation at $\rho = 40$ is quite acceptable,
particularly based on my facial details.
There is substantial noise in the sky, but I deem this less important to the
photo's overall quality.
Furthermore, this noise never quite goes away for values of $\rho \leq 54$.
For $\rho = 40$, the corresponding compression is $26.33\%$ and the mean error is $4.51$.

# Problem 2

The product of the pseudo-inverse of my photo and itself is given by
\begin{equation*}
\overbrace{\imat{../data/arman.png}{width=1.5cm}}^\mat{A}
\implies
\overbrace{\imat{inverse/Pinv.png}{height=1.5cm}}^{\mat{P}_\text{inv}}
\overbrace{\imat{../data/arman.png}{width=1.5cm}}^\mat{A}
=
\overbrace{\imat{inverse/Ihat.png}{width=1.5cm}}^\mat{\widehat{I}},
\end{equation*}
where the pseudo-inverse was computed by performing SVD on the photo,
computing the pseudo-inverse $\mat{D}^+$ of the diagonal matrix, and reassembling as
\begin{equation*}
\mat{P}_\text{inv} = \mat{V} \mat{D}^+ \mat{U}^\intercal
\end{equation*}

\input{inverse/error.tex}

For $\widehat{\mat{I}}$, the error range is \range, the mean is \mean,
and the standard deviation is \stdev.

The matrix $\mat{B}$, given below, is like a noisy identity matrix relative to $\widehat{\mat{I}}$.

\begin{equation*}
\overbrace{\imat{../data/arman.png}{width=1.5cm}}^\mat{A}
\overbrace{\imat{inverse/Pinv.png}{height=1.5cm}}^{\mat{P}_\text{inv}}
=
\overbrace{\imat{inverse/B.png}{width=1.8cm}}^\mat{B}
\end{equation*}

# Problem 3

In spirit of the `EigenvalueDecomposition` and `SingularValueDecomposition` classes in JAMA,
I created a `PrincipalComponentsAnalysis` class to perform PCA on a data matrix.
For numerical stability, instead of explicitly forming the covariance matrix,
it uses the SVD of the data matrix to compute the eigenvalues and eigenvectors
of the covariance matrix.
Suppose that $\mat{A}$ is an $m \times n$ centred data matrix,
where rows represent samples and columns represent variables.
Then the SVD yields
\begin{equation*}
\mat{A} = \mat{U}\mat{D}\mat{V}^\intercal,
\end{equation*}
where the columns of $\mat{V}$ are the principal components
(i.e., the eigenvectors of the covariance matrix).
Note that because the JAMA SVD implementation insists that $m \geq n$,
if necessary my PCA implementation computes the SVD of $\mat{A}^\intercal$.
In this case, the principal components are given by $\mat{U}$.

\input{PCA/values.tex}

The singular values for the SNP dataset are

\sv.

The first 5 principal components associated with the largest singular values are

\pc.

To find the position of individuals along the first two principal components,
I created a projection matrix with the first two principal components as columns
and multiplied the original data matrix by it.
The resulting projection is plotted and tabulated below.

\begin{figure}[h]
\centering
\includegraphics[width=0.667\textwidth]{PCA/mySNPplot.pdf}
\caption{Position of individuals along first two principal components.}
\end{figure}

Visual inspection of the plot suggested there are 3 subpopulations, represented by the rules
\begin{equation*}
\left\{i:x_i>0\right\},\left\{i:x_i<0,y_i>0\right\},\left\{i:x_i<0,y_i<0\right\},
\end{equation*}
where $x$ refers to the first principal component and $y$ to the second.
The former of these is the largest, and contains the individuals

\biggest.

\begin{table}[h]
\caption{Position of each individual along the first two principal components.}
\vspace{10pt}
\centering
\input{PCA/locations1.tex}
\hspace{4em}
\input{PCA/locations2.tex}
\end{table}

# Problem 4

Newton's root-finding algorithm says that for a given $x_0$,
$\lim_{i\to\infty} f\left(x_i\right) = 0$,
where $x_{i+1} = x_i - \frac{f\left(x_i\right)}{f^\prime\left(x_i\right)}$.
I created a generic implementation of the algorithm by writing a function
that takes a `double` and two `DoubleUnaryOperator`s as its arguments,
representing the intial value $x_0$, $f\left(x\right)$,
and $f^\prime\left(x\right)$, respectively.
My implementation terminates when $\left|x_i - x_{i-1}\right| \leq 0.0001$.

\input{newton/roots.tex}

Let
\begin{equation*}
f\left(x\right) = 2x^3 - 15x^2 + 36x - 23
\implies \frac{d}{dx} f\left(x\right) = 6x^2 - 30x + 36
\end{equation*}
and
\begin{equation*}
g\left(x\right) = \exp\left(0.1x\right) - \exp\left(-0.4x\right) - 1
\implies \frac{d}{dx} g\left(x\right) = 0.1\exp\left(0.1x\right) + 0.4\exp\left(-0.4x\right).
\end{equation*}

With the initial $x_0 = 0$, my implementation found
that $f\left(\rootf\right) \approx 0$ and $g\left(\rootg\right) \approx 0$.
Visual inspection of plots of $f\left(x\right)$ and $g\left(x\right)$ indicated
that both have exactly one root, which is at the $x$-value reported by my implementation.
The following tables log the iterations taken by the algorithm.

\begin{table}[h]
\caption{Four iterations of Newton's root-finding algorithm applied to $f\left(x\right)$.}
\vspace{10pt}
\centering
\input{newton/f.tex}
\end{table}

\begin{table}[h]
\caption{Four iterations of Newton's root-finding algorithm applied to $g\left(x\right)$.}
\vspace{10pt}
\centering
\input{newton/g.tex}
\end{table}
