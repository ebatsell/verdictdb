/*
 *    Copyright 2018 University of Michigan
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.verdictdb.core.sqlobject;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents the alias name that appears in the group-by clause or in the order-by clause. This
 * column does not include any reference to the table.
 *
 * @author Yongjoo Park
 */
public class AliasReference implements UnnamedColumn {

  private static final long serialVersionUID = 6273526004275442693L;

  String aliasName;

  String tableAlias;

  public AliasReference(String aliasName) {
    this.aliasName = aliasName;
  }

  public AliasReference(String tableAlias, String aliasName) {
    this.tableAlias = tableAlias;
    this.aliasName = aliasName;
  }

  public String getAliasName() {
    return aliasName;
  }

  public String getTableAlias() {
    return tableAlias;
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

  @Override
  public boolean isAggregateColumn() {
    return false;
  }

  @Override
  public UnnamedColumn deepcopy() {
    return new AliasReference(tableAlias, aliasName);
  }
}
