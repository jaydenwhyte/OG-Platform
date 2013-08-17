/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.curve.interestrate.generator;

import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.financial.provider.description.interestrate.MulticurveProviderInterface;
import com.opengamma.analytics.math.curve.DoublesCurveNelsonSiegel;
import com.opengamma.util.ArgumentChecker;

/**
 * Store the details and generate the required curve. The yield (continuously compounded) is generated by a Nelson-Siegel function.
 * <p> Reference: Nelson, C.R., Siegel, A.F. (1987). Parsimonious modeling of yield curves, Journal of Business, 60(4):473-489.
 */
public class GeneratorCurveYieldNelsonSiegel extends GeneratorYDCurve {

  /**
   * The number of parameters of the curve.
   */
  private static final int NB_PARAMETERS = 4;

  @Override
  public int getNumberOfParameter() {
    return NB_PARAMETERS;
  }

  @Override
  public YieldAndDiscountCurve generateCurve(final String name, final double[] parameters) {
    ArgumentChecker.isTrue(parameters.length == NB_PARAMETERS, "Nelson-Siegel should have 4 parameters");
    return new YieldCurve(name, new DoublesCurveNelsonSiegel(name, parameters));
  }

  /**
   * {@inheritDoc}
   * @deprecated Curve builders that use and populate {@link YieldCurveBundle}s are deprecated.
   */
  @Deprecated
  @Override
  public YieldAndDiscountCurve generateCurve(final String name, final YieldCurveBundle bundle, final double[] parameters) {
    return generateCurve(name, parameters);
  }

  @Override
  public YieldAndDiscountCurve generateCurve(final String name, final MulticurveProviderInterface multicurve, final double[] parameters) {
    return generateCurve(name, parameters);
  }

  @Override
  public double[] initialGuess(final double[] rates) {
    final double[] guess = rates.clone();
    guess[3] = 2.0; //TODO: get a better guess?
    return guess;
  }

}
