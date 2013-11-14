/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import static com.opengamma.sesame.StandardResultGenerator.propagateFailure;
import static com.opengamma.sesame.StandardResultGenerator.success;

import com.opengamma.financial.analytics.CurrencyLabelledMatrix1D;
import com.opengamma.financial.security.fx.FXForwardSecurity;

public class DiscountingFXForwardPV implements FXForwardPVFunction {

  private final FxForwardCalculatorProvider _fxForwardCalculatorProvider;

  public DiscountingFXForwardPV(FxForwardCalculatorProvider fxForwardCalculatorProvider) {

    _fxForwardCalculatorProvider = fxForwardCalculatorProvider;
  }

  @Override
  public FunctionResult<CurrencyLabelledMatrix1D> calculatePV(FXForwardSecurity security) {

    FunctionResult<FxForwardCalculator> result = _fxForwardCalculatorProvider.generateCalculator(security);
    if (result.isResultAvailable()) {
      return success(result.getResult().calculatePV());
    } else {
      return propagateFailure(result);
    }
  }
}
