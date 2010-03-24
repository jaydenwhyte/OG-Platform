/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.opengamma.engine.position.PortfolioNode;
import com.opengamma.engine.position.Position;
import com.opengamma.engine.security.Security;
import com.opengamma.id.DomainSpecificIdentifier;
import com.opengamma.id.Identifiable;
import com.opengamma.util.ArgumentChecker;

/**
 * A fully resolved target, sufficient for computation invocation.
 *
 * @author kirk
 */
public class ComputationTarget implements Serializable {
  private final ComputationTargetType _type;
  private final Identifiable _value;
  
  public ComputationTarget(ComputationTargetType type, Identifiable value) {
    checkValueValid(type, value);
    
    _type = type;
    _value = value;
  }
  
  /**
   * Determine whether the value provided is valid given the computation target type
   * specified.
   * If it is not valid, this method will throw an {@link IllegalArgumentException}.
   * If it is not provided but required, this method will throw an
   * {@link NullPointerException}.
   * 
   * @param type  The target type required.
   * @param value The value provided.
   */
  public static void checkValueValid(ComputationTargetType type, Object value) {
    ArgumentChecker.checkNotNull(type, "Computation Target Type");
    ArgumentChecker.checkNotNull(value, "Value");
    
    // Now check argument assignment.
    switch(type) {
    case PRIMITIVE:
      // Value can be null or anything else for a primitive. No constraints apply.
      break;
    case SECURITY:
      if(!(value instanceof Security)) {
        throw new IllegalArgumentException("SECURITY target type requires value of type Security. Provided " + value);
      }
      break;
    case POSITION:
      if(!(value instanceof Position)) {
        throw new IllegalArgumentException("POSITION target type requires value of type Position. Provided " + value);
      }
      break;
    case MULTIPLE_POSITIONS:
      if(!(value instanceof PortfolioNode)) {
        throw new IllegalArgumentException("MULTIPLE_POSITIONS target type requires value of type PortfolioNode. Provided " + value);
      }
      break;
    default:
      throw new AssertionError("Unimplemented ComputationTargetType");
    }
  }

  /**
   * @return the type
   */
  public ComputationTargetType getType() {
    return _type;
  }
  /**
   * @return the value
   */
  public Identifiable getValue() {
    return _value;
  }
  
  public DomainSpecificIdentifier getUniqueIdentifier() {
    return getValue().getIdentityKey();
  }
  
  public Security getSecurity() {
    if(getType() != ComputationTargetType.SECURITY) {
      throw new IllegalStateException("Requested a Security for a target of type " + getType());
    }
    return (Security)getValue();
  }

  public Position getPosition() {
    if(getType() != ComputationTargetType.POSITION) {
      throw new IllegalStateException("Requested a Position for a target of type " + getType());
    }
    return (Position)getValue();
  }

  public PortfolioNode getPortfolioNode() {
    if(getType() != ComputationTargetType.MULTIPLE_POSITIONS) {
      throw new IllegalStateException("Requested a PortfolioNode for a target of type " + getType());
    }
    return (PortfolioNode)getValue();
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null) {
      return false;
    }
    if(!(obj instanceof ComputationTarget)) {
      return false;
    }
    ComputationTarget other = (ComputationTarget) obj;
    if(_type != other._type) {
      return false;
    }
    if(!ObjectUtils.equals(_value, other._value)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _type.hashCode();
    if(_value != null) {
      result = prime * result + _value.hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
  
  public ComputationTargetSpecification getSpecification() {
    return new ComputationTargetSpecification(_type, getUniqueIdentifier());
  }

}
