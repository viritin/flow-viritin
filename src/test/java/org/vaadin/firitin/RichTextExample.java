/*
 * Copyright 2022 Viritin.
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

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.RandomStringUtils;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

/**
 * @author mstahv
 */
@Route
public class RichTextExample extends VVerticalLayout {

    public RichTextExample() {

        add(new H1("RichText examples"));

        // even though adding a large content, RichText don't consume memory
        // in the user session
        add(new RichText(RandomStringUtils.randomAlphabetic(100000)));

        // for this you could call getText and get meaningful return value,
        // but this consumes 100kB of (uncompressed) memory, while users
        // session is active
        add(new RichText().setRichTextAndSaveReference(RandomStringUtils.randomAlphabetic(100000)));

    }

}
