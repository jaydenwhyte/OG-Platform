/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.timeseries.date;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Specialized iterator that can access primitive values.
 * This iterator is dedicated to {@code DateDoubleTimeSeries}.
 * 
 * @param <T>  the date type
 */
public interface DateDoubleEntryIterator<T> extends Iterator<Map.Entry<T, Double>> {

  /**
   * The next available date in the iterator.
   * Use instead of calling {@code next()}, use this method and {@code currentValue()}.
   * 
   * @return the next date
   * @throws NoSuchElementException if the iterator is exhausted
   */
  int nextTimeFast();

  /**
   * The next available date in the iterator.
   * Use instead of calling {@code next()}, use this method and {@code currentValue()}.
   * 
   * @return the next date, not null
   * @throws NoSuchElementException if the iterator is exhausted
   */
  T nextTime();

  /**
   * The current date in the iterator.
   * This returns the same as the last call to {@code nextTimeFast()}.
   * 
   * @return the current date
   * @throws IllegalStateException if the iterator has not been started
   */
  int currentTimeFast();

  /**
   * The current date in the iterator.
   * This returns the same as the last call to {@code nextTime()}.
   * 
   * @return the current date, not null
   * @throws IllegalStateException if the iterator has not been started
   */
  T currentTime();

  /**
   * The current value in the iterator.
   * This returns the value associated with the last call to {@code next()}.
   * 
   * @return the current value
   * @throws IllegalStateException if the iterator has not been started
   */
  double currentValue();

  /**
   * The current index of the iterator.
   * This returns the index of the time-value pair associated with the
   * last call to {@code next()}, or -1 if iteration has not yet started.
   * 
   * @return the current index, or -1 if iteration has not yet started
   */
  int currentIndex();

}
