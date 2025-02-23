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
package org.jkiss.dbeaver.ext.oracle.model;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.impl.DBObjectNameCaseTransformer;
import org.jkiss.dbeaver.model.impl.jdbc.struct.JDBCTableConstraint;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSEntityConstraintType;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * OracleTableConstraint
 */
public abstract class OracleTableConstraintBase extends JDBCTableConstraint<OracleTableBase, OracleTableConstraintColumn> {

    private static final Log log = Log.getLog(OracleTableConstraintBase.class);

    private OracleObjectStatus status;
    private final List<OracleTableConstraintColumn> columns = new ArrayList<>();

    public OracleTableConstraintBase(OracleTableBase oracleTable, String name, DBSEntityConstraintType constraintType, OracleObjectStatus status, boolean persisted) {
        super(oracleTable, name, null, constraintType, persisted);
        this.status = status;
    }

    protected OracleTableConstraintBase(OracleTableBase oracleTableBase, String name, String description, DBSEntityConstraintType constraintType, boolean persisted) {
        super(oracleTableBase, name, description, constraintType, persisted);
    }

    @NotNull
    @Override
    public OracleDataSource getDataSource() {
        return getTable().getDataSource();
    }

    @NotNull
    @Property(viewable = true, editable = false, valueTransformer = DBObjectNameCaseTransformer.class, order = 3)
    @Override
    public DBSEntityConstraintType getConstraintType() {
        return constraintType;
    }

    @Property(viewable = true, editable = false, order = 9)
    public OracleObjectStatus getStatus() {
        return status;
    }

    @Override
    public List<OracleTableConstraintColumn> getAttributeReferences(DBRProgressMonitor monitor) {
        return columns;
    }

    @Override
    public void addAttributeReference(DBSTableColumn column) throws DBException {
        this.columns.add(new OracleTableConstraintColumn(this, (OracleTableColumn) column, columns.size()));
    }

    public void addColumn(OracleTableConstraintColumn column) {
        this.columns.add(column);
    }

    public void setAttributeReferences(List<OracleTableConstraintColumn> columns) {
        this.columns.clear();
        this.columns.addAll(columns);
    }

}
