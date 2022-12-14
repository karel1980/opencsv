package com.opencsv;

import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Andre Rosot
 * @since 4.2
 */
public class CSVReaderHeaderAwareBuilderTest {

    private CSVReaderHeaderAwareBuilder builder;
    private final Reader reader = new StringReader("header");

    private static Locale systemLocale;

    @BeforeAll
    public static void storeSystemLocale() {
        systemLocale = Locale.getDefault();
    }

    @AfterEach
    public void setSystemLocaleBackToDefault() {
        Locale.setDefault(systemLocale);
    }

    @BeforeEach
    public void setUp() {
        Locale.setDefault(Locale.US);
        builder = new CSVReaderHeaderAwareBuilder(reader);
    }

    @Test
    public void testDefaultBuilder() {
        assertSame(reader, builder.getReader());
        assertNull(builder.getCsvParser());
        assertEquals(
                CSVReader.DEFAULT_SKIP_LINES,
                builder.getSkipLines());
        assertEquals(0, builder.getMultilineLimit());

        final CSVReader csvReader = builder.build();
        assertEquals(
                CSVReader.DEFAULT_SKIP_LINES,
                csvReader.getSkipLines());
        assertEquals(CSVReader.DEFAULT_KEEP_CR, csvReader.keepCarriageReturns());
        assertEquals(CSVReader.DEFAULT_VERIFY_READER, csvReader.verifyReader());
        assertEquals(CSVReader.DEFAULT_MULTILINE_LIMIT, csvReader.getMultilineLimit());
        assertEquals(Locale.getDefault(), csvReader.errorLocale);
    }

    @Test
    public void testNullReader() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new CSVReaderBuilder(null));
    }

    @Test
    public void testWithCSVParserNull() {
        builder.withCSVParser(mock(CSVParser.class));
        builder.withCSVParser(null);
        assertNull(builder.getCsvParser());
    }

    @Test
    public void testWithCSVParser() {
        final ICSVParser csvParser = mock(CSVParser.class);

        builder.withCSVParser(csvParser);

        assertSame(csvParser, builder.getCsvParser());
    }

    @Test
    public void testWithSkipLines() {
        builder.withSkipLines(99);

        assertEquals(99, builder.getSkipLines());
    }

    @Test
    public void testWithKeepCR() {
        builder.withKeepCarriageReturn(true);
        assertTrue(builder.keepCarriageReturn());

        final CSVReader actual = builder.build();
        assertTrue(actual.keepCarriageReturns());
    }

    @Test
    public void testWithSkipLinesZero() {
        builder.withSkipLines(0);

        assertEquals(0, builder.getSkipLines());

        final CSVReader actual = builder.build();
        assertSame(0, actual.getSkipLines());
    }

    @Test
    public void testWithSkipLinesNegative() {
        builder.withSkipLines(-1);

        assertEquals(0, builder.getSkipLines());

        final CSVReader actual = builder.build();
        assertSame(0, actual.getSkipLines());
    }

    @Test
    public void testWithVerifyReader() {
        final CSVReader r = builder.withVerifyReader(false).build();
        assertFalse(r.verifyReader());
    }

    @Test
    public void builderWithNullFieldIndicator() {
        final CSVReader r = builder.withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).build();

        assertEquals(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS, r.getParser().nullFieldIndicator());
    }

    @Test
    public void builderWithMultilineLimit() {
        final CSVReader r = builder.withMultilineLimit(Integer.MAX_VALUE).build();

        assertEquals(Integer.MAX_VALUE, r.getMultilineLimit());
    }

    @Test
    public void builderWithErrorLocale() {
        final CSVReader r = builder.withErrorLocale(Locale.KOREAN).build();
        assertEquals(Locale.KOREAN, r.errorLocale);
    }

    @Test
    public void shouldThrowExceptionWhenCannotReadHeader() throws IOException {
        Reader reader = mock(Reader.class);
        when(reader.read(any((char[].class)), eq(0), eq(8192))).thenThrow(new IOException());
        Assertions.assertThrows(RuntimeException.class, () -> new CSVReaderHeaderAwareBuilder(reader).build());
    }
}