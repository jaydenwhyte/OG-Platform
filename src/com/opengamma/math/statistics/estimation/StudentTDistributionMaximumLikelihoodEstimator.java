/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.estimation;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;
import com.opengamma.math.function.special.GammaFunction;
import com.opengamma.math.minimization.GoldenSectionMinimizer1D;
import com.opengamma.math.minimization.Minimizer1D;
import com.opengamma.math.statistics.descriptive.MeanCalculator;
import com.opengamma.math.statistics.descriptive.PopulationStandardDeviationCalculator;
import com.opengamma.math.statistics.distribution.ProbabilityDistribution;
import com.opengamma.math.statistics.distribution.StudentTDistribution;
import com.opengamma.util.ArgumentChecker;

/**
 * 
 */
public class StudentTDistributionMaximumLikelihoodEstimator extends DistributionParameterEstimator<Double> {
  // TODO add error estimates
  private final Minimizer1D _minimizer = new GoldenSectionMinimizer1D();
  private final Function1D<Double, Double> _gamma = new GammaFunction();
  private final Function1D<double[], Double> _mean = new MeanCalculator();
  private final Function1D<double[], Double> _std = new PopulationStandardDeviationCalculator();

  @Override
  public ProbabilityDistribution<Double> evaluate(final double[] x) {
    Validate.notNull(x, "x");
    ArgumentChecker.notEmpty(x, "x");
    final double[] standardized = getStandardizedData(x);
    final Function1D<Double, Double> f = new Function1D<Double, Double>() {

      @SuppressWarnings("synthetic-access")
      @Override
      public Double evaluate(final Double nu) {
        double sum = 0;
        for (final double t : standardized) {
          sum += Math.log(_gamma.evaluate((nu + 1) / 2.) * Math.pow(1 + t * t / (nu - 2), -(nu + 1) / 2.) / Math.sqrt(Math.PI * (nu - 2)) / _gamma.evaluate(nu / 2.));
        }
        return -sum;
      }

    };
    return new StudentTDistribution(_minimizer.minimize(f, new double[] {3., 10.})[0]);
  }

  protected double[] getStandardizedData(final double[] x) {
    final double mean = _mean.evaluate(x);
    final double std = _std.evaluate(x);
    final double[] z = new double[x.length];
    for (int i = 0; i < x.length; i++) {
      z[i] = (x[i] - mean) / std;
    }
    return z;
  }
}
