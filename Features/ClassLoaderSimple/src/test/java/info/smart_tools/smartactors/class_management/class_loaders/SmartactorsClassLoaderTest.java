package info.smart_tools.smartactors.class_management.class_loaders;

import info.smart_tools.smartactors.class_management.class_loaders.SmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests for SmartactorsClassLoader
 */
public class SmartactorsClassLoaderTest {

    @Test
    public void checkSmartactorsClassLoaderCreation()
            throws Exception {
        ISmartactorsClassLoader ccl = SmartactorsClassLoader.newInstance("coreName", "coreVersion");
        ccl.setDefault();
        ISmartactorsClassLoader cl1 = SmartactorsClassLoader.newInstance("cl1", null);
        assertNotNull(cl1);
        ISmartactorsClassLoader cl2 = SmartactorsClassLoader.newInstance("cl2", null);
        assertNotNull(cl2);
        assertSame(((ClassLoader)cl2).getParent(), ClassLoader.getSystemClassLoader());
        cl1.addDependency(cl2);
        assertNotNull(ccl.getCompilationClassLoader());
    }

    @Test
    public void checkAdditionNewURL()
            throws Exception {
        URL url1 = new URL("http", "host", 9000, "filename1");
        URL url2 = new URL("http", "host", 9000, "filename2");
        URL url3 = new URL("http", "host", 9000, "filename1");
        ISmartactorsClassLoader cl0 = SmartactorsClassLoader.newInstance("cl0", "cl0");
        assertNotNull(cl0);
        cl0.setDefault();
        ISmartactorsClassLoader cl = SmartactorsClassLoader.newInstance("clURL", "clURL");
        assertNotNull(cl);
        cl.addDependency(cl0);
        cl0.addURL(url1);
        cl.addURL(url1);
        cl.addURL(url2);
        cl.addURL(url3);
        URL[] result = cl.getURLsFromDependencies();
        ArrayList<URL> al = new ArrayList<URL>();
        for(URL url : result) {
            al.add(url);
        }
        if( !al.contains(url1)) {
            fail();
        }
        if( !al.contains(url2)) {
            fail();
        }

        try {
            Class clazz = cl.loadClass("nowhere.classNotExist");
            fail();
        } catch (ClassNotFoundException e) {
        }

        Class clazz = cl.loadClass("java.lang.String");
    }

    @Test
    public void checkGettersSetters() {

        ISmartactorsClassLoader cl = SmartactorsClassLoader.newInstance("cl", null);

        try {
            byte[] byteCode = { 1, 2 };
            Class clazz = cl.addClass("newClass", byteCode);
            fail();
        } catch (ClassFormatError e) { }
    }

    private void attachJarToClassLoader(String jarName, ISmartactorsClassLoader cl)
            throws Exception {
        String pathToJar = this.getClass().getClassLoader().getResource(jarName).getFile();
        cl.addURL(new URL("jar:file:" + pathToJar + "!/"));
    }

    private ClassLoader createClassLoaderForJar(String jarName, ClassLoader parent)
            throws Exception {
        URLClassLoader cl;
        String pathToJar = this.getClass().getClassLoader().getResource(jarName).getFile();
        URL url = new URL("jar:file:" + pathToJar + "!/");
        URL[] urLs = {url};
        cl = new URLClassLoader(urLs, parent);
        return cl;
    }

