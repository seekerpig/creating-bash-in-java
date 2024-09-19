package cs4218.impl.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD")
public class TeeApplicationRandoopTest {

    public static boolean debug = false;

    @Test
    public void test01() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test01");
        java.lang.Object obj0 = new java.lang.Object();
        java.lang.Class<?> wildcardClass1 = obj0.getClass();
        Assertions.assertNotNull(wildcardClass1);
    }

    @Test
    public void test02() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test02");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray3 = new java.lang.String[]{};
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) true, inputStream2, strArray3);
        });
        Assertions.assertNotNull(strArray3);
    }

    @Test
    public void test03() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test03");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.Class<?> wildcardClass1 = teeApplication0.getClass();
        Assertions.assertNotNull(wildcardClass1);
    }

    @Test
    public void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test04");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray5 = new java.lang.String[]{"", "hi!"};
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) true, inputStream2, strArray5);
        });
        Assertions.assertNotNull(strArray5);
    }

    @Test
    public void test05() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test05");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray3 = null;
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) true, inputStream2, strArray3);
        });
    }

    @Test
    public void test06() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test06");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.String[] strArray2 = new java.lang.String[]{"hi!"};
        java.io.InputStream inputStream3 = null;
        java.io.OutputStream outputStream4 = null;
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.run(strArray2, inputStream3, outputStream4);
        });
        Assertions.assertNotNull(strArray2);
    }

    @Test
    public void test07() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test07");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray8 = new java.lang.String[]{"hi!", "hi!", "hi!", "", ""};
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) true, inputStream2, strArray8);
        });
        Assertions.assertNotNull(strArray8);
    }

    @Test
    public void test08() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test08");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.String[] strArray7 = new java.lang.String[]{"hi!", "", "hi!", "", "hi!", "hi!"};
        java.io.InputStream inputStream8 = null;
        java.io.OutputStream outputStream9 = null;
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.run(strArray7, inputStream8, outputStream9);
        });
        Assertions.assertNotNull(strArray7);
    }

    @Test
    public void test09() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test09");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.String[] strArray4 = new java.lang.String[]{"", "", ""};
        java.io.InputStream inputStream5 = null;
        java.io.OutputStream outputStream6 = null;
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.run(strArray4, inputStream5, outputStream6);
        });
        Assertions.assertNotNull(strArray4);
    }

    @Test
    public void test10() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test10");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray9 = new java.lang.String[]{"hi!", "hi!", "", "hi!", "", ""};
        // The following exception was thrown during execution in test generation
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) false, inputStream2, strArray9);
        });
        Assertions.assertNotNull(strArray9);
    }

    @Test
    public void test11() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test11");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray8 = new java.lang.String[]{"hi!", "", "hi!", "hi!", "hi!"};
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) false, inputStream2, strArray8);
        });
        Assertions.assertNotNull(strArray8);
    }

    @Test
    public void test12() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test12");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.String[] strArray5 = new java.lang.String[]{"hi!", "", "", ""};
        java.io.InputStream inputStream6 = null;
        java.io.OutputStream outputStream7 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.run(strArray5, inputStream6, outputStream7);
        });
        Assertions.assertNotNull(strArray5);
    }

    @Test
    public void test13() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test13");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray9 = new java.lang.String[]{"hi!", "hi!", "", "", "", ""};
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) false, inputStream2, strArray9);
        });
        Assertions.assertNotNull(strArray9);
    }

    @Test
    public void test14() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test14");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.String[] strArray2 = new java.lang.String[]{""};
        java.io.InputStream inputStream3 = null;
        java.io.OutputStream outputStream4 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.run(strArray2, inputStream3, outputStream4);
        });
        Assertions.assertNotNull(strArray2);
    }

    @Test
    public void test15() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test15");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray7 = new java.lang.String[]{"", "hi!", "hi!", ""};
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) false, inputStream2, strArray7);
        });
        Assertions.assertNotNull(strArray7);
    }

    @Test
    public void test16() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test16");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray6 = new java.lang.String[]{"", "", ""};
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) true, inputStream2, strArray6);
        });
        Assertions.assertNotNull(strArray6);
    }

    @Test
    public void test17() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test17");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.String[] strArray5 = new java.lang.String[]{"hi!", "", "", "hi!"};
        java.io.InputStream inputStream6 = null;
        java.io.OutputStream outputStream7 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.run(strArray5, inputStream6, outputStream7);
        });
        Assertions.assertNotNull(strArray5);
    }

    @Test
    public void test18() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test18");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray9 = new java.lang.String[]{"hi!", "", "", "hi!", "hi!", "hi!"};
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) false, inputStream2, strArray9);
        });
        Assertions.assertNotNull(strArray9);
    }

    @Test
    public void test19() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test19");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.io.InputStream inputStream2 = null;
        java.lang.String[] strArray3 = new java.lang.String[]{};
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.teeFromStdin((java.lang.Boolean) false, inputStream2, strArray3);
        });
        Assertions.assertNotNull(strArray3);
    }

    @Test
    public void test20() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "TeeApplicationRandoopTest.test20");
        sg.edu.nus.comp.cs4218.impl.app.TeeApplication teeApplication0 = new sg.edu.nus.comp.cs4218.impl.app.TeeApplication();
        java.lang.String[] strArray4 = new java.lang.String[]{"hi!", "hi!", ""};
        java.io.InputStream inputStream5 = null;
        java.io.OutputStream outputStream6 = null;
        Assertions.assertThrows(sg.edu.nus.comp.cs4218.exception.TeeException.class, () -> {
            teeApplication0.run(strArray4, inputStream5, outputStream6);
        });
        Assertions.assertNotNull(strArray4);
    }
}
