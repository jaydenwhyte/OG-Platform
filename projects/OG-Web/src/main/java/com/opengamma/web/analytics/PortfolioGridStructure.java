/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opengamma.core.position.Portfolio;
import com.opengamma.core.position.PortfolioNode;
import com.opengamma.core.position.Position;
import com.opengamma.core.position.Trade;
import com.opengamma.core.position.impl.PortfolioMapper;
import com.opengamma.core.position.impl.PortfolioMapperFunction;
import com.opengamma.core.security.Security;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.target.ComputationTargetType;
import com.opengamma.engine.value.ValueProperties;
import com.opengamma.engine.view.ViewCalculationConfiguration;
import com.opengamma.engine.view.ViewDefinition;
import com.opengamma.engine.view.compilation.CompiledViewDefinition;
import com.opengamma.financial.security.FinancialSecurity;
import com.opengamma.id.UniqueId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.Pair;
import com.opengamma.web.analytics.blotter.BlotterColumn;
import com.opengamma.web.analytics.blotter.BlotterColumnMapper;

/**
 * The structure of the grid that displays portfolio data and analytics. Contains the column definitions and
 * the portfolio tree structure.
 */
public final class PortfolioGridStructure extends MainGridStructure {

  /** The root node of the portfolio structure. */
  private final AnalyticsNode _root;

  private PortfolioGridStructure(GridColumnGroups columnGroups,
                                 CompiledViewDefinition compiledViewDef,
                                 TargetLookup targetLookup) {
    super(columnGroups, targetLookup);
    _root = AnalyticsNode.portoflioRoot(compiledViewDef);
  }

  private PortfolioGridStructure() {
    _root = AnalyticsNode.emptyRoot();
  }

  /* package */ static PortfolioGridStructure forAnalytics(CompiledViewDefinition compiledViewDef,
                                                           ValueMappings valueMappings) {
    ArgumentChecker.notNull(compiledViewDef, "compiledViewDef");
    ArgumentChecker.notNull(valueMappings, "valueMappings");
    List<PortfolioGridRow> rows = buildRows(compiledViewDef);
    TargetLookup targetLookup = new TargetLookup(valueMappings, rows);
    GridColumnGroup fixedColumns = buildFixedColumns(rows);
    List<GridColumnGroup> analyticsColumns = buildAnalyticsColumns(compiledViewDef.getViewDefinition(), targetLookup);
    List<GridColumnGroup> groups = Lists.newArrayList(fixedColumns);
    groups.addAll(analyticsColumns);
    GridColumnGroups columnGroups = new GridColumnGroups(groups);
    return new PortfolioGridStructure(columnGroups, compiledViewDef, targetLookup);
  }

  /* package */ static PortfolioGridStructure forBlotter(CompiledViewDefinition compiledViewDef,
                                                         ValueMappings valueMappings,
                                                         BlotterColumnMapper columnMappings) {
    ArgumentChecker.notNull(compiledViewDef, "compiledViewDef");
    ArgumentChecker.notNull(valueMappings, "valueMappings");
    ArgumentChecker.notNull(columnMappings, "columnMappings");
    List<PortfolioGridRow> rows = buildRows(compiledViewDef);
    TargetLookup targetLookup = new TargetLookup(valueMappings, rows);
    GridColumnGroup fixedColumns = buildFixedColumns(rows);
    GridColumnGroup blotterColumns = buildBlotterColumns(columnMappings, rows);
    List<GridColumnGroup> analyticsColumns = buildAnalyticsColumns(compiledViewDef.getViewDefinition(), targetLookup);
    List<GridColumnGroup> groups = Lists.newArrayList(fixedColumns);
    groups.add(blotterColumns);
    groups.addAll(analyticsColumns);
    GridColumnGroups columnGroups = new GridColumnGroups(groups);
    return new PortfolioGridStructure(columnGroups, compiledViewDef, targetLookup);
  }

  /* package */ static PortfolioGridStructure empty() {
    return new PortfolioGridStructure();
  }

  /**
   * @return The root node of the portfolio structure.
   */
  public AnalyticsNode getRoot() {
    return _root;
  }

  private static GridColumnGroup buildFixedColumns(List<PortfolioGridRow> rows) {
    GridColumn labelColumn = new GridColumn("Label", "", null, new PortfolioLabelRenderer(rows));
    GridColumn quantityColumn = new GridColumn("Quantity", "", BigDecimal.class, new QuantityRenderer(rows), null);
    return new GridColumnGroup("fixed", ImmutableList.of(labelColumn, quantityColumn), false);
  }

  /**
   * @param viewDef The view definition
   * @return Columns for displaying calculated analytics data, one group per calculation configuration
   */
  private static List<GridColumnGroup> buildAnalyticsColumns(ViewDefinition viewDef, TargetLookup targetLookup) {
    List<GridColumnGroup> columnGroups = Lists.newArrayList();
    Set<ColumnSpecification> columnSpecs = Sets.newHashSet();
    for (ViewCalculationConfiguration calcConfig : viewDef.getAllCalculationConfigurations()) {
      List<GridColumn> columns = Lists.newArrayList();
      for (Pair<String, ValueProperties> portfolioOutput : calcConfig.getAllPortfolioRequirements()) {
        String valueName = portfolioOutput.getFirst();
        Class<?> columnType = ValueTypes.getTypeForValueName(valueName);
        ValueProperties constraints = portfolioOutput.getSecond();
        ColumnSpecification columnSpec = new ColumnSpecification(calcConfig.getName(), valueName, constraints);
        // ensure columnSpec isn't a duplicate
        if (columnSpecs.add(columnSpec)) {
          columns.add(GridColumn.forSpec(columnSpec, columnType, targetLookup));
        }
      }
      if (!columns.isEmpty()) {
        columnGroups.add(new GridColumnGroup(calcConfig.getName(), columns, true));
      }
    }
    return columnGroups;
  }

  private static GridColumnGroup buildBlotterColumns(BlotterColumnMapper columnMappings,
                                                     List<PortfolioGridRow> rows) {
    List<GridColumn> columns = Lists.newArrayList(
        blotterColumn(BlotterColumn.TYPE, columnMappings, rows),
        blotterColumn(BlotterColumn.PRODUCT, columnMappings, rows),
        blotterColumn(BlotterColumn.QUANTITY, columnMappings, rows),
        blotterColumn(BlotterColumn.DIRECTION, columnMappings, rows),
        blotterColumn(BlotterColumn.START, columnMappings, rows),
        blotterColumn(BlotterColumn.MATURITY, columnMappings, rows),
        blotterColumn(BlotterColumn.RATE, columnMappings, rows),
        blotterColumn(BlotterColumn.INDEX, columnMappings, rows),
        blotterColumn(BlotterColumn.FREQUENCY, columnMappings, rows),
        blotterColumn(BlotterColumn.FLOAT_FREQUENCY, columnMappings, rows));
    return new GridColumnGroup("Blotter", columns, false);
  }

  private static GridColumn blotterColumn(BlotterColumn column,
                                          BlotterColumnMapper columnMappings,
                                          List<PortfolioGridRow> rows) {
    return new GridColumn(column.getName(), "", String.class, new BlotterColumnRenderer(column, columnMappings, rows));
  }


  private static List<PortfolioGridRow> buildRows(final CompiledViewDefinition viewDef) {
    final Portfolio portfolio = viewDef.getPortfolio();
    if (portfolio == null) {
      return Collections.emptyList();
    }
    PortfolioMapperFunction<List<PortfolioGridRow>> targetFn = new PortfolioMapperFunction<List<PortfolioGridRow>>() {

      @Override
      public List<PortfolioGridRow> apply(PortfolioNode node) {
        ComputationTargetSpecification target =
            new ComputationTargetSpecification(ComputationTargetType.PORTFOLIO_NODE, node.getUniqueId());
        String nodeName;
        // if the parent ID is null it's the root node
        if (node.getParentNodeId() == null) {
          // the root node is called "Root" which isn't any use for displaying in the UI, use the portfolio name instead
          nodeName = portfolio.getName();
        } else {
          nodeName = node.getName();
        }
        return Lists.newArrayList(new PortfolioGridRow(target, nodeName, node.getUniqueId()));
      }

      @Override
      public List<PortfolioGridRow> apply(PortfolioNode parentNode, Position position) {
        ComputationTargetSpecification nodeSpec = ComputationTargetSpecification.of(parentNode);
        // TODO I don't think toLatest() will do long term. resolution time available on the result model
        UniqueId positionId = position.getUniqueId();
        ComputationTargetSpecification target = nodeSpec.containing(ComputationTargetType.POSITION,
                                                                    positionId.toLatest());
        Security security = position.getSecurity();
        List<PortfolioGridRow> rows = Lists.newArrayList();
        UniqueId nodeId = parentNode.getUniqueId();
        if (isFungible(position.getSecurity())) {
          rows.add(new PortfolioGridRow(target, security.getName(), security, position.getQuantity(), nodeId, positionId));
          for (Trade trade : position.getTrades()) {
            String tradeDate = trade.getTradeDate().toString();
            rows.add(new PortfolioGridRow(ComputationTargetSpecification.of(trade), tradeDate, security, trade.getQuantity(),
                                          nodeId, positionId, trade.getUniqueId()));
          }
        } else {
          Collection<Trade> trades = position.getTrades();
          if (trades.isEmpty()) {
            rows.add(new PortfolioGridRow(target, security.getName(), security, position.getQuantity(), nodeId, positionId));
          } else {
            // there is never more than one trade on a position in an OTC security
            UniqueId tradeId = trades.iterator().next().getUniqueId();
            rows.add(new PortfolioGridRow(target, security.getName(), security, position.getQuantity(), nodeId, positionId, tradeId));
          }
        }
        return rows;
      }
    };
    List<List<PortfolioGridRow>> rows = PortfolioMapper.map(portfolio.getRootNode(), targetFn);
    Iterable<PortfolioGridRow> flattenedRows = Iterables.concat(rows);
    return Lists.newArrayList(flattenedRows);
  }

