/*
 * Copyright 2021 by Stefan Uebe
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
package org.vaadin.firitin.fields.internalhtmltable;

import java.util.Arrays;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;

/**
 * @author Stefan Uebe
 */
public final class ElementHelper {
    private ElementHelper() {
        // utility class
    }

    public static Element[] asElements(Component... components) {
        return Arrays.stream(components).map(Component::getElement).toArray(Element[]::new);
    }
}
