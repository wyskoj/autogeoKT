package org.wysko.autogeokt.linalg

/**
 * Options for the least-squares iteration.
 *
 * @property convergenceThreshold The threshold for the objective function to converge.
 * @property iterationThreshold The maximum number of iterations to perform before throwing an exception.
 */
data class LeastSquaresIterationOptions(
    val convergenceThreshold: Double = 1E-4,
    val iterationThreshold: Int = 100,
)
