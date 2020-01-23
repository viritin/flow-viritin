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

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.vaadin.firitin.testdomain.Dude;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Route;

/**
 *
 * @author mstahv
 */
@Route
public class CrossFieldValidationPlayground extends VerticalLayout {
    
    private TextField email = new TextField("email");
    private TextField emailConfirm = new TextField("confirm email");

    public CrossFieldValidationPlayground() {
        add(email, emailConfirm);
        
        Dude d = new Dude(0, "Jorma", "Jormala", 0);
        
        BeanValidationBinder<Dude> b = new BeanValidationBinder<>(Dude.class);
        b.bindInstanceFields(this);
        
        b.setBean(d);
        
        email.setValue("jorma@foo.bar");
        emailConfirm.setValue("jorma@foo.fi");
        
        boolean binderIsValid = b.isValid();
        
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        Set<ConstraintViolation<Dude>> violations = validator.validate(d);
        
        Notification.show(String.format("Binder says valid == %s, JSR3030 has %s issues", binderIsValid, violations.size()));
        
        System.out.println(violations);
        
    }
    
    
    
}
