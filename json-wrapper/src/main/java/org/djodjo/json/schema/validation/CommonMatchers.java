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
import org.djodjo.json.Validator;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;


public class CommonMatchers {

    private CommonMatchers(){}



    // ==> STRING ==>
    public static Matcher<JsonElement> withCharsLessOrEqualTo(final int value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {

            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not String
                if(!item.isString()) return true;
                if(item.asString().length()>value) {
                    mismatchDescription.appendText("String length more than maximum value: " + value);
                    return false;
                }
                return true;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("String maximum length");
            }
        };
    }

    public static Matcher<JsonElement> withCharsMoreOrEqualTo(final int value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {

            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not String
                if(!item.isString()) return true;
                if(item.asString().length()<value) {
                    mismatchDescription.appendText("String length less than minimum value: " + value);
                    return false;
                }
                return true;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("String minimum length");
            }
        };
    }

    public static Matcher<JsonElement> matchesPattern(final String value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {

            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not String
                if(!item.isString()) return true;
                if(!Pattern.matches(value,item.asString())) {
                    mismatchDescription.appendText("Pattern '"+value+"' does not match '" + item.asString() +"'");
                    return false;
                }
                return true;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("Pattern match");
            }
        };
    }

    // <== STRING <==


    // ==> NUMBER ==>

    public static Matcher<JsonElement> isLessThan(final double value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {

            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not Number
                if(!item.isNumber()) return true;
                if(!(item.asDouble()<value)) {
                    mismatchDescription.appendText("value is not less than exclusive maximum " + value);
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("exclusive maximum");
            }
        };
    }

    public static Matcher<JsonElement> isLessOrEqualThan(final double value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {

            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not Number
                if(!item.isNumber()) return true;
                if(!(item.asDouble()<=value)) {
                    mismatchDescription.appendText("value is not less than maximum " + value);
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("maximum");
            }
        };
    }

    public static Matcher<JsonElement> isMoreThan(final double value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {

            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not Number
                if(!item.isNumber()) return true;
                if(!(item.asDouble()>value)) {
                    mismatchDescription.appendText("value is not more than exclusive minimum " + value);
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("exclusive minimum");
            }
        };
    }



    public static Matcher<JsonElement> isMoreOrEqualThan(final double value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {

            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not Number
                if(!item.isNumber()) return true;
                if(!(item.asDouble()>=value)) {
                    mismatchDescription.appendText("value is not more than minimum " + value);
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("minimum");
            }
        };
    }

    public static Matcher<JsonElement> isMultipleOf(final double value) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not Number
                if(!item.isNumber()) return true;

                Number remainder = item.asDouble()%value;
                if(!remainder.equals(0) && !remainder.equals(0.0)) {
                    mismatchDescription.appendText("value is not multipleOf " + value);
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("multipleOf");
            }
        };
    }

    // <== NUMBER <==




    // ==> COMMON ==>
    public static Matcher<JsonElement> isOfType(final String type) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                if(type.equals(item.getJsonType()))
                    return true;
                else {
                    mismatchDescription.appendText("expected type was '" + type + "' but found '" + item.getJsonType() +"'");
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is of Type: " + type);
            }
        };
    }

    public static Matcher<JsonElement> isOfType(final ArrayList<String> types) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                if(types.contains(item.getJsonType()))
                    return true;
                else {
                    mismatchDescription.appendText("expected type was any of '" + types.toString() + "' but found '" + item.getJsonType() +"'");
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is of Type: " + types.toString());
            }
        };
    }

    // <== COMMON <==

    // ==> ARRAY ==>

    public static Matcher<JsonElement> areItemsValid(final Validator validator) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonArray
                if(!item.isJsonArray()) return true;

                for(int i=0;i<item.asJsonArray().length();i++) {
                    if(!isItemValid(validator,i).matches(item)) return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is array item valid");
            }
        };
    }

    public static Matcher<JsonElement> isItemValid(final Validator validator, final int itemPos) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonArray
                if(!item.isJsonArray()) return true;

                //we also dont care if the item at position is not actually there
                //if it is needed it will be handled by another matcher
                if(item.asJsonArray().opt(itemPos) == null) return true;

                if(!validator.isValid(item.asJsonArray().opt(itemPos))) {
                    mismatchDescription.appendText("item at pos: " + itemPos + ", does not validate by validator " + validator.getTitle());
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is array item valid");
            }
        };
    }

    public static Matcher<JsonElement> doesItemCountMatches(final int itemsCount) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonArray
                if(!item.isJsonArray()) return true;


                if(item.asJsonArray().length() > itemsCount) {
                    mismatchDescription.appendText("items in Json array more than defined");
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("array items max count");
            }
        };
    }

    public static Matcher<JsonElement> maxItems(final int maxItems) {
        return doesItemCountMatches(maxItems);
    }

    public static Matcher<JsonElement> minItems(final int minItems) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the items if parent item is not JsonArray
                if(!item.isJsonArray()) return true;


                if(item.asJsonArray().length() < minItems) {
                    mismatchDescription.appendText("items in Json array less than defined");
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("array items min count");
            }
        };
    }


    public static Matcher<JsonElement> areItemsUnique() {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the items if parent item is not JsonArray
                if(!item.isJsonArray()) return true;

                JsonElement prevEl = null;
                for(JsonElement el:item.asJsonArray()) {
                    if (prevEl!=null && el.equals(prevEl)) {
                        mismatchDescription.appendText("items in Json array are not unique");
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("unique items");
            }
        };
    }


    // <== ARRAY <==

    // ==> OBJECT ==>


    public static Matcher<JsonElement> maxProperties(final int maxProperties) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonObject
                if(!item.isJsonObject()) return true;


                if(item.asJsonObject().length() > maxProperties) {
                    mismatchDescription.appendText("properties in Json object more than defined");
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("object properties max count");
            }
        };
    }

    public static Matcher<JsonElement> minProperties(final int minProperties) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonObject
                if(!item.isJsonObject()) return true;


                if(item.asJsonObject().length() < minProperties) {
                    mismatchDescription.appendText("properties in Json object less than defined");
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("object properties min count");
            }
        };
    }



    public static Matcher<JsonElement> isPropertyPresent(final String property) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonObject
                if(!item.isJsonObject()) return true;

                if(!item.asJsonObject().has(property)) {
                    mismatchDescription.appendText("property: '" + property + "' is missing");
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("object property is present");
            }
        };
    }

    public static Matcher<JsonElement> isPropertyValid(final Validator validator, final String property) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonObject
                if(!item.isJsonObject()) return true;

                //we also dont care if the property is not actually there
                //if it is needed it will be handled by the "required" constraint on another matcher
                if(!item.asJsonObject().has(property)) return true;

                return validator.isValid(item.asJsonObject().opt(property));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is object property valid");
            }
        };
    }

    public static Matcher<JsonElement> isPropertyPatternValid(final Validator validator, final String propertyPattern) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                //we do not care for the properties if parent item is not JsonObject
                if(!item.isJsonObject()) return true;

                //we also dont care if the property is not actually there
                //if it is needed it will be handled by the "required" constraint on another matcher

                Pattern p = Pattern.compile(propertyPattern);
                for(Map.Entry<String, JsonElement> entry : item.asJsonObject()) {
                    if(p.matcher(entry.getKey()).matches()) {
                        if(!validator.isValid(entry.getValue())) {
                            mismatchDescription.appendText("property: '" + entry.getKey() + "' is not valid by propertyPattern: " + propertyPattern);
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is object property valid");
            }
        };
    }


    // <== OBJECT <==



    // ==> GENERAL ==>

    /**
     * General matcher
     * @param validator
     * @param element
     * @return
     */
    public static Matcher<JsonElement> isElementValid(final Validator validator,final JsonElement element) {
        return new TypeSafeDiagnosingMatcher<JsonElement>() {
            @Override
            protected boolean matchesSafely(JsonElement item, Description mismatchDescription) {
                if(!validator.isValid(element)) {
                    mismatchDescription.appendText("element: " + element.toString() + ", does not validate by validator " + validator.getTitle());
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is array item valid");
            }
        };
    }

    public static Matcher<CharSequence> hasPattern(final String regex) {
        final Pattern p = Pattern.compile(regex);
        return new TypeSafeDiagnosingMatcher<CharSequence>() {
            @Override
            protected boolean matchesSafely(CharSequence item, Description mismatchDescription) {
                if (p.matcher(item).matches()) return true;
                else {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("string matching pattern: " + p.pattern());
            }
        };
    }



    // <== GENERAL <==

}
