## LocalizedField

`LocalizedField` is a `CustomField` for editing the **language versions of a
single piece of text** — a title, a slogan, a product description — when your
application stores content in several languages. The user edits one language at
a time, and the field value is a `Map<Locale, String>` from each locale to its
text.

It comes in two flavours:

* **`LocalizedTextField`** — single-line, backed by a `TextField`.
* **`LocalizedTextArea`** — multi-line, backed by a `TextArea`.

Optionally, a pluggable **translation engine** can fill the remaining languages
from the one the user just typed. With Spring (and, say, Spring AI) this is
literally a matter of registering one bean — the "auto translate" action then
appears on its own.

### Basic usage

Pass the locales you want to support. They can be given in any order; the field
always shows them alphabetically by their localized name. A leading `String`
argument sets the field label:

```java
LocalizedTextField title = new LocalizedTextField("Title",
        Locale.ENGLISH, Locale.of("fi"), Locale.of("sv"), Locale.GERMAN);
add(title);

// The value is a Map<Locale, String>
Map<Locale, String> value = title.getValue();
```

`LocalizedTextArea` works the same way and is sizable straight through the
standard `HasSize` API; the height propagates down to the editor:

```java
LocalizedTextArea description = new LocalizedTextArea(locales);
description.setLabel("Description");
description.setWidth("480px");
description.setHeight("220px");
```

### Binding to a domain model

The field value type is `Map<Locale, String>`. If your domain object stores
translations the same way, binding is glue-free:

```java
binder.forField(title).bind(Product::getTitle, Product::setTitle);
```

Many applications instead key translations by **language code** (`"fi"`),
e.g. persisted as a `jsonb` column — a `Map<String, String>`. For that, bind
through `languageCodeMapConverter()`:

```java
binder.forField(title)
      .withConverter(title.languageCodeMapConverter())   // Map<Locale,String> <-> Map<String,String>
      .bind(Product::getTitleTexts, Product::setTitleTexts);
```

Conversion is by language code only, and the field rebuilds the presentation
with its own locales, so locale country variants (`en` vs `en_US`) do not cause
surprises. The same tolerance applies when binding a `Map<Locale, String>`
directly: a value keyed by `en_US` still shows in an `en` tab.

For a wrapper type — say a `TranslatedText` backed by a `Map<String, String>` —
either bind through its map accessor, or chain a second converter that wraps and
unwraps it.

### Tab mode vs. combo box mode

By default the languages are presented as a **tab bar**. The active tab is
marked with an underline, and the editor fills a single bordered box below it —
no "box in a box".

Once there are more languages than a configurable threshold (20 by default), a
tab bar becomes unwieldy, so the component automatically switches to a
**combo box** for picking the language instead. Everything else stays the same;
the two modes look alike.

The threshold is configurable globally or per instance:

```java
// Global default for all fields
LocalizedField.setDefaultComboBoxThreshold(12);

// Per instance (these locales exceed the given threshold of 8 -> combo box)
LocalizedTextField keyword = new LocalizedTextField(manyLocales, 8);
```

### Showing flags, names, or both

Each language in the selector is shown by default as its **flag emoji followed
by the localized name** (e.g. 🇬🇧 English). Switch this with
`setLanguageDisplay(...)` — no subclassing needed, and the selector refreshes
immediately:

```java
title.setLanguageDisplay(LocalizedField.LanguageDisplay.FLAG);       // 🇬🇧 (flag only)
title.setLanguageDisplay(LocalizedField.LanguageDisplay.NAME);       // English (name only)
title.setLanguageDisplay(LocalizedField.LanguageDisplay.FLAG_AND_NAME); // 🇬🇧 English (default)
```

