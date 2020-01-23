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

import org.vaadin.firitin.layouts.BorderLayout;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

/**
 *
 * @author mstahv
 */
@Route
public class BorderLayoutExample extends BorderLayout {

    public BorderLayoutExample() {
        setChildAt(Region.EAST, new Span("East"));
        setChildAt(Region.NORTH, new Span("North"));
        setChildAt(Region.WEST, new Span("West"));
        setChildAt(Region.SOUTH, new Span("South"));
        
    }
    
}
