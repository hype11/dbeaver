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

package org.jkiss.dbeaver.ext.mysql.ui.config;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ext.mysql.model.MySQLDataSource;
import org.jkiss.dbeaver.ext.mysql.model.MySQLTableColumn;
import org.jkiss.dbeaver.ext.mysql.model.MySQLTableConstraint;
import org.jkiss.dbeaver.ext.mysql.model.MySQLTableConstraintColumn;
import org.jkiss.dbeaver.ext.mysql.ui.internal.MySQLUIMessages;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.edit.DBEObjectConfigurator;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.DBSEntityConstraintType;
import org.jkiss.dbeaver.ui.UITask;
import org.jkiss.dbeaver.ui.editors.object.struct.EditConstraintPage;

import java.util.Map;

/**
 * MySQL constraint configurator
 */
public class MySQLConstraintConfigurator implements DBEObjectConfigurator<MySQLTableConstraint> {


    @Override
    public MySQLTableConstraint configureObject(@NotNull DBRProgressMonitor monitor, @Nullable DBECommandContext commandContext, @Nullable Object parent, @NotNull MySQLTableConstraint constraint, @NotNull Map<String, Object> options) {
        MySQLDataSource dataSource = constraint.getDataSource();
        return UITask.run(() -> {
            EditConstraintPage editPage = new EditConstraintPage(
                MySQLUIMessages.edit_constraint_manager_title,
                constraint);
            if (!editPage.edit()) {
                return null;
            }

            constraint.setName(editPage.getConstraintName());
            constraint.setConstraintType(editPage.getConstraintType());
            if (editPage.getConstraintType() == DBSEntityConstraintType.CHECK && dataSource.supportsCheckConstraints()) {
                constraint.setCheckClause(editPage.getConstraintExpression());
            } else {
            int colIndex = 1;
            for (DBSEntityAttribute tableColumn : editPage.getSelectedAttributes()) {
                constraint.addColumn(
                        new MySQLTableConstraintColumn(
                                constraint,
                                (MySQLTableColumn) tableColumn,
                                colIndex++));
                }
            }
            return constraint;
        });
    }
}
