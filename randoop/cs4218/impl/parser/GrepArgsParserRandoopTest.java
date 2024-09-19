package cs4218.impl.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import sg.edu.nus.comp.cs4218.impl.parser.ArgsParser;
import sg.edu.nus.comp.cs4218.impl.parser.GrepArgsParser;

@SuppressWarnings("PMD")
public class GrepArgsParserRandoopTest {
    static boolean debug = false;

    @Test
    void test01() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test01");
        String str0 = ArgsParser.ILLEGAL_FLAG_MSG;
        Assertions.assertEquals("illegal option -- ", str0);
    }

    @Test
    void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test04");
        GrepArgsParser grepArgsParser0 = new GrepArgsParser();
        String[] strArray1 = grepArgsParser0.getFileNames();
        String[] strArray2 = grepArgsParser0.getFileNames();
        String[] strArray5 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser0.parse(strArray5);
        String str7 = grepArgsParser0.getPattern();
        Assertions.assertNull(strArray1);
        Assertions.assertNull(strArray2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNull(str7);
    }

    @Test
    void test07() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test07");
        GrepArgsParser grepArgsParser0 = new GrepArgsParser();
        String[] strArray1 = grepArgsParser0.getFileNames();
        String[] strArray2 = grepArgsParser0.getFileNames();
        String[] strArray5 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser0.parse(strArray5);
        GrepArgsParser grepArgsParser7 = new GrepArgsParser();
        String[] strArray8 = grepArgsParser7.getFileNames();
        String[] strArray9 = grepArgsParser7.getFileNames();
        String[] strArray12 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser7.parse(strArray12);
        grepArgsParser0.parse(strArray12);
        Boolean boolean15 = grepArgsParser0.isInvert();
        Assertions.assertNull(strArray1);
        Assertions.assertNull(strArray2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNull(strArray8);
        Assertions.assertNull(strArray9);
        Assertions.assertNotNull(strArray12);
        Assertions.assertFalse(boolean15);
    }

    @Test
    void test08() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test08");
        GrepArgsParser grepArgsParser0 = new GrepArgsParser();
        String[] strArray1 = grepArgsParser0.getFileNames();
        String[] strArray2 = grepArgsParser0.getFileNames();
        // The following exception was thrown during execution in test generation
        try {
            String str3 = grepArgsParser0.getPattern();
            Assertions.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: Index 0 out of bounds for length 0");
        } catch (IndexOutOfBoundsException e) {
            // Expected exception.
        }
        Assertions.assertNull(strArray1);
        Assertions.assertNull(strArray2);
    }

    @Test
    void test09() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test09");
        GrepArgsParser grepArgsParser0 = new GrepArgsParser();
        String[] strArray1 = grepArgsParser0.getFileNames();
        String[] strArray2 = grepArgsParser0.getFileNames();
        String[] strArray5 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser0.parse(strArray5);
        GrepArgsParser grepArgsParser7 = new GrepArgsParser();
        String[] strArray8 = grepArgsParser7.getFileNames();
        String[] strArray9 = grepArgsParser7.getFileNames();
        String[] strArray12 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser7.parse(strArray12);
        grepArgsParser0.parse(strArray12);
        GrepArgsParser grepArgsParser15 = new GrepArgsParser();
        String[] strArray16 = grepArgsParser15.getFileNames();
        String[] strArray17 = grepArgsParser15.getFileNames();
        String[] strArray20 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser15.parse(strArray20);
        grepArgsParser0.parse(strArray20);
        Assertions.assertNull(strArray1);
        Assertions.assertNull(strArray2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNull(strArray8);
        Assertions.assertNull(strArray9);
        Assertions.assertNotNull(strArray12);
        Assertions.assertNull(strArray16);
        Assertions.assertNull(strArray17);
        Assertions.assertNotNull(strArray20);
    }

    @Test
    void test10() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test10");
        GrepArgsParser grepArgsParser0 = new GrepArgsParser();
        String[] strArray1 = grepArgsParser0.getFileNames();
        String[] strArray2 = grepArgsParser0.getFileNames();
        String[] strArray5 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser0.parse(strArray5);
        GrepArgsParser grepArgsParser7 = new GrepArgsParser();
        String[] strArray8 = grepArgsParser7.getFileNames();
        String[] strArray9 = grepArgsParser7.getFileNames();
        String[] strArray12 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser7.parse(strArray12);
        grepArgsParser0.parse(strArray12);
        String str15 = grepArgsParser0.getPattern();
        Assertions.assertNull(strArray1);
        Assertions.assertNull(strArray2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNull(strArray8);
        Assertions.assertNull(strArray9);
        Assertions.assertNotNull(strArray12);
        Assertions.assertNull(str15);
    }

    @Test
    void test11() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test11");
        GrepArgsParser grepArgsParser0 = new GrepArgsParser();
        String[] strArray1 = grepArgsParser0.getFileNames();
        String[] strArray2 = grepArgsParser0.getFileNames();
        Boolean boolean3 = grepArgsParser0.isInvert();
        String[] strArray4 = grepArgsParser0.getFileNames();
        Assertions.assertNull(strArray1);
        Assertions.assertNull(strArray2);
        Assertions.assertFalse(boolean3);
        Assertions.assertNull(strArray4);
    }

    @Test
    void test12() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test12");
        GrepArgsParser grepArgsParser0 = new GrepArgsParser();
        String[] strArray1 = grepArgsParser0.getFileNames();
        String[] strArray2 = grepArgsParser0.getFileNames();
        String[] strArray5 = new String[]{"illegal option -- ", "illegal option -- "};
        grepArgsParser0.parse(strArray5);
        Class<?> wildcardClass7 = strArray5.getClass();
        Assertions.assertNull(strArray1);
        Assertions.assertNull(strArray2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNotNull(wildcardClass7);
    }
}

