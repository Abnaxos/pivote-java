/*
 * Copyright 2012 Piratenpartei Schweiz
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
package ch.piratenpartei.pivote.serialize.types;

import java.io.Serializable;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class LangString implements Serializable {
    private static final long serialVersionUID = 2012060101L;

    private static final Language[] LANGUAGES = Language.values();

    private final String[] strings = new String[LANGUAGES.length];

    public String get(Language language) {
        String string = strings[language.ordinal()];
        if ( string != null ) {
            return string;
        }
        else {
            for ( int i = 0, stringsLength = strings.length; i < stringsLength; i++ ) {
                String s = strings[i];
                if ( s != null ) {
                    return "[?" + language + "?] " + s;
                }
            }
            return "!!!MISSING LANGSTRING!!!";
        }
    }

    public String get(Locale locale) {
        return get(Language.forLocale(locale, Language.ENGLISH));
    }

    public String get() {
        return get(Locale.getDefault());
    }

    public void set(Language language, String string) {
        strings[language.ordinal()] = string;
    }

    public static enum Language {
        ENGLISH, GERMAN, FRENCH, ITALIAN;

        private static final ImmutableMap<String, Language> localeMap =
                ImmutableMap.<String, Language>builder()
                        .put("en", ENGLISH)
                        .put("de", GERMAN)
                        .put("fr", FRENCH)
                        .put("it", ITALIAN)
                        .build();

        public static Language forLocale(Locale locale) {
            return localeMap.get(locale.getLanguage());
        }

        public static Language forLocale(Locale locale, Language fallback) {
            Language result = localeMap.get(locale.getLanguage());
            if ( result != null ) {
                return result;
            }
            else {
                return fallback;
            }
        }

        public static Language forIndex(int index) {
            if ( index < 0 || index >= LANGUAGES.length ) {
                return null;
            }
            else {
                return LANGUAGES[index];
            }
        }

    }

}
