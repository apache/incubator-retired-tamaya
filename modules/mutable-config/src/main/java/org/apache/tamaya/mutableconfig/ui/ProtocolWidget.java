/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.mutableconfig.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.services.MessageProvider;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Widget showing a text protocol wioth a clear button to clear the widget space.
 */
public class ProtocolWidget extends VerticalLayout{

    private TextArea textArea = new TextArea(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.edit.textArea.protocol"));
    private Button clearButton = new Button(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("view.edit.button.clearProtocol"));

    private StringWriter protocol = new StringWriter();
    private PrintWriter writer = new PrintWriter(protocol);

    public ProtocolWidget(){
        textArea.setWidth(100, Unit.PERCENTAGE);
        textArea.setReadOnly(true);
        clearButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                protocol.getBuffer().setLength(0);
                flush();
            }
        });
        textArea.setSizeFull();
        addComponents(textArea, clearButton);
    }

    public PrintWriter getWriter(){
        return writer;
    }

    public void println(){
        writer.println();
    }

    public void println(Object... items){
        for(int i=0;i<items.length;i++){
            writer.print(items[i]);
        }
        writer.println();
        flush();
    }

    public void print(Object... items){
        for(int i=0;i<items.length;i++){
            writer.print(items[i]);
        }
        flush();
    }

    private void flush(){
        writer.flush();
        textArea.setReadOnly(false);
        textArea.setValue(protocol.toString());
        textArea.setReadOnly(true);
    }

}
