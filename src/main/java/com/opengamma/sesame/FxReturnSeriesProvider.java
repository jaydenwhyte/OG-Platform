/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

import com.opengamma.analytics.financial.schedule.HolidayDateRemovalFunction;
import com.opengamma.analytics.financial.schedule.Schedule;
import com.opengamma.analytics.financial.schedule.TimeSeriesSamplingFunction;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.currency.CurrencyPair;
import com.opengamma.sesame.marketdata.MarketDataProviderFunction;
import com.opengamma.sesame.marketdata.MarketDataRequirementFactory;
import com.opengamma.sesame.marketdata.MarketDataSeries;
import com.opengamma.timeseries.date.localdate.LocalDateDoubleTimeSeries;

public class FxReturnSeriesProvider implements FxReturnSeriesProviderFunction {

  /** Removes weekends */
  private static final HolidayDateRemovalFunction HOLIDAY_REMOVER = HolidayDateRemovalFunction.getInstance();

  /** A weekend calendar */
  private static final Calendar WEEKEND_CALENDAR = new MondayToFridayCalendar("Weekend");

  private final MarketDataProviderFunction _marketDataProviderFunction;

  private final TimeSeriesReturnConverter _timeSeriesConverter;

  private final TimeSeriesSamplingFunction _timeSeriesSamplingFunction;

  private final Schedule _scheduleCalculator;

  public FxReturnSeriesProvider(MarketDataProviderFunction marketDataProviderFunction,
                                TimeSeriesReturnConverter timeSeriesConverter,
                                TimeSeriesSamplingFunction timeSeriesSamplingFunction,
                                Schedule schedule) {
    _marketDataProviderFunction = marketDataProviderFunction;
    _timeSeriesConverter = timeSeriesConverter;
    _timeSeriesSamplingFunction = timeSeriesSamplingFunction;
    _scheduleCalculator = schedule;
  }

  @Override
  public FunctionResult<LocalDateDoubleTimeSeries> calculateReturnSeries(Period seriesPeriod, CurrencyPair currencyPair) {

    FunctionResult<MarketDataSeries> result =
        _marketDataProviderFunction.requestData(MarketDataRequirementFactory.of(currencyPair), seriesPeriod);

    if (result.isResultAvailable()) {

      // todo - is faffing about with include start / end required?
      LocalDateDoubleTimeSeries timeSeries = (LocalDateDoubleTimeSeries) result.getResult().getOnlySeries();

      final LocalDate[] dates = HOLIDAY_REMOVER.getStrippedSchedule(
          _scheduleCalculator.getSchedule(timeSeries.getEarliestTime(), timeSeries.getLatestTime(), true, false), WEEKEND_CALENDAR);
      LocalDateDoubleTimeSeries sampledTimeSeries = _timeSeriesSamplingFunction.getSampledTimeSeries(timeSeries, dates);

      // Implementation note: to obtain the series for one unit of non-base currency expressed in base currency.
      LocalDateDoubleTimeSeries reciprocalSeries = sampledTimeSeries.reciprocal();

      // todo - clip the time-series to the range originally asked for?
      return StandardResultGenerator.success(_timeSeriesConverter.convert(reciprocalSeries));

    } else {
      return StandardResultGenerator.propagateFailure(result);
    }
  }
}
