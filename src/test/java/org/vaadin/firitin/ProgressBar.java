/*
 * Copyright 2019 Viritin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.progressbar.VProgressBar;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

import java.util.List;

/**
 *
 * @author mstahv
 */
@Route
public class ProgressBar extends VerticalLayout {

    public ProgressBar() {
        add(new H1("This is slow view..."));

        runSlowUpdate();

    }

    private void runSlowUpdate() {
        add(VProgressBar.indeterminateForTask(() -> {
            // slow task, but at least the UI shows it is not a bug
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            add("Done");
            add(new Button("Run again", e -> {runSlowUpdate(); e.getSource().removeFromParent();}));
        }));
    }

}
