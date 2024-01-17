package io.jonasg.xjx.sax;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class SaxParserTest {

    @Nested
    class ShouldTriggerStartDocumentCallback {
        @Test
        void onDocumentTypeDeclaration() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <animals>
                    <foo>bar<foo>
                        <dog/>
                    </animals>
                    """));

            // when
            var startDocumentTriggered = new AtomicBoolean();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void startDocument() {
                    startDocumentTriggered.set(true);
                }
            });

            // then
            Assertions.assertThat(startDocumentTriggered).isTrue();
        }

        @Test
        void onMissingDocumentTypeDeclaration() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <animals>
                        <dog/>
                    </animals>
                    """));

            // when
            var startDocumentTriggered = new AtomicBoolean();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void startDocument() {
                    startDocumentTriggered.set(true);
                }
            });

            // then
            Assertions.assertThat(startDocumentTriggered).isTrue();
        }
    }

    @Nested
    class ShouldTriggerStartTag {
        @Test
        void onEmptyStartTag() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <animals>
                        <dog/>
                    </animals>
                    """));

            // when
            var actualStartTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void startTag(String namespace, String name, List<Attribute> attributes) {
                    actualStartTags.add(new ActualStartTag(name, attributes));
                }
            });

            // then
            Assertions.assertThat(actualStartTags)
                    .hasSize(2)
                    .contains(new ActualStartTag("animals"));
        }

        @Test
        void onStartTagWithAttribute() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <animals age="11">
                        <dog/>
                    </animals>
                    """));

            // when
            var actualStartTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void startTag(String namespace, String name, List<Attribute> attributes) {
                    actualStartTags.add(new ActualStartTag(name, attributes));
                }
            });

            // then
            Assertions.assertThat(actualStartTags)
                    .hasSize(2)
                    .contains(new ActualStartTag("animals", new Attribute("age", "11")));
        }


        @Test
        void onStartTagWithNamespace() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <a:animals age="11">
                        <dog/>
                    </a:animals>
                    """));

            // when
            var actualStartTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void startTag(String namespace, String name, List<Attribute> attributes) {
                    actualStartTags.add(new ActualStartTag(namespace, name, attributes));
                }
            });

            // then
            Assertions.assertThat(actualStartTags)
                    .hasSize(2)
                    .contains(new ActualStartTag("a", "animals", new Attribute("age", "11")));
        }

        @Test
        void onSelfClosingTag() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <animals age="11">
                        <dog/>
                    </animals>
                    """));

            // when
            var actualStartTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void startTag(String namespace, String name, List<Attribute> attributes) {
                    actualStartTags.add(new ActualStartTag(name, attributes));
                }
            });

            // then
            Assertions.assertThat(actualStartTags)
                    .hasSize(2)
                    .contains(new ActualStartTag("dog"));
        }
    }

    @Nested
    class ShouldTriggerEndTag {
        @Test
        void onNormalEndTag() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <animals>
                        <dog/>
                    </animals>
                    """));

            // when
            var actualEndTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void endTag(String namespace, String name) {
                    actualEndTags.add(new ActualEndTag(name));
                }
            });

            // then
            Assertions.assertThat(actualEndTags)
                    .hasSize(2)
                    .contains(new ActualEndTag("animals"), new ActualEndTag("dog"));
        }

        @Test
        void onNormalEndTagWithNamespace() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <a:animals>
                        <dog/>
                    </a:animals>
                    """));

            // when
            var actualEndTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void endTag(String namespace, String name) {
                    actualEndTags.add(new ActualEndTag(namespace, name));
                }
            });

            // then
            Assertions.assertThat(actualEndTags)
                    .hasSize(2)
                    .contains(new ActualEndTag("a", "animals"), new ActualEndTag("dog"));
        }

        @Test
        void onSelfClosingTag() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <dog/>
                    """));

            // when
            var actualEndTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void endTag(String namespace, String name) {
                    actualEndTags.add(new ActualEndTag(namespace, name));
                }
            });

            // then
            Assertions.assertThat(actualEndTags)
                    .hasSize(1)
                    .contains(new ActualEndTag("dog"));
        }
    }

    @Nested
    class ShouldTriggerEndDocument {
        @Test
        void onNormalEndTag() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <animals>
                        <dog/>
                    </animals>
                    """));

            // when
            var actualEndTags = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void endTag(String namespace, String name) {
                    actualEndTags.add(new ActualEndTag(name));
                }
            });

            // then
            Assertions.assertThat(actualEndTags)
                    .hasSize(2)
                    .contains(new ActualEndTag("animals"));
        }
    }

    @Nested
    class ShouldTriggerCharacters {
        @Test
        void onCharacterData() {
            // given
            var parser = new SaxParser();
            var xmlDocument = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    <animals>
                        <animal>dog</animal>
                        <animal>fish</animal>
                    </animals>
                    """));

            // when
            var actualCharacters = new ArrayList<>();
            parser.parse(xmlDocument, new TestSaxHandler() {
                @Override
                public void characters(String data) {
                    actualCharacters.add(data);
                }
            });

            // then
            Assertions.assertThat(actualCharacters)
                    .hasSize(2)
                    .contains("dog", "fish");
        }
    }

    record ActualEndTag(String namespace, String name) {
        ActualEndTag(String name) {
            this(null, name);
        }
    }

    record ActualStartTag(String namespace, String name, List<Attribute> attributes) {
        ActualStartTag(String name) {
            this(null, name, List.of());
        }

        public ActualStartTag(String name, Attribute attribute) {
            this(null, name, List.of(attribute));
        }

        public ActualStartTag(String name, List<Attribute> attribute) {
            this(null, name, attribute);
        }

        public ActualStartTag(String namespace, String name, Attribute attribute) {
            this(namespace, name, List.of(attribute));
        }
    }
}

