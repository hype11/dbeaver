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
package org.jkiss.dbeaver.data.office.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.dbeaver.data.office.export.DataExporterXLSX;
import org.jkiss.dbeaver.model.data.DBDDataFilter;
import org.jkiss.dbeaver.model.messages.ModelMessages;
import org.jkiss.dbeaver.model.runtime.AbstractJob;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.tools.transfer.IDataTransferConsumer;
import org.jkiss.dbeaver.tools.transfer.database.DatabaseProducerSettings;
import org.jkiss.dbeaver.tools.transfer.database.DatabaseTransferProducer;
import org.jkiss.dbeaver.tools.transfer.stream.StreamConsumerSettings;
import org.jkiss.dbeaver.tools.transfer.stream.StreamTransferConsumer;
import org.jkiss.dbeaver.tools.transfer.stream.exporter.StreamExporterAbstract;
import org.jkiss.dbeaver.ui.ShellUtils;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.resultset.*;
import org.jkiss.dbeaver.ui.controls.resultset.handler.ResultSetHandlerMain;
import org.jkiss.utils.CommonUtils;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Deprecated
public class OpenSpreadsheetHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event)
    {
    	IResultSetController resultSet = ResultSetHandlerMain.getActiveResultSet(HandlerUtil.getActivePart(event));
        if (resultSet == null) {
            DBWorkbench.getPlatformUI().showError("Open Excel", "No active results viewer");
            return null;
        }


        ResultSetDataContainerOptions options = new ResultSetDataContainerOptions();

        IResultSetSelection rsSelection = resultSet.getSelection();
        List<ResultSetRow> rsSelectedRows = rsSelection.getSelectedRows();
        if (rsSelectedRows.size() > 1) {
            List<Integer> selectedRows = new ArrayList<>();
            for (ResultSetRow selectedRow : rsSelectedRows) {
                selectedRows.add(selectedRow.getRowNumber());
            }

            options.setSelectedRows(selectedRows);
            options.setSelectedColumns(rsSelection.getSelectedAttributes());
        }
        ResultSetDataContainer dataContainer = new ResultSetDataContainer(resultSet, options);
        if (dataContainer.getDataSource() == null) {
            DBWorkbench.getPlatformUI().showError("Open Excel", ModelMessages.error_not_connected_to_database);
            return null;
        }


        AbstractJob exportJob = new AbstractJob("Open Excel") {

            {
                setUser(true);
                setSystem(false);
            }

            @Override
            protected IStatus run(DBRProgressMonitor monitor) {
                try {
                    Path tempDir = DBWorkbench.getPlatform().getTempFolder(monitor, "office-files");
                    Path tempFile = tempDir.resolve(
                        CommonUtils.escapeFileName(CommonUtils.truncateString(dataContainer.getName(), 32)) +
                            "." + new SimpleDateFormat("yyyyMMdd-HHmmss").format(System.currentTimeMillis()) + ".xlsx");
                    tempFile.toFile().deleteOnExit();

                    StreamExporterAbstract exporter = new DataExporterXLSX();

                    StreamTransferConsumer consumer = new StreamTransferConsumer();
                    StreamConsumerSettings settings = new StreamConsumerSettings();

                    settings.setOutputEncodingBOM(false);
                    settings.setOutputFolder(tempDir.toAbsolutePath().toString());
                    settings.setOutputFilePattern(tempFile.getFileName().toString());

                    Map<String, Object> properties = DataExporterXLSX.getDefaultProperties();
                    consumer.initTransfer(
                        dataContainer,
                        settings,
                        new IDataTransferConsumer.TransferParameters(true, false),
                        exporter,
                        properties,
                        null);

                    DBDDataFilter dataFilter = resultSet.getModel().getDataFilter();
                    DatabaseTransferProducer producer = new DatabaseTransferProducer(dataContainer, dataFilter);
                    DatabaseProducerSettings producerSettings = new DatabaseProducerSettings();
                    producerSettings.setExtractType(DatabaseProducerSettings.ExtractType.SINGLE_QUERY);
                    producerSettings.setQueryRowCount(false);
                    producerSettings.setSelectedRowsOnly(true);
                    producerSettings.setSelectedColumnsOnly(true);

                    producer.transferData(monitor, consumer, null, producerSettings, null);

                    consumer.finishTransfer(monitor, false);

                    UIUtils.asyncExec(() -> {
                        if (!ShellUtils.launchProgram(tempFile.toAbsolutePath().toString())) {
                            DBWorkbench.getPlatformUI().showError("Open XLSX", "Can't open XLSX file '" + tempFile.toAbsolutePath() + "'");
                        }
                    });
                } catch (Exception e) {
                    DBWorkbench.getPlatformUI().showError("Error opening in Excel", null, e);
                }
                return Status.OK_STATUS;
            }
        };
        exportJob.schedule();

        return null;
    }

}
