package io.jonasg.xjx;

import static io.jonasg.xjx.Token.Type.CHARACTER_DATA;
import static io.jonasg.xjx.Token.Type.START_TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;

import java.io.BufferedReader;
import java.io.StringReader;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TokenizerIntegrationTest {

    @Test
    void shouldTokenize() {
        // given
        var tokenizer = new Tokenizer();
        var reader = new BufferedReader(new StringReader("""
                 <?xml version="1.0" encoding="UTF-8"?>
                <cellosaurus xmlns:bk="urn:bk.example.com">
                    <cell-line category="Hybridoma" created="2012-06-06" last_updated="2020-03-12" entry_version="6">
                        <bk:foo>bar</bk:foo>
                        <accession-list>
                            <accession type="primary">CVCL_B375</accession>
                        </accession-list>
                        <name-list>
                            <name type="identifier">#490</name>
                            <name type="synonym">490</name>
                            <name type="synonym">Mab 7</name>
                            <name type="synonym">Mab7</name>
                        </name-list><comment-list>
                            <comment category="Monoclonal antibody target">Cronartium ribicola antigens</comment>
                            <comment category="Monoclonal antibody isotype">IgM, kappa</comment>
                        </comment-list>
                        <species-list>
                            <cv-term terminology="NCBI-Taxonomy" accession="10090">Mus musculus</cv-term>
                        </species-list>
                        <derived-from>
                            <cv-term terminology="Cellosaurus" accession="CVCL_4032">P3X63Ag8.653</cv-term>
                        </derived-from>
                        <reference-list>
                            <reference resource-internal-ref="Patent=US5616470"/>
                        </reference-list>
                        <xref-list>
                            <xref database="CLO"    
                                  category="Ontologies" accession="CLO_0001018"
                            
                            >
                                <url>
                                    <![CDATA[https://www.ebi.ac.uk/ols/ontologies/clo/terms?iri=http://purl.obolibrary.org/obo/CLO_0001018]]></url>
                            </xref>
                            <xref database="ATCC" category="Cell line collections" accession="HB-12029">
                                <url><![CDATA[https://www.atcc.org/Products/All/HB-12029.aspx]]></url>
                            </xref>
                            <xref database="Wikidata" category="Other" accession="Q54422073">
                                <url><![CDATA[https://www.wikidata.org/wiki/Q54422073]]></url>
                            </xref>
                        </xref-list>
                    </cell-line>
                </cellosaurus>"""));

        // when
        var tokens = tokenizer.tokenize(reader);

        // then
        assertThat(tokens.toList())
                .hasSize(65)
                .is(isStartTagWithNamespace("foo", "bk"), atIndex(3))
                .is(isStartTag("url"), atIndex(58))
                .is(isCharacterData("https://www.wikidata.org/wiki/Q54422073"), atIndex(59));
    }

    private static Condition<Token<?>> isStartTag(String name) {
        return new Condition<>(t -> new Token<>(Token.Type.START_TAG, new StartTag(name)).equals(t), START_TAG + " = " + name);
    }

    private static Condition<Token<?>> isStartTagWithNamespace(String name, String namespace) {
        return new Condition<>(t -> new Token<>(Token.Type.START_TAG, new StartTag(name, namespace)).equals(t), START_TAG + " = " + name);
    }

    private static Condition<Token<?>> isCharacterData(String data) {
        return new Condition<>(t -> new Token<>(Token.Type.CHARACTER_DATA, data).equals(t), CHARACTER_DATA + " = " + data);
    }

    @Test
    void shouldHandleEmptyLinesWithinDocument() {
        // given
        var tokenizer = new Tokenizer();
        var reader = new BufferedReader(new StringReader("""
                    <?xml version="1.0" encoding="UTF-8"?>
                    
                    <WeatherData>
                    
                        <Forecasts>
                        
                            <Day Date="2023-09-12">
                                <High>
                                    <Value>71</Value>
                                    <Unit>°F</Unit>
                                </High>
                                <Low>
                                    <Value>60</Value>
                                    <Unit>°F</Unit>
                                </Low>
                                <Precipitation>
                                    <Value>10</Value>
                                    <Unit>%</Unit>
                                </Precipitation>
                                <WeatherCondition>Partly Cloudy</WeatherCondition>
                            </Day>
                            
                            <Day Date="2023-09-13">
                                <High>
                                    <Value>78</Value>
                                    <Unit>°F</Unit>
                                </High>
                                <Low>
                                    <Value>62</Value>
                                    <Unit>°F</Unit>
                                </Low>
                                <Precipitation>
                                
                                    <Value>10</Value>
                                    
                                    <Unit>%</Unit>
                                    
                                </Precipitation>
                                
                                <WeatherCondition>Partly Cloudy</WeatherCondition>
                            </Day>
                            
                        </Forecast>
                        
                    </WeatherData>
                    
                    """));

        // when
        var tokens = tokenizer.tokenize(reader);

        // then
        assertThat(tokens.count()).isEqualTo(63);
    }
}
