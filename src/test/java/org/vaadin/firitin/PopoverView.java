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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import com.vaadin.flow.component.popover.PopoverVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.popover.PopoverButton;
import org.vaadin.firitin.components.progressbar.VProgressBar;
import org.vaadin.firitin.layouts.HorizontalFloatLayout;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

/**
 *
 * @author mstahv
 */
@Route
public class PopoverView extends VerticalLayout {

    public static class PersonGrid extends VGrid<Person> {
        public PersonGrid() {
            // The headers "First Name", "Last Name" and "Age" and the sorting come
            // from the getter references, no need to spell them out
            addColumn(Person::getFirstName);
            addColumn(Person::getLastName);
            addColumn(Person::getAge);
            addComponentColumn(person -> {
                return new VHorizontalLayout(
                        new Button(VaadinIcon.EDIT.create()),
                        new DeleteButton(),
                        new OtherActions(person)
                );
            }).setHeader("Actions");
            setItems(Service.getListOfPersons(1000));
        }

        public class OtherActions extends PopoverButton {
            public OtherActions(Person person) {
                super(() -> new VVerticalLayout(
                        new ActionButton("Merge.."),
                        new ActionButton("Send email"),
                        new ActionButton("Call"),
                        new ActionButton("SMS"),
                        new QuickAgeFix(person),
                        new RichText().withMarkDown("""
                        Popover is essentially a dialog that can contain anything, 
                        but it is "bound to" another component. It is positioned next
                        to the "target" component and often the relation is emphasized
                        with *addThemeVariants(PopoverVariant.ARROW)*. Popover can be 
                        for example a handy way to expose less often used functions.
                        
                        Note, that Popover expands Vertically as much as needed. Thus,
                        if you have a ton of content, like here,  you'll have to limit the size
                        to less half of the screen height (take the target component height into 
                        account!!). Here setHeight("45vh") is used, but optimally [Popover would
                        implicitly reduce the height](https://github.com/vaadin/web-components/issues/8016) 
                        so that content can be accessed.
                        
                        Also note that bad patterns using Popover in the code can ruin
                        the performance. E.g. If this popover would use moderate 50k (it doesen't easily could),
                        the view would suddenly waste 2.5 MB per user. This can be overcome by dynamically 
                        creating the content, only when the popover is displayed.
                        
                        """)));
                // The popover can be preconfigured via getter, instance is then created already
                // but the actual content (commonly the heavy part) is still postponed to UI interraction
                // If you want to avoid creation of the popover instance beforehand, do these in click listener instead
                addClickListener(() -> {
                    getPopover().setWidth("30vw");
                    getPopover().setHeight("45vh"); // optimally Popover would have implicit max height
                });
            }

            public static class ActionButton extends Button {
                public ActionButton(String text) {
                    setText(text);
                    addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                }

                @Override
                protected void onAttach(AttachEvent attachEvent) {
                    super.onAttach(attachEvent);
                    addClickListener(event -> {
                        Notification.show("Not implemented");
                        findAncestor(Popover.class).close();
                    });

                }
            }
        }
        public class QuickAgeFix extends HorizontalFloatLayout {
            public QuickAgeFix(Person person) {
                IntegerField age = new IntegerField();
                age.setLabel("Set new age");
                age.setValue(person.getAge());
                var apply = new Button(VaadinIcon.PLAY.create(), e-> {
                    person.setAge(age.getValue());
                    getGenericDataView().refreshAll();
                });
                add(age, apply);
            }
        }
    }



    public PopoverView() {

        addAndExpand(new PersonGrid());

    }
}
