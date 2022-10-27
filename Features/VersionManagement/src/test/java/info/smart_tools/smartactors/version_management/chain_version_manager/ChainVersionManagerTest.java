package info.smart_tools.smartactors.version_management.chain_version_manager;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.class_management.module_manager.exception.ModuleManagerException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for SmartactorsClassLoader
 */
public class ChainVersionManagerTest {

    @Test
    public void checkModuleCreation()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        assertNotNull(ModuleManager.getCurrentModule());
        ModuleManager.addModule("cl1", "cl1", null);
        assertNotNull(ModuleManager.getModuleById("cl1").getClassLoader());
        ModuleManager.addModule("cl2", "cl2", null);
        assertNotNull(ModuleManager.getModuleById("cl2").getClassLoader());
        assertSame(((ClassLoader) ModuleManager.getModuleById("cl2").getClassLoader()).getParent(), ClassLoader.getSystemClassLoader());
        ModuleManager.addModuleDependency("cl1", "cl2");
        ModuleManager.finalizeModuleDependencies("cl1");
        ModuleManager.finalizeModuleDependencies("cl2");

        try {
            ModuleManager.addModule(ModuleManager.coreId, null, null);
            fail();
        } catch(InvalidArgumentException e) { }

        try {
            ModuleManager.addModule(ModuleManager.coreId, "core", "version");
            fail();
        } catch(ModuleManagerException e) { }
    }

    @Test
    public void checkAdditionNewURL()
            throws Exception {
    }

    @Test
    public void checkGettersSetters()
            throws Exception {
    }
}