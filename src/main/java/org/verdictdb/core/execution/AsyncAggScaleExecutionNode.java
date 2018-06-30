package org.verdictdb.core.execution;

import jdk.vm.ci.meta.Constant;
import org.apache.commons.lang3.tuple.Pair;
import org.verdictdb.connection.DbmsConnection;
import org.verdictdb.core.execution.ola.AsyncAggExecutionNode;
import org.verdictdb.core.execution.ola.HyperTableCube;
import org.verdictdb.core.query.*;
import org.verdictdb.core.rewriter.ScrambleMeta;
import org.verdictdb.exception.VerdictDBException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsyncAggScaleExecutionNode extends ProjectionExecutionNode {

  // Default value. Will be modified when executeNode() is called.
  double scaleFactor = 1.0;
  List<UnnamedColumn> aggColumnlist = new ArrayList<>();

  protected AsyncAggScaleExecutionNode(QueryExecutionPlan plan) {
    super(plan);
  }

  public static AsyncAggScaleExecutionNode create(QueryExecutionPlan plan, AsyncAggExecutionNode asyncNode) {
    AsyncAggScaleExecutionNode node = new AsyncAggScaleExecutionNode(plan);

    // Setup select list
    List<SelectItem> newSelectList = asyncNode.getSelectQuery().deepcopy().getSelectList();
    for (SelectItem selectItem:newSelectList) {
      // invariant: the agg column must be aliased column
      if (selectItem instanceof AliasedColumn) {
        int index = newSelectList.indexOf(selectItem);
        UnnamedColumn col = ((AliasedColumn) selectItem).getColumn();
        if (AsyncAggScaleExecutionNode.isAggregateColumn(col)) {
          UnnamedColumn aggColumn = new ColumnOp("multiply", Arrays.<UnnamedColumn>asList(
              ConstantColumn.valueOf(node.scaleFactor), col
          ));
          node.aggColumnlist.add(aggColumn);
          newSelectList.set(index, new AliasedColumn(aggColumn, ((AliasedColumn) selectItem).getAliasName()));
        }
      }
    }
    // Setup from table
    Pair<BaseTable, ExecutionTokenQueue> baseAndQueue = node.createPlaceHolderTable("to_scale_query");
    SelectQuery query = SelectQuery.create(newSelectList, baseAndQueue.getLeft());
    node.setSelectQuery(query);

    // Set this node to broadcast to the parents of asyncNode
    // Also remove the dependency
    for (QueryExecutionNode parent:asyncNode.getParents()) {
      int index = parent.dependents.indexOf(asyncNode);
      ExecutionTokenQueue queue = new ExecutionTokenQueue();
      parent.getListeningQueues().set(index, queue);
      node.addBroadcastingQueue(queue);
      parent.dependents.set(index, node);
    }

    // Set the asyncNode only to broadcast to this node
    // Also set parent
    asyncNode.getBroadcastingQueues().clear();
    asyncNode.addBroadcastingQueue(baseAndQueue.getRight());
    asyncNode.getParents().clear();
    asyncNode.getParents().add(node);

    return node;
  }

  // Currently, only need to judge whether it is sum or count
  public static boolean isAggregateColumn(UnnamedColumn sel) {
    List<SelectItem> itemToCheck = new ArrayList<>();
    itemToCheck.add(sel);
    while (!itemToCheck.isEmpty()) {
      SelectItem s = itemToCheck.get(0);
      itemToCheck.remove(0);
      if (s instanceof ColumnOp) {
        if (((ColumnOp) s).getOpType().equals("count") || ((ColumnOp) s).getOpType().equals("sum")) {
          return true;
        }
        else itemToCheck.addAll(((ColumnOp) s).getOperands());
      }
    }
    return false;
  }

  @Override
  public ExecutionInfoToken executeNode(DbmsConnection conn, List<ExecutionInfoToken> downstreamResults)
      throws VerdictDBException {
    ExecutionInfoToken token = super.executeNode(conn, downstreamResults);
    // Calculate the scale factor
    for (ExecutionInfoToken downstreamResult:downstreamResults) {
      List<HyperTableCube> cubes = (List<HyperTableCube>) downstreamResult.getValue("hyperTableCube");
      if (cubes != null) {

      }
    }
    // Substitute the scale factor
    return token;
  }

  @Override
  public QueryExecutionNode deepcopy() {
    AsyncAggScaleExecutionNode node = new AsyncAggScaleExecutionNode(plan);
    copyFields(this, node);
    node.scaleFactor = scaleFactor;
    node.aggColumnlist = aggColumnlist;
    return node;
  }

  public double calculateScaleFactor(List<HyperTableCube> cubes) {
    double executedRatio = 0;
    ScrambleMeta scrambleMeta = ((AsyncAggExecutionNode)(this.dependents.get(0))).getScrambleMeta();
    scrambleMeta.
    for (HyperTableCube cube:cubes) {

    }
  }
}