    private ClassLoader[] createClassLoaderSet()
            throws Exception {
        ISmartactorsClassLoader cls[] = new SmartactorsClassLoader[22];
        cls[0] = SmartactorsClassLoader.newInstance("CL0", "CL0");
        cls[1] = SmartactorsClassLoader.newInstance("CL1", "CL1");
        cls[2] = SmartactorsClassLoader.newInstance("CL2", "CL2");
        cls[3] = SmartactorsClassLoader.newInstance("CL3", "CL3");
        cls[4] = SmartactorsClassLoader.newInstance("CL4", "CL4");
        cls[5] = SmartactorsClassLoader.newInstance("CL5", "CL5");
        cls[6] = SmartactorsClassLoader.newInstance("CL6", "CL6");
        cls[7] = SmartactorsClassLoader.newInstance("CL7", "CL7");
        cls[8] = SmartactorsClassLoader.newInstance("CL8", "CL8");
        cls[9] = SmartactorsClassLoader.newInstance("CL9", "CL9");

        cls[9].addDependency(cls[8]);

        cls[3].addDependency(cls[0]);
        cls[3].addDependency(cls[1]);
        cls[4].addDependency(cls[1]);
        cls[4].addDependency(cls[2]);
        cls[5].addDependency(cls[3]);
        cls[5].addDependency(cls[4]);
        cls[6].addDependency(cls[5]);
        cls[7].addDependency(cls[6]);


        attachJarToClassLoader("ifield_name.jar", (ISmartactorsClassLoader)cls[2]);
        attachJarToClassLoader("iobject.jar", (ISmartactorsClassLoader)cls[2]);
        attachJarToClassLoader("ifield.jar", (ISmartactorsClassLoader)cls[0]);
        attachJarToClassLoader("ifield.jar", (ISmartactorsClassLoader)cls[1]);
        attachJarToClassLoader("iobject-wrapper.jar", (ISmartactorsClassLoader)cls[4]);

        attachJarToClassLoader("ifield_name.jar", (ISmartactorsClassLoader)cls[8]);

        return (ClassLoader[])cls;
    }

    @Test
    public void performanceTestForExtendedClassLoader()
            throws Exception {
        ClassLoader[] cls;

        Class[] clazz = { null, null, null, null, null };
        long iterations, t1, t2, t3, t4;
        double d, a, u, d1, d2;
        d = 0; a = 0; u = 0; d1 = 0.0; d2 = 0.0;
        iterations = 1000;
        for(int i=0; i<iterations+5; i++)
        {
            cls = createClassLoaderSet();

            t1 = System.nanoTime();
            clazz[0] = cls[9].loadClass("info.smart_tools.smartactors.core.ifield_name.IFieldName");
            t2 = System.nanoTime();
            clazz[1] = cls[7].loadClass("info.smart_tools.smartactors.core.ifield_name.IFieldName");
            t3 = System.nanoTime();
            if (i>4) {
                d += t2 - t1;
                a += t3 - t2;
            }
        }
        d /= iterations;
        a /= iterations;
        System.out.println("");
        System.out.println("Average direct line class loading (ns): "+d);
        System.out.println("Average around line class loading (ns): "+a);

        cls = createClassLoaderSet();

        ClassLoader cls8 =  createClassLoaderForJar("ifield.jar", ClassLoader.getSystemClassLoader());
        ClassLoader cls9 =  createClassLoaderForJar("ifield_name.jar", cls8);
        ClassLoader cls10 = createClassLoaderForJar("iobject.jar", cls9);
        ClassLoader cls11 = createClassLoaderForJar("iobject-wrapper.jar", cls10);
        ClassLoader cls12 = new URLClassLoader(new URL[]{}, cls11);
        ClassLoader cls13 = new URLClassLoader(new URL[]{}, cls12);
        ClassLoader cls14 = new URLClassLoader(new URL[]{}, cls13);
        ClassLoader cls15 = new URLClassLoader(new URL[]{}, cls14);
        ClassLoader cls16 = new URLClassLoader(new URL[]{}, cls15);
        ClassLoader cls17 = new URLClassLoader(new URL[]{}, cls16);

        clazz[0] = cls[3].loadClass("info.smart_tools.smartactors.iobject.ifield.IField");
        clazz[1] = cls[4].loadClass("info.smart_tools.smartactors.iobject.iobject.IObject");
        clazz[2] = cls[4].loadClass("info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper");
        clazz[3] = cls[7].loadClass("info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper");
        clazz[4] = cls15 .loadClass("info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper");
        d = 0; a = 0; u = 0;
        iterations = 100000;
        for(int i=0; i<iterations+5; i++)
        {
            t1 = System.nanoTime();
            clazz[1] = cls[4].loadClass("info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper");
            t2 = System.nanoTime();
            clazz[0] = cls[7].loadClass("info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper");
            t3 = System.nanoTime();
            clazz[2] = cls17 .loadClass("info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper");
            t4 = System.nanoTime();
            if (i>4) {
                d += t2 - t1;
                a += t3 - t2;
                u += t4 - t3;
            }
        }
        d /= iterations;
        a /= iterations;
        u /= iterations;
        System.out.println("Average direct line class reading (ns): "+d);
        System.out.println("Average around line class reading (ns): "+a);
        System.out.println("Average descendant line class reading (ns): "+u);
    }
}