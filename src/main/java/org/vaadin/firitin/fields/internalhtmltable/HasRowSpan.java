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

import com.vaadin.flow.component.HasElement;

/**
 * A mixing interface for components, that have a "rowspan" attribute.
 * @author Stefan Uebe
 */
public interface HasRowSpan extends HasElement {
    String ATTRIBUTE_ROWSPAN = "rowspan";

    /**
     * Set a rowspan for this instance. Must be a non negative integer (further checks are delegated to the browser).
     * @param rowSpan row span
     */
    default void setRowSpan(int rowSpan) {
        if (rowSpan < 0) {
            throw new IllegalArgumentException("Rowspan must be a positive number");
        }

        getElement().setAttribute(ATTRIBUTE_ROWSPAN, String.valueOf(rowSpan));
    }

    /**
     * Returns the rowspan set for this instance. Defaults to 1 if none has been set before.
     * @return rowspan
     */
    default int getRowSpan() {
        String rowspan = getElement().getAttribute(ATTRIBUTE_ROWSPAN);
        if (rowspan == null) {
            rowspan = "1";
        }
        return Integer.parseInt(rowspan);
    }

    /**
     * Resets the rowspan to its default (1).
     */
    default void resetRowSpan() {
        getElement().removeAttribute(ATTRIBUTE_ROWSPAN);
    }
}
