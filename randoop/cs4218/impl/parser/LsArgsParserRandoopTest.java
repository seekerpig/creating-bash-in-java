package cs4218.impl.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


@SuppressWarnings("PMD")
public class LsArgsParserRandoopTest {
    public static boolean debug = false;

    @Test
    public void test02() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "LsArgsParserRandoopTest.test02");
        sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser lsArgsParser0 = new sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser();
        java.util.List<java.lang.String> strList1 = lsArgsParser0.getDirectories();
        java.util.List<java.lang.String> strList2 = lsArgsParser0.getDirectories();
        java.lang.Boolean boolean3 = lsArgsParser0.isSortByExt();
        Assertions.assertNotNull(strList1);
        Assertions.assertNotNull(strList2);
        Assertions.assertEquals(false, boolean3);
    }

    @Test
    public void test03() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "LsArgsParserRandoopTest.test03");
        sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser lsArgsParser0 = new sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser();
        java.util.List<java.lang.String> strList1 = lsArgsParser0.getDirectories();
        java.util.List<java.lang.String> strList2 = lsArgsParser0.getDirectories();
        java.lang.String[] strArray5 = new java.lang.String[]{"illegal option -- ", "illegal option -- "};
        lsArgsParser0.parse(strArray5);
        sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser lsArgsParser7 = new sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser();
        java.util.List<java.lang.String> strList8 = lsArgsParser7.getDirectories();
        java.util.List<java.lang.String> strList9 = lsArgsParser7.getDirectories();
        java.lang.String[] strArray12 = new java.lang.String[]{"illegal option -- ", "illegal option -- "};
        lsArgsParser7.parse(strArray12);
        java.lang.String[] strArray18 = new java.lang.String[]{"illegal option -- ", "hi!", "", "hi!"};
        lsArgsParser7.parse(strArray18);
        lsArgsParser0.parse(strArray18);
        Assertions.assertNotNull(strList1);
        Assertions.assertNotNull(strList2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNotNull(strList8);
        Assertions.assertNotNull(strList9);
        Assertions.assertNotNull(strArray12);
        Assertions.assertNotNull(strArray18);
    }

    @Test
    public void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "LsArgsParserRandoopTest.test04");
        sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser lsArgsParser0 = new sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser();
        java.util.List<java.lang.String> strList1 = lsArgsParser0.getDirectories();
        java.util.List<java.lang.String> strList2 = lsArgsParser0.getDirectories();
        java.lang.String[] strArray5 = new java.lang.String[]{"illegal option -- ", "illegal option -- "};
        lsArgsParser0.parse(strArray5);
        java.lang.String[] strArray11 = new java.lang.String[]{"illegal option -- ", "hi!", "", "hi!"};
        lsArgsParser0.parse(strArray11);
        java.lang.Boolean boolean13 = lsArgsParser0.isSortByExt();
        java.lang.Boolean boolean14 = lsArgsParser0.isRecursive();
        Assertions.assertNotNull(strList1);
        Assertions.assertNotNull(strList2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNotNull(strArray11);
        Assertions.assertEquals(false, boolean13);
        Assertions.assertEquals(false, boolean14);
    }

    @Test
    public void test05() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "LsArgsParserRandoopTest.test05");
        sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser lsArgsParser0 = new sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser();
        java.util.List<java.lang.String> strList1 = lsArgsParser0.getDirectories();
        java.util.List<java.lang.String> strList2 = lsArgsParser0.getDirectories();
        java.lang.String[] strArray5 = new java.lang.String[]{"illegal option -- ", "illegal option -- "};
        lsArgsParser0.parse(strArray5);
        java.lang.String[] strArray11 = new java.lang.String[]{"illegal option -- ", "hi!", "", "hi!"};
        lsArgsParser0.parse(strArray11);
        java.lang.Boolean boolean13 = lsArgsParser0.isRecursive();
        Assertions.assertNotNull(strList1);
        Assertions.assertNotNull(strList2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNotNull(strArray11);
        Assertions.assertEquals(false, boolean13);
    }

    @Test
    public void test06() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "LsArgsParserRandoopTest.test06");
        sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser lsArgsParser0 = new sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser();
        java.util.List<java.lang.String> strList1 = lsArgsParser0.getDirectories();
        java.util.List<java.lang.String> strList2 = lsArgsParser0.getDirectories();
        java.lang.String[] strArray5 = new java.lang.String[]{"illegal option -- ", "illegal option -- "};
        lsArgsParser0.parse(strArray5);
        java.lang.Class<?> wildcardClass7 = strArray5.getClass();
        Assertions.assertNotNull(strList1);
        Assertions.assertNotNull(strList2);
        Assertions.assertNotNull(strArray5);
        Assertions.assertNotNull(wildcardClass7);
    }
}