  /**
   * @param security A security
   * @return true if the security is fungible, false if OTC
   */
  private static boolean isFungible(Security security) {
    if (security instanceof FinancialSecurity) {
      return !((FinancialSecurity) security).accept(new OtcSecurityVisitor());
    } else {
      return false;
    }
  }
}

/**
 * A row in the grid. TODO subclass(es) for trades with trade & position ID?
 * also security only belongs in position and trade rows, not nodes. do we really care?
 */
/* package */ class PortfolioGridRow extends MainGridStructure.Row {

  /** The row's security, null if the row represents a node in the portfolio structure. */
  private final Security _security;
  /** The row's quantity, null for row's that don't represent a position or trade. */
  private final BigDecimal _quantity;
  /** The node ID of the row (if it's a noderow ) or its parent node (if it's a position or trade row). */
  private final UniqueId _nodeId;
  /** The position ID of the row (if it's a position row) or its parent position (if it's a trade row). */
  private final UniqueId _positionId;
  /** The row's trade ID (if it's a trade row). */
  private final UniqueId _tradeId;

  /**
   * For rows representing portfolio nodes which have no security or quantity
   * @param target The row's target
   * @param name The row name
   */
  /* package */ PortfolioGridRow(ComputationTargetSpecification target, String name, UniqueId nodeId) {
    super(target, name);
    ArgumentChecker.notNull(nodeId, "nodeId");
    _security = null;
    _quantity = null;
    _nodeId = nodeId;
    _positionId = null;
    _tradeId = null;
  }

  /**
   * For rows representing position nodes which have a security and quantity
   * @param target The row's target
   * @param security The position's security, not null
   * @param quantity The position's quantity, not null
   */
  /* package */ PortfolioGridRow(ComputationTargetSpecification target,
                                 String name,
                                 Security security,
                                 BigDecimal quantity,
                                 UniqueId nodeId,
                                 UniqueId positionId) {
    super(target, name);
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(nodeId, "nodeId");
    ArgumentChecker.notNull(positionId, "positionId");
    _security = security;
    _quantity = quantity;
    _nodeId = nodeId;
    _positionId = positionId;
    _tradeId = null;
  }

  /**
   * For rows representing position nodes which have a security and quantity
   * @param target The row's target
   * @param security The position's security, not null
   * @param quantity The position's quantity, not null
   */
  /* package */ PortfolioGridRow(ComputationTargetSpecification target,
                                 String name,
                                 Security security,
                                 BigDecimal quantity,
                                 UniqueId nodeId,
                                 UniqueId positionId,
                                 UniqueId tradeId) {
    super(target, name);
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(nodeId, "nodeId");
    ArgumentChecker.notNull(positionId, "positionId");
    ArgumentChecker.notNull(tradeId, "tradeId");
    _security = security;
    _quantity = quantity;
    _nodeId = nodeId;
    _positionId = positionId;
    _tradeId = tradeId;
  }

  /* package */ Security getSecurity() {
    return _security;
  }

  /* package */ BigDecimal getQuantity() {
    return _quantity;
  }

  /* package */ UniqueId getNodeId() {
    return _nodeId;
  }

  /* package */ UniqueId getPositionId() {
    return _positionId;
  }

  /* package */ UniqueId getTradeId() {
    return _tradeId;
  }
}
