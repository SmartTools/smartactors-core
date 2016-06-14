package info.smart_tools.smartactors.core.recursive_scope;

import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope.exception.ScopeException;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class RecursiveScopeTest {

    @Test
    public void checkCreation() {
        IScope scope = new Scope(null);
        assertNotNull(scope);
    }

    @Test
    public void checkCreationWithParent() {
        IScope parent = new Scope(null);
        assertNotNull(parent);
        IScope child = new Scope(parent);
        assertNotNull(child);
    }

    @Test
    public void checkStoringAndGettingValue()
            throws ScopeException {
        IScope scope = new Scope(null);
        Integer number = 1;
        scope.setValue("number", number);
        assertEquals(scope.getValue("number"), number);
    }

    @Test(expected = ScopeException.class)
    public void checkGettingAbsentValue()
            throws ScopeException {
        IScope scope = new Scope(null);
        scope.getValue("number");
    }

    @Test
    public void checkRecursiveLogic()
            throws ScopeException{
        IScope parent = new Scope(null);
        assertNotNull(parent);
        IScope child = new Scope(parent);
        assertNotNull(child);
        Integer number = 1;
        parent.setValue("number", number);
        assertEquals(child.getValue("number"), number);
    }

    @Test(expected = ScopeException.class)
    public void checkValueDeletion()
            throws ScopeException {
        IScope scope = new Scope(null);
        Integer number = 1;
        scope.setValue("number", number);
        assertEquals(scope.getValue("number"), number);
        scope.deleteValue("number");
        scope.getValue("number");
    }

    @Test
    public void checkAbsentValueDeletion()
            throws ScopeException {
        IScope scope = new Scope(null);
        scope.deleteValue("number");
    }

    @Test (expected = ScopeException.class)
    public void checkScopeExceptionOnSet()
            throws ScopeException, NoSuchFieldException, IllegalAccessException {
        IScope scope = new Scope(null);
        scope.setValue(null, "some value");
        fail();
    }

    @Test
    public void checkEqualKeysUsage()
            throws ScopeException {
        IScope scope = new Scope(null);
        Integer number1 = 1;
        Integer number2 = 2;
        scope.setValue("number", number1);
        scope.setValue("number", number2);
        assertEquals(scope.getValue("number"), number2);
    }

    @Test (expected = ScopeException.class)
    public void checkScopeExceptionOnDelete()
            throws ScopeException, NoSuchFieldException, IllegalAccessException {
        IScope scope = new Scope(null);
        Field f = scope.getClass().getDeclaredField("storage");
        f.setAccessible(true);
        f.set(scope, null);
        scope.deleteValue("some key");
    }
}
