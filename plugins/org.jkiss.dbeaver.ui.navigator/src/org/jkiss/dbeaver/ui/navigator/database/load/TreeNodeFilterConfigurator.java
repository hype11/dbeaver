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
package org.jkiss.dbeaver.ui.navigator.database.load;

import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.swt.graphics.Image;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.navigator.DBNNode;
import org.jkiss.dbeaver.ui.internal.UINavigatorMessages;
import org.jkiss.dbeaver.ui.navigator.actions.NavigatorHandlerFilterConfig;
import org.jkiss.dbeaver.ui.navigator.database.DatabaseNavigatorTree;

/**
 * A special node that is shown when a filter is applied to a parent folder node.
 */
public class TreeNodeFilterConfigurator extends TreeNodeSpecial implements IToolTipProvider {
    public TreeNodeFilterConfigurator(@NotNull DBNNode parent) {
        super(parent);
    }

    @Override
    public String getText(Object element) {
        return UINavigatorMessages.navigator_filtered_nodes_text;
    }

    @Override
    public String getToolTipText(Object element) {
        return UINavigatorMessages.navigator_filtered_nodes_tip;
    }

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public boolean handleDefaultAction(DatabaseNavigatorTree tree) {
        NavigatorHandlerFilterConfig.configureFilters(tree.getShell(), getParent());
        return true;
    }
}
