/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.sesame.marketdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.LocalDate;

import com.opengamma.core.historicaltimeseries.HistoricalTimeSeries;
import com.opengamma.core.historicaltimeseries.HistoricalTimeSeriesSource;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.time.LocalDateRange;

/**
 *
 */
public class RawHistoricalMarketDataSourceImpl implements RawHistoricalMarketDataSource {

  private static final Logger s_logger = LoggerFactory.getLogger(RawHistoricalMarketDataSourceImpl.class);

  protected final HistoricalTimeSeriesSource _timeSeriesSource;
  protected final String _dataSource;
  protected final String _dataProvider;

  public RawHistoricalMarketDataSourceImpl(HistoricalTimeSeriesSource timeSeriesSource,
                                           String dataSource,
                                           String dataProvider) {
    _timeSeriesSource = ArgumentChecker.notNull(timeSeriesSource, "timeSeriesSource");
    _dataProvider = ArgumentChecker.notEmpty(dataProvider, "dataProvider");
    _dataSource = ArgumentChecker.notEmpty(dataSource, "dataSource");
  }

  @Override
  public MarketDataItem get(ExternalIdBundle idBundle, String dataField, LocalDateRange dateRange) {
    LocalDate startDate = dateRange.getStartDateInclusive();
    LocalDate endDate = dateRange.getEndDateInclusive();
    HistoricalTimeSeries hts = _timeSeriesSource.getHistoricalTimeSeries(idBundle, _dataSource, _dataProvider, dataField,
                                                                         startDate, true, endDate, true);
    if (hts == null || hts.getTimeSeries().isEmpty()) {
      s_logger.info("No time-series for {}", idBundle);
      return MarketDataItem.UNAVAILABLE;
    } else {
      return MarketDataItem.available(hts.getTimeSeries());
    }
  }
}
