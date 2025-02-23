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
package org.jkiss.dbeaver.ext.hana.edit;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.ext.generic.edit.GenericTableColumnManager;
import org.jkiss.dbeaver.ext.generic.model.GenericTableBase;
import org.jkiss.dbeaver.ext.generic.model.GenericTableColumn;
import org.jkiss.dbeaver.model.impl.edit.DBECommandAbstract;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;

import java.util.Map;

/**
 * HANATableColumnManager
 */
public class HANATableColumnManager extends GenericTableColumnManager {

    @Override
    public StringBuilder getNestedDeclaration(
        @NotNull DBRProgressMonitor monitor,
        @NotNull GenericTableBase owner,
        @NotNull DBECommandAbstract<GenericTableColumn> command,
        @NotNull Map<String, Object> options
    ) {
        StringBuilder decl = super.getNestedDeclaration(monitor, owner, command, options);
        if (owner.isPersisted()) {
            decl.insert(0, "(");
            decl.append(")");
        }
        return decl;
    }

}
