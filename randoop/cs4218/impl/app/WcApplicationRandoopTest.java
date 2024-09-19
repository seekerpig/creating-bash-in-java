package cs4218.impl.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD")
public class WcApplicationRandoopTest {

    public static boolean debug = false;

    @Test
    public void test01() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test01");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.io.InputStream inputStream4 = null;
        java.lang.String[] strArray6 = new java.lang.String[]{""};
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.countFromFileAndStdin((java.lang.Boolean) true, (java.lang.Boolean) false, (java.lang.Boolean) false, inputStream4, strArray6);
        });
        Assertions.assertNotNull(strArray6);
    }

    @Test
    public void test02() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test02");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.io.InputStream inputStream4 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.countFromStdin((java.lang.Boolean) true, (java.lang.Boolean) false, (java.lang.Boolean) true, inputStream4);
        });
    }

    @Test
    public void test03() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test03");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray4 = new java.lang.String[]{};
        java.lang.String str5 = wcApplication0.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray4);
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication6 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray10 = new java.lang.String[]{};
        java.lang.String str11 = wcApplication6.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray10);
        java.io.InputStream inputStream12 = null;
        java.io.OutputStream outputStream13 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.run(strArray10, inputStream12, outputStream13);
        });
        Assertions.assertNotNull(strArray4);
        Assertions.assertEquals(str5, "");
        Assertions.assertNotNull(strArray10);
        Assertions.assertEquals(str11, "");
    }

    @Test
    public void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test04");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.io.InputStream inputStream1 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.getCountReport(inputStream1);
        });
    }

    @Test
    public void test05() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test05");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.io.InputStream inputStream4 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.countFromStdin((java.lang.Boolean) true, (java.lang.Boolean) false, (java.lang.Boolean) false, inputStream4);
        });
    }

    @Test
    public void test06() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test06");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray8 = new java.lang.String[]{};
        java.lang.String str9 = wcApplication4.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray8);
        java.lang.String str10 = wcApplication0.countFromFiles((java.lang.Boolean) true, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray8);
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication11 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication15 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray19 = new java.lang.String[]{};
        java.lang.String str20 = wcApplication15.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray19);
        java.lang.String str21 = wcApplication11.countFromFiles((java.lang.Boolean) true, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray19);
        java.io.InputStream inputStream22 = null;
        java.io.OutputStream outputStream23 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.run(strArray19, inputStream22, outputStream23);
        });
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
        Assertions.assertNotNull(strArray19);
        Assertions.assertEquals(str20, "");
        Assertions.assertEquals(str21, "");
    }

    @Test
    public void test07() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test07");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray1 = null;
        java.io.InputStream inputStream2 = null;
        java.io.OutputStream outputStream3 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.run(strArray1, inputStream2, outputStream3);
        });
    }

    @Test
    public void test08() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test08");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray8 = new java.lang.String[]{};
        java.lang.String str9 = wcApplication4.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray8);
        java.lang.String str10 = wcApplication0.countFromFiles((java.lang.Boolean) true, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray8);
        java.io.InputStream inputStream14 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.countFromStdin((java.lang.Boolean) true, (java.lang.Boolean) true, (java.lang.Boolean) false, inputStream14);
        });
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
    }

    @Test
    public void test09() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test09");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray4 = new java.lang.String[]{};
        java.lang.String str5 = wcApplication0.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray4);
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication6 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication10 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray14 = new java.lang.String[]{};
        java.lang.String str15 = wcApplication10.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray14);
        java.lang.String str16 = wcApplication6.countFromFiles((java.lang.Boolean) true, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray14);
        java.io.InputStream inputStream17 = null;
        java.io.OutputStream outputStream18 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.run(strArray14, inputStream17, outputStream18);
        });
        Assertions.assertNotNull(strArray4);
        Assertions.assertEquals(str5, "");
        Assertions.assertNotNull(strArray14);
        Assertions.assertEquals(str15, "");
        Assertions.assertEquals(str16, "");
    }

    @Test
    public void test10() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test10");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        java.lang.String[] strArray4 = new java.lang.String[]{};
        java.lang.String str5 = wcApplication0.countFromFiles((java.lang.Boolean) false, (java.lang.Boolean) false, (java.lang.Boolean) false, strArray4);
        java.io.InputStream inputStream6 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.getCountReport(inputStream6);
        });
        Assertions.assertNotNull(strArray4);
        Assertions.assertEquals(str5, "");
    }

    @Test
    public void test11() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test11");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray8 = new String[]{};
        String str9 = wcApplication4.countFromFiles(false, false, false, strArray8);
        String str10 = wcApplication0.countFromFiles(true, false, false, strArray8);
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication11 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray15 = new String[]{};
        String str16 = wcApplication11.countFromFiles(false, false, false, strArray15);
        java.io.InputStream inputStream17 = null;
        java.io.OutputStream outputStream18 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.run(strArray15, inputStream17, outputStream18);
        });
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
        Assertions.assertNotNull(strArray15);
        Assertions.assertEquals(str16, "");
    }

    @Test
    public void test12() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test12");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray8 = new String[]{};
        String str9 = wcApplication4.countFromFiles(false, false, false, strArray8);
        String str10 = wcApplication0.countFromFiles(true, false, false, strArray8);
        java.io.InputStream inputStream14 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.countFromStdin(true, false, true, inputStream14);
        });
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
    }

    @Test
    public void test13() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test13");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray8 = new String[]{};
        String str9 = wcApplication4.countFromFiles(false, false, false, strArray8);
        String str10 = wcApplication0.countFromFiles(true, false, false, strArray8);
        Class<?> wildcardClass11 = wcApplication0.getClass();
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
        Assertions.assertNotNull(wildcardClass11);
    }

    @Test
    public void test16() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test16");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray8 = new String[]{};
        String str9 = wcApplication4.countFromFiles(false, false, false, strArray8);
        String str10 = wcApplication0.countFromFiles(true, false, false, strArray8);
        java.io.InputStream inputStream14 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.countFromStdin(false, false, true, inputStream14);
        });
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
    }

    @Test
    public void test17() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test17");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray3 = new String[]{"hi!", "       0 total\r\n\r\n"};
        java.io.InputStream inputStream4 = null;
        java.io.OutputStream outputStream5 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.WcException.class, () -> {
            wcApplication0.run(strArray3, inputStream4, outputStream5);
        });
        Assertions.assertNotNull(strArray3);
    }

    @Test
    public void test19() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test19");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray8 = new String[]{};
        String str9 = wcApplication4.countFromFiles(false, false, false, strArray8);
        String str10 = wcApplication0.countFromFiles(true, false, false, strArray8);
        Class<?> wildcardClass11 = strArray8.getClass();
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
        Assertions.assertNotNull(wildcardClass11);
    }

    @Test
    public void test20() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test20");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray8 = new String[]{};
        String str9 = wcApplication4.countFromFiles(false, false, false, strArray8);
        String str10 = wcApplication0.countFromFiles(true, false, false, strArray8);
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication14 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication18 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray22 = new String[]{};
        String str23 = wcApplication18.countFromFiles(false, false, false, strArray22);
        String str24 = wcApplication14.countFromFiles(true, false, false, strArray22);
        String str25 = wcApplication0.countFromFiles(true, false, false, strArray22);
        Class<?> wildcardClass26 = wcApplication0.getClass();
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
        Assertions.assertNotNull(strArray22);
        Assertions.assertEquals(str23, "");
        Assertions.assertEquals(str24, "");
        Assertions.assertEquals(str25, "");
        Assertions.assertNotNull(wildcardClass26);
    }

    @Test
    public void test23() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test23");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray4 = new String[]{};
        String str5 = wcApplication0.countFromFiles(false, false, false, strArray4);
        Assertions.assertNotNull(strArray4);
        Assertions.assertEquals(str5, "");
    }

    @Test
    public void test24() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "WcApplicationRandoopTest.test24");
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication0 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication4 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray8 = new String[]{};
        String str9 = wcApplication4.countFromFiles(false, false, false, strArray8);
        String str10 = wcApplication0.countFromFiles(true, false, false, strArray8);
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication14 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        sg.edu.nus.comp.cs4218.impl.app.WcApplication wcApplication18 = new sg.edu.nus.comp.cs4218.impl.app.WcApplication();
        String[] strArray22 = new String[]{};
        String str23 = wcApplication18.countFromFiles(false, false, false, strArray22);
        String str24 = wcApplication14.countFromFiles(true, false, false, strArray22);
        String str25 = wcApplication0.countFromFiles(true, false, false, strArray22);
        Class<?> wildcardClass26 = wcApplication0.getClass();
        Assertions.assertNotNull(strArray8);
        Assertions.assertEquals(str9, "");
        Assertions.assertEquals(str10, "");
        Assertions.assertNotNull(strArray22);
        Assertions.assertEquals(str23, "");
        Assertions.assertEquals(str24, "");
        Assertions.assertEquals(str25, "");
        Assertions.assertNotNull(wildcardClass26);
    }
}
