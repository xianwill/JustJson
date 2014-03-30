/*
 * Copyright (C) 2014 Kalin Maldzhanski
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

package org.djodjo.json.schema.validation;


import org.djodjo.json.JsonElement;
import org.djodjo.json.schema.Schema;
import org.djodjo.json.schema.SchemaMap;
import org.djodjo.json.schema.SchemaV4;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Map;

import static org.djodjo.json.schema.validation.CommonMatchers.areItemsUnique;
import static org.djodjo.json.schema.validation.CommonMatchers.areItemsValid;
import static org.djodjo.json.schema.validation.CommonMatchers.doesItemCountMatches;
import static org.djodjo.json.schema.validation.CommonMatchers.isItemValid;
import static org.djodjo.json.schema.validation.CommonMatchers.isLessOrEqualThan;
import static org.djodjo.json.schema.validation.CommonMatchers.isLessThan;
import static org.djodjo.json.schema.validation.CommonMatchers.isMoreOrEqualThan;
import static org.djodjo.json.schema.validation.CommonMatchers.isMoreThan;
import static org.djodjo.json.schema.validation.CommonMatchers.isMultipleOf;
import static org.djodjo.json.schema.validation.CommonMatchers.isOfType;
import static org.djodjo.json.schema.validation.CommonMatchers.isPropertyPresent;
import static org.djodjo.json.schema.validation.CommonMatchers.isPropertyValid;
import static org.djodjo.json.schema.validation.CommonMatchers.matchesPattern;
import static org.djodjo.json.schema.validation.CommonMatchers.maxItems;
import static org.djodjo.json.schema.validation.CommonMatchers.minItems;
import static org.djodjo.json.schema.validation.CommonMatchers.maxProperties;
import static org.djodjo.json.schema.validation.CommonMatchers.minProperties;
import static org.djodjo.json.schema.validation.CommonMatchers.withCharsLessOrEqualTo;
import static org.djodjo.json.schema.validation.CommonMatchers.withCharsMoreOrEqualTo;
import static org.hamcrest.Matchers.allOf;


public class SchemaV4Validator extends SchemaValidator<SchemaV4> {

    ArrayList<Matcher<? super JsonElement>> allMatchers =  new ArrayList<Matcher<? super JsonElement>>();
    public SchemaV4Validator(SchemaV4 schema) {
        super(schema);
        //allMatchers
        putMatchers4Numeric();
        putMatchers4String();
        putMatchers4Array();
        putMatcher4Object();
        putMatchers4Common();

    }


    private void putMatchers4Numeric() {
        double multipleOf = schema.getMultipleOf();
        if(multipleOf != Double.MIN_VALUE && multipleOf>0) {
            allMatchers.add(isMultipleOf(multipleOf));
        }

        double maximum = schema.getMaximum();
        if(maximum != Double.MAX_VALUE) {
            if(schema.getExclusiveMaximum()) {
                allMatchers.add(isLessThan(maximum));
            } else {
                allMatchers.add(isLessOrEqualThan(maximum));
            }
        }

        double minimum = schema.getMinimum();
        if(minimum != Double.MIN_VALUE) {
            if(schema.getExclusiveMinimum()) {
                allMatchers.add(isMoreThan(minimum));
            } else {
                allMatchers.add(isMoreOrEqualThan(minimum));
            }
        }

    }
    private void putMatchers4String() {

        int maxLength = schema.getMaxLength();
        if(maxLength != Integer.MAX_VALUE) {
            allMatchers.add(withCharsLessOrEqualTo(maxLength));
        }

        int minLength = schema.getMinLength();
        if(minLength > 0) {
            allMatchers.add(withCharsMoreOrEqualTo(minLength));
        }

        String pattern = schema.getPattern();
        if(pattern != null && !pattern.isEmpty()) {
            allMatchers.add(matchesPattern(pattern));
        }



    }
    private void putMatchers4Array() {

        ArrayList<Schema>  items = schema.getItems();
        if(items != null && !items.isEmpty()) {
            if(items.size() == 1) {
                //single type for array items
                allMatchers.add(areItemsValid(items.get(0).getDefaultValidator()));
            } else {
                //tuple typing
                //first check if schemas needs to be the same as the instance items
                if(!schema.getAdditionalItems()) {
                    allMatchers.add(doesItemCountMatches(items.size()));
                }

                //then check if available items are according to provided schemas
                for(int i=0;i<items.size();i++) {
                    allMatchers.add(isItemValid(items.get(i).getDefaultValidator(), i));
                }

            }
        }

        int maxItems = schema.getMaxItems();
        if(maxItems < Integer.MAX_VALUE) {
            allMatchers.add(maxItems(maxItems));
        }

        int minItems = schema.getMinItems();
        if(minItems > 0) {
            allMatchers.add(minItems(minItems));
        }

        if(schema.getUniqueItems()) {
            allMatchers.add(areItemsUnique());
        }


    }

    private void putMatcher4Object() {

        int maxProperties = schema.getMaxProperties();
        if(maxProperties < Integer.MAX_VALUE) {
            allMatchers.add(maxProperties(maxProperties));
        }

        int minProperties = schema.getMinProperties();
        if(minProperties > 0) {
            allMatchers.add(minProperties(minProperties));
        }

        ArrayList<String> required = schema.getRequired();
        if(required != null && !required.isEmpty()) {
            for(String param : required) {
                allMatchers.add(isPropertyPresent(param));
            }
        }

        SchemaMap schemaMap = schema.getProperties();
        if(schemaMap != null && schemaMap.length() > 0) {
            for(Map.Entry<String, Schema> entry : schemaMap) {
                allMatchers.add(isPropertyValid(entry.getValue().getDefaultValidator(), entry.getKey()));
            }
        }

        //TODO as per : http://tools.ietf.org/html/draft-fge-json-schema-validation-00#section-5.4.4.4

        //TODO as per : http://tools.ietf.org/html/draft-fge-json-schema-validation-00#section-5.4.5

    }

    private void putMatchers4Common() {

        ArrayList<String> schemaType = schema.getType();
        if(schemaType != null && !schemaType.isEmpty()) {
            allMatchers.add(isOfType(schemaType));
        }
    }

    @Override
    protected boolean doValidate(JsonElement el, StringBuilder sb) {
        //check if empty schema
        if(allMatchers == null || allMatchers.isEmpty())
            return true;

        Matcher<JsonElement> matcher = allOf(allMatchers);

        return matcher.matches(el);
    }
}