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
package org.jkiss.dbeaver.ext.oracle.ui.config;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ext.oracle.model.OracleTableColumn;
import org.jkiss.dbeaver.ext.oracle.model.OracleTableConstraint;
import org.jkiss.dbeaver.ext.oracle.model.OracleTableConstraintColumn;
import org.jkiss.dbeaver.ext.oracle.ui.internal.OracleUIMessages;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.edit.DBEObjectConfigurator;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.ui.UITask;
import org.jkiss.dbeaver.ui.editors.object.struct.EditConstraintPage;

import java.util.Map;

/**
 * OracleConstraintConfigurator
 */
public class OracleConstraintConfigurator implements DBEObjectConfigurator<OracleTableConstraint> {


    @Override
    public OracleTableConstraint configureObject(@NotNull DBRProgressMonitor monitor, @Nullable DBECommandContext commandContext, @Nullable Object parent, @NotNull OracleTableConstraint constraint, @NotNull Map<String, Object> options) {
        return UITask.run(() -> {
            EditConstraintPage editPage = new EditConstraintPage(
                OracleUIMessages.edit_oracle_constraint_manager_dialog_title,
                constraint);
            if (!editPage.edit()) {
                return null;
            }
            constraint.setName(editPage.getConstraintName());
            constraint.setConstraintType(editPage.getConstraintType());
            constraint.setSearchCondition(editPage.getConstraintExpression());

            int colIndex = 1;
            for (DBSEntityAttribute tableColumn : editPage.getSelectedAttributes()) {
                constraint.addColumn(
                    new OracleTableConstraintColumn(
                        constraint,
                        (OracleTableColumn) tableColumn,
                        colIndex++));
            }

            return constraint;
        });
    }

}