In flag-only mode the language name is still available as a **tooltip** on the
tab (and screen readers reach it through the editor's accessible name), so the
field stays usable. When a language has no known flag, the flag-bearing modes
fall back to showing its name, so a language is never blank.

The flag itself comes from `flagFor(Locale)`. It derives a country from the
locale (or, for the most common languages, from the language code) and turns it
into an emoji. Override it to add languages or to pick a different country for a
language (e.g. US instead of GB for English):

```java
LocalizedTextField title = new LocalizedTextField("Title", locales) {
    @Override
    protected String flagFor(Locale locale) {
        if (locale.getLanguage().equals("en")) {
            return super.flagFor(Locale.of("en", "US")); // 🇺🇸 instead of 🇬🇧
        }
        return super.flagFor(locale);
    }
};
```

### Which language is open

When the field is attached, it opens the **user's own language** if it is one of
the editable languages (matched first exactly, then by language code). Otherwise
the alphabetically first language is opened.

You can choose the open language explicitly — this overrides the user-locale
default:

```java
title.setSelectedLocale(Locale.of("fi"));
Locale open = title.getSelectedLocale();
```

### Automatic translation

A `Translator` turns the text of the currently open language into the other
languages. The interface is intentionally tiny and backend-agnostic:

```java
@FunctionalInterface
public interface Translator {
    String translate(String text, Locale from, Locale to);
}
```

When a translator is available, a small **"magic" icon** appears in the
bottom-right corner of the field. Clicking it translates the open language into
all the others. The work runs **off the UI thread** (translators may call a slow
network/LLM API) on an `ActionButton`. While it runs, the icon turns into a
**spinner** and further clicks are blocked, so impatient users cannot stack up
several calls. Server push or polling is handled automatically.

#### Providing a translator with the Instantiator (Spring)

The component looks a translator up from Vaadin's `Instantiator` on attach, so
in a Spring application **registering a `Translator` bean is all it takes** for
the action to appear — no per-field wiring. Conversely, when the Instantiator
finds nothing (and nothing was set explicitly), the action stays hidden. So the
button's visibility follows availability: to hide it when, say, no AI is
configured, simply don't register the bean — gate it with `@ConditionalOnProperty`
as below, or opt a single field out with `setTranslator(null)`.

```java
@Configuration
class TranslationConfig {

    @Bean
    Translator translator() {
        return (text, from, to) -> myTranslationService.translate(text, from, to);
    }
}
```

#### Spring AI example

A large language model makes a capable translation engine. With
[Spring AI](https://docs.spring.io/spring-ai/reference/) and, for example, the
Mistral AI starter on the classpath, a `ChatClient.Builder` is auto-configured
from your `spring.ai.mistralai.api-key`. Wrap it as a `Translator` bean:

```java
@Configuration
class AiTranslationConfig {

    @Bean
    @ConditionalOnProperty("spring.ai.mistralai.api-key")
    Translator aiTranslator(ChatClient.Builder chatClientBuilder) {
        ChatClient chat = chatClientBuilder.build();
        return (text, from, to) -> chat.prompt()
                .system("You are a translation engine. Translate the user's message from "
                        + from.getDisplayLanguage(Locale.ENGLISH) + " to "
                        + to.getDisplayLanguage(Locale.ENGLISH)
                        + ". Reply with the translation only, no quotes or explanations.")
                .user(text)
                .call()
                .content();
    }
}
```

Because the bean is `@ConditionalOnProperty`, the translate action appears only
when an API key is configured; without one, the fields simply show no magic
icon. You could just as well provide a different `Translator` (an online
translation API, a glossary, a stub for tests) — the component does not care.

#### Setting or disabling the translator explicitly

Calling `setTranslator(...)` overrides whatever the Instantiator would supply.
This also lets you turn the action **off** for a particular field even when a
bean exists, by passing `null`:

```java
field.setTranslator(myTranslator);   // use this one here
field.setTranslator(null);           // no translate action on this field
```

#### Customizing the action button

`getTranslateButton()` returns the `ActionButton`, so you can adjust its
presentation — change the icon or tooltip via its inner button, set a busy text,
show its progress bar, and so on:

```java
field.getTranslateButton().getButton()
        .setIcon(VaadinIcon.LANGUAGE.create());
```

### Theming

The styling is theme-agnostic and tuned to look the same in spirit in both the
**Aura** and **Lumo** themes: a plain bordered box, a shaded selector header
with a straight divider, the editor filling the box, an accent focus ring that
joins the active-tab underline, and the bare corner icon for translation. It is
all done with a single scoped stylesheet, so it does not affect other tab bars,
combo boxes or buttons in your application.

### Extension points

`LocalizedField` is designed to be subclassed and tweaked:

* `createField(Locale)` — the abstract factory for the per-language editor (this
  is what `LocalizedTextField` and `LocalizedTextArea` implement).
* `setLanguageDisplay(LanguageDisplay)` — show the flag, the name, or both
  (see [Showing flags, names, or both](#showing-flags-names-or-both)); no
  subclassing needed.
* `tabLabel(Locale)` / `languageName(Locale)` / `flagFor(Locale)` — customize the
  label, name and flag shown for each language. Note that overriding
  `tabLabel(Locale)` builds the label yourself and so bypasses the
  `LanguageDisplay` mode.
* `translateButtonText()` — localize the translate action's tooltip / accessible
  name.
