package info.smart_tools.smartactors.class_management.interfaces.module_able;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;

/**
 * Interface for objects which having own module
 */
public interface IModuleAble {

    /**
     * Get module which chain belongs to
     * @return the module which chain belongs to
     */
    IModule getModule();
}
