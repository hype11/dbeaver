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
package org.jkiss.dbeaver.model.impl.struct;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPNamedObject2;
import org.jkiss.dbeaver.model.exec.DBCFeatureNotSupportedException;
import org.jkiss.dbeaver.model.meta.Property;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSEntityConstraint;
import org.jkiss.dbeaver.model.struct.DBSEntityConstraintType;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableConstraint;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableConstraintColumn;

import java.util.List;

/**
 * GenericConstraint
 */
public abstract class AbstractTableConstraint<TABLE extends DBSTable, CON_COLUMN extends DBSTableConstraintColumn>
    implements DBSTableConstraint, DBPNamedObject2
{
    private final TABLE table;
    private String name;
    protected String description;
    protected DBSEntityConstraintType constraintType;

    protected AbstractTableConstraint(TABLE table, String name, String description, DBSEntityConstraintType constraintType) {
        this.table = table;
        this.name = name;
        this.description = description;
        this.constraintType = constraintType;
    }

    // Copy constructor
    protected AbstractTableConstraint(TABLE table, DBSEntityConstraint source) {
        this.table = table;
        this.name = source.getName();
        this.description = source.getDescription();
        this.constraintType = source.getConstraintType();
    }

    @Property(id = "owner", viewable = true, order = 2)
    public TABLE getTable() {
        return table;
    }

    @NotNull
    @Override
    @Property(viewable = true, order = 1)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //    @Property(name = "Description", viewable = true, order = 100)
    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @Override
    @Property(viewable = true, order = 3)
    public DBSEntityConstraintType getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(DBSEntityConstraintType constraintType) {
        this.constraintType = constraintType;
    }

    @Nullable
    @Override
    public abstract List<CON_COLUMN> getAttributeReferences(DBRProgressMonitor monitor) throws DBException;

    public void addAttributeReference(DBSTableColumn column) throws DBException {
        throw new DBCFeatureNotSupportedException("Attribute add is not supported for " + getClass().getSimpleName());
    }

    public abstract void setAttributeReferences(List<CON_COLUMN> columns) throws DBException;

    @NotNull
    @Override
    public TABLE getParentObject() {
        return table;
    }

    @Override
    public boolean isPersisted() {
        return true;
    }

    public String toString() {
        return getName() == null ? "<NONE>" : getName();
    }

}
