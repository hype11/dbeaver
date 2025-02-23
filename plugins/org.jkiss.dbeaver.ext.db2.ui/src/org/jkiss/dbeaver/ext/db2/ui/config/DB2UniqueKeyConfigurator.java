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
package org.jkiss.dbeaver.ext.db2.ui.config;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ext.db2.model.DB2TableColumn;
import org.jkiss.dbeaver.ext.db2.model.DB2TableKeyColumn;
import org.jkiss.dbeaver.ext.db2.model.DB2TableUniqueKey;
import org.jkiss.dbeaver.ext.db2.ui.internal.DB2Messages;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.edit.DBEObjectConfigurator;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.ui.UITask;
import org.jkiss.dbeaver.ui.editors.object.struct.EditConstraintPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DB2 unique constraint configurator
 */
public class DB2UniqueKeyConfigurator implements DBEObjectConfigurator<DB2TableUniqueKey> {
	
    @Override
    public DB2TableUniqueKey configureObject(@NotNull DBRProgressMonitor monitor, @Nullable DBECommandContext commandContext, @Nullable Object table, @NotNull DB2TableUniqueKey constraint, @NotNull Map<String, Object> options) {
    	return new UITask<DB2TableUniqueKey>() {
            @Override
            protected DB2TableUniqueKey runTask()
            {
                EditConstraintPage editPage = new EditConstraintPage(DB2Messages.edit_db2_constraint_manager_dialog_title, constraint);

                if (!editPage.edit()) {
                    return null;
                }

                constraint.setConstraintType(editPage.getConstraintType());
                constraint.setName(editPage.getConstraintName());

                List<DB2TableKeyColumn> columns = new ArrayList<>(editPage.getSelectedAttributes().size());
                DB2TableKeyColumn column;
                int colIndex = 1;
                for (DBSEntityAttribute tableColumn : editPage.getSelectedAttributes()) {
                    column = new DB2TableKeyColumn(constraint, (DB2TableColumn) tableColumn, colIndex++);
                    columns.add(column);
                }
                constraint.setAttributeReferences(columns);

                return constraint;
            }
        }.execute();
    }

}
