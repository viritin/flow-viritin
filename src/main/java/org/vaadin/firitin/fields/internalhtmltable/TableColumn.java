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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;

/**
 * Represents a table column element ({@code <col>}). A column is contained in a column group and can
 * setup table columns.
 *
 * @see TableColumnGroup
 * @author Stefan Uebe
 */
@Tag("col")
public class TableColumn extends Component implements HasStyle {
    private final String ATTRIBUTE_SPAN = "span";

    /**
     * Sets the column span. That column span defines how styles of the column's css class will cover
     * related cells. It does not override the cell's colspan attribute.
     * <br><br>
     * Must be a positive integer
     * @param span span
     */
    public void setSpan(int span) {
        if (span < 0) {
            throw new IllegalArgumentException("Span must be a positive number");
        }
        getElement().setAttribute(ATTRIBUTE_SPAN, String.valueOf(span));
    }

    /**
     * Returns the span attribute for this column. Default is 1, if none has been set for this instance.
     * @return span
     */
    public int getSpan() {
        String span = getElement().getAttribute(ATTRIBUTE_SPAN);
        if (span == null) {
            span = "1";
        }
        return Integer.parseInt(span);
    }

    /**
     * Resets the span to its default (1).
     */
    public void resetSpan() {
        getElement().removeAttribute(ATTRIBUTE_SPAN);
    }
}
