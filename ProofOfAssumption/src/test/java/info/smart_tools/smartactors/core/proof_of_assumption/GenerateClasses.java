package info.smart_tools.smartactors.core.proof_of_assumption;

import info.smart_tools.smartactors.core.iobject.FieldName;
import info.smart_tools.smartactors.core.iobject.IFieldName;
import info.smart_tools.smartactors.core.iobject.IObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class GenerateClasses {

    //@Test
    public void ByteBuddyClassGenerator()
            throws Exception {


        IFieldName fieldName = new FieldName("field");
        Class<?> cl = new ByteBuddy()
                .subclass(Object.class)
                .defineField("value", String.class).modifiers(Modifier.PRIVATE)
//                .method(not(isDeclaredBy(Object.class)))
//                .intercept(MethodDelegation.toInstanceField(String.class, "value"))
                .implement(IWrapper.class).intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        IObject obj = (IObject) cl.newInstance();
        Object value = new Object();
        obj.setValue(fieldName, value);
        Object result = obj.getValue(fieldName);

        Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .method(named("toString")).intercept(FixedValue.value("Hello World!"))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        assertThat(dynamicType.newInstance().toString(), is("Hello World!"));
    }
}

