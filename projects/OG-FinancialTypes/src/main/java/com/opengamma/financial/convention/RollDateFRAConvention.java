/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention;

import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.core.convention.ConventionType;
import com.opengamma.id.ExternalId;
import com.opengamma.id.ExternalIdBundle;
import com.opengamma.util.ArgumentChecker;

/**
 * Convention for IMM FRAs.
 */
@BeanDefinition
public class RollDateFRAConvention extends FinancialConvention {

  /**
   * Type of the convention.
   */
  public static final ConventionType TYPE = ConventionType.of("RollDateFRA");

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The underlying index convention.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _indexConvention;
  /**
   * The IMM date convention.
   */
  @PropertyDefinition(validate = "notNull")
  private ExternalId _rollDateConvention;

  /**
   * Creates an instance.
   */
  RollDateFRAConvention() {
    super();
  }

  /**
   * Creates an instance.
   * 
   * @param name  the convention name, not null
   * @param externalIdBundle  the external identifiers for this convention, not null
   * @param indexConvention  the index convention, not null 
   * @param rollDateConvention  the roll date convention, not null
   */
  public RollDateFRAConvention(
      final String name, final ExternalIdBundle externalIdBundle, final ExternalId indexConvention,
      final ExternalId rollDateConvention) {
    // TODO: Index
    super(name, externalIdBundle);
    setIndexConvention(indexConvention);
    setRollDateConvention(rollDateConvention);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the type identifying this convention.
   * 
   * @return the {@link #TYPE} constant, not null
   */
  @Override
  public ConventionType getConventionType() {
    return TYPE;
  }

  /**
   * Accepts a visitor to manage traversal of the hierarchy.
   *
   * @param <T>  the result type of the visitor
   * @param visitor  the visitor, not null
   * @return the result
   */
  @Override
  public <T> T accept(final FinancialConventionVisitor<T> visitor) {
    ArgumentChecker.notNull(visitor, "visitor");
    return visitor.visitIMMFRAConvention(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code RollDateFRAConvention}.
   * @return the meta-bean, not null
   */
  public static RollDateFRAConvention.Meta meta() {
    return RollDateFRAConvention.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(RollDateFRAConvention.Meta.INSTANCE);
  }

  @Override
  public RollDateFRAConvention.Meta metaBean() {
    return RollDateFRAConvention.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the underlying index convention.
   * @return the value of the property, not null
   */
  public ExternalId getIndexConvention() {
    return _indexConvention;
  }

  /**
   * Sets the underlying index convention.
   * @param indexConvention  the new value of the property, not null
   */
  public void setIndexConvention(ExternalId indexConvention) {
    JodaBeanUtils.notNull(indexConvention, "indexConvention");
    this._indexConvention = indexConvention;
  }

  /**
   * Gets the the {@code indexConvention} property.
   * @return the property, not null
   */
  public final Property<ExternalId> indexConvention() {
    return metaBean().indexConvention().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the IMM date convention.
   * @return the value of the property, not null
   */
  public ExternalId getRollDateConvention() {
    return _rollDateConvention;
  }

  /**
   * Sets the IMM date convention.
   * @param rollDateConvention  the new value of the property, not null
   */
  public void setRollDateConvention(ExternalId rollDateConvention) {
    JodaBeanUtils.notNull(rollDateConvention, "rollDateConvention");
    this._rollDateConvention = rollDateConvention;
  }

  /**
   * Gets the the {@code rollDateConvention} property.
   * @return the property, not null
   */
  public final Property<ExternalId> rollDateConvention() {
    return metaBean().rollDateConvention().createProperty(this);
  }

  //-----------------------------------------------------------------------
  @Override
  public RollDateFRAConvention clone() {
    return JodaBeanUtils.cloneAlways(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      RollDateFRAConvention other = (RollDateFRAConvention) obj;
      return JodaBeanUtils.equal(getIndexConvention(), other.getIndexConvention()) &&
          JodaBeanUtils.equal(getRollDateConvention(), other.getRollDateConvention()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getIndexConvention());
    hash += hash * 31 + JodaBeanUtils.hashCode(getRollDateConvention());
    return hash ^ super.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("RollDateFRAConvention{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  @Override
  protected void toString(StringBuilder buf) {
    super.toString(buf);
    buf.append("indexConvention").append('=').append(JodaBeanUtils.toString(getIndexConvention())).append(',').append(' ');
    buf.append("rollDateConvention").append('=').append(JodaBeanUtils.toString(getRollDateConvention())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RollDateFRAConvention}.
   */
  public static class Meta extends FinancialConvention.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code indexConvention} property.
     */
    private final MetaProperty<ExternalId> _indexConvention = DirectMetaProperty.ofReadWrite(
        this, "indexConvention", RollDateFRAConvention.class, ExternalId.class);
    /**
     * The meta-property for the {@code rollDateConvention} property.
     */
    private final MetaProperty<ExternalId> _rollDateConvention = DirectMetaProperty.ofReadWrite(
        this, "rollDateConvention", RollDateFRAConvention.class, ExternalId.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "indexConvention",
        "rollDateConvention");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -668532253:  // indexConvention
          return _indexConvention;
        case 509875100:  // rollDateConvention
          return _rollDateConvention;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends RollDateFRAConvention> builder() {
      return new DirectBeanBuilder<RollDateFRAConvention>(new RollDateFRAConvention());
    }

    @Override
    public Class<? extends RollDateFRAConvention> beanType() {
      return RollDateFRAConvention.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code indexConvention} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> indexConvention() {
      return _indexConvention;
    }

    /**
     * The meta-property for the {@code rollDateConvention} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ExternalId> rollDateConvention() {
      return _rollDateConvention;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -668532253:  // indexConvention
          return ((RollDateFRAConvention) bean).getIndexConvention();
        case 509875100:  // rollDateConvention
          return ((RollDateFRAConvention) bean).getRollDateConvention();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -668532253:  // indexConvention
          ((RollDateFRAConvention) bean).setIndexConvention((ExternalId) newValue);
          return;
        case 509875100:  // rollDateConvention
          ((RollDateFRAConvention) bean).setRollDateConvention((ExternalId) newValue);
          return;
      }
      super.propertySet(bean, propertyName, newValue, quiet);
    }

    @Override
    protected void validate(Bean bean) {
      JodaBeanUtils.notNull(((RollDateFRAConvention) bean)._indexConvention, "indexConvention");
      JodaBeanUtils.notNull(((RollDateFRAConvention) bean)._rollDateConvention, "rollDateConvention");
      super.validate(bean);
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
