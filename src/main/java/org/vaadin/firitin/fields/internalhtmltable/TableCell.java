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

import com.vaadin.flow.component.HtmlContainer;

/**
 * Abstract base class for a table cell, that is contained in table rows. A table cell can have a colspan and
 * a rowspan.
 * @author Stefan Uebe
 */
public abstract class TableCell extends HtmlContainer implements HasColSpan, HasRowSpan {

}
