/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.mssql.model;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPEvaluationContext;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCTableConstraint;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSEntityAttributeRef;
import org.jkiss.dbeaver.model.struct.DBSEntityConstraint;
import org.jkiss.dbeaver.model.struct.DBSEntityConstraintType;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableConstraintColumn;
import org.jkiss.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLServerTableUniqueKey
 */
public class SQLServerTableUniqueKey extends JDBCTableConstraint<SQLServerTableBase, DBSTableConstraintColumn> {
    private final SQLServerTableIndex index;
    private final List<SQLServerTableUniqueKeyColumn> columns = new ArrayList<>();

    public SQLServerTableUniqueKey(SQLServerTableBase table, String name, String remarks, DBSEntityConstraintType constraintType, SQLServerTableIndex index, boolean persisted) {
        super(table, name, remarks, constraintType, persisted);
        this.index = index;
    }

    // Copy constructor
    protected SQLServerTableUniqueKey(DBRProgressMonitor monitor, SQLServerTableBase table, DBSEntityConstraint source) throws DBException {
        super(table, source, false);
        this.index = table.getIndex(monitor, source.getName());
    }

    @Property(viewable = true, order = 10)
    public SQLServerTableIndex getIndex() {
        return index;
    }

    @Override
    public List<DBSTableConstraintColumn> getAttributeReferences(DBRProgressMonitor monitor) {
        if (!CommonUtils.isEmpty(columns)) {
            return new ArrayList<>(columns);
        }
        if (index == null) {
            return null;
        }
        List<SQLServerTableIndexColumn> indexAttrs = index.getAttributeReferences(monitor);
        return indexAttrs == null ? null : new ArrayList<>(indexAttrs);
    }

    @Override
    public void addAttributeReference(DBSTableColumn column) throws DBException {
        this.columns.add(new SQLServerTableUniqueKeyColumn(this, (SQLServerTableColumn) column, columns.size()));
    }

    public void setAttributeReferences(List<DBSTableConstraintColumn> columns) {
        this.columns.clear();
        for (DBSEntityAttributeRef ar : columns) {
            this.columns.add((SQLServerTableUniqueKeyColumn)ar);
        }
    }

    @NotNull
    @Override
    public String getFullyQualifiedName(DBPEvaluationContext context) {
        return DBUtils.getFullQualifiedName(getDataSource(),
            getTable().getDatabase(),
            getTable().getSchema(),
            getTable(),
            this);
    }

    @NotNull
    @Override
    public SQLServerDataSource getDataSource() {
        return getTable().getDataSource();
    }

    public void addColumn(SQLServerTableUniqueKeyColumn column) {
        this.columns.add(column);
    }


}
