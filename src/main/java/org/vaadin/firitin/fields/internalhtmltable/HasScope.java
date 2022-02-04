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
 * A mixing interface for components, that have a "scope" attribute.
 * @author Stefan Uebe
 */
public interface HasScope extends HasElement {
    String ATTRIBUTE_SCOPE = "scope";

    /**
     * Sets the scope attribute for this instance. Any compliance checks are delegated to the browser.
     * @param scope scope
     */
    default void setScope(String scope) {
        getElement().setAttribute(ATTRIBUTE_SCOPE, scope);
    }

    /**
     * Returns the scope attribute of this instance. The value is null, when no attribute has been set for
     * this instance, which means, that on the client side it depends on the actual table structure.
     * @return scope
     */
    default String getScope() {
        return getElement().getAttribute(ATTRIBUTE_SCOPE);
    }

    /**
     * Resets the scope to its default. The default value on the server side is null; on the client side it depends on
     * the actual table structure.
     */
    default void resetScope() {
        getElement().removeAttribute(ATTRIBUTE_SCOPE);
    }
}
