/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.forward;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.financial.security.FinancialSecurityVisitor;
import com.opengamma.util.money.Currency;
import com.opengamma.util.time.Expiry;

/**
 * A security for energy futures.
 */
@BeanDefinition
public class EnergyForwardSecurity extends CommodityForwardSecurity {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  EnergyForwardSecurity() { //For builder
    super();
  }

  public EnergyForwardSecurity(String unitName, Double unitNumber, Expiry expiry, Currency currency, double unitAmount, String category) {
    super(unitName, unitNumber, expiry, currency, unitAmount, category);
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(FinancialSecurityVisitor<T> visitor) {
    return visitor.visitEnergyForwardSecurity(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code EnergyForwardSecurity}.
   * @return the meta-bean, not null
   */
  public static EnergyForwardSecurity.Meta meta() {
    return EnergyForwardSecurity.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(EnergyForwardSecurity.Meta.INSTANCE);
  }

  @Override
  public EnergyForwardSecurity.Meta metaBean() {
    return EnergyForwardSecurity.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code EnergyForwardSecurity}.
   */
  public static class Meta extends CommodityForwardSecurity.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends EnergyForwardSecurity> builder() {
      return new DirectBeanBuilder<EnergyForwardSecurity>(new EnergyForwardSecurity());
    }

    @Override
    public Class<? extends EnergyForwardSecurity> beanType() {
      return EnergyForwardSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
