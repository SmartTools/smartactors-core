package info.smart_tools.smartactors.core.wrapper_generator;

import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.wrapper_generator.Field;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.wrapper_generator.IWrapper;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import java.lang.Integer;
import info.smart_tools.smartactors.core.wrapper_generator.TestClass;
import info.smart_tools.smartactors.core.wrapper_generator.IInnerWrapper;
import java.util.List;

public class IWrapperImpl implements IObjectWrapper, IWrapper {
    private IField fieldFor_out_setTestClassValue;
    private IField fieldFor_in_getListOfTestClasses;
    private IField fieldFor_out_setListOfTestClasses;
    private IField fieldFor_in_getIntValue;
    private IField fieldFor_out_setIntValue;
    private IField fieldFor_out_transform;
    private IField fieldFor_in_getTestClassValue;
    private IField fieldFor_in_wrappedIObject;
    private IField fieldFor_out_wrappedIObject;
    private IObject env;

    public IWrapperImpl() throws InvalidArgumentException  {
        try {
            this.fieldFor_out_setTestClassValue = new Field(new FieldName("out_setTestClassValue"));
            this.fieldFor_in_getListOfTestClasses = new Field(new FieldName("in_getListOfTestClasses"));
            this.fieldFor_out_setListOfTestClasses = new Field(new FieldName("out_setListOfTestClasses"));
            this.fieldFor_in_getIntValue = new Field(new FieldName("in_getIntValue"));
            this.fieldFor_out_setIntValue = new Field(new FieldName("out_setIntValue"));
            this.fieldFor_out_transform = new Field(new FieldName("out_transform"));
            this.fieldFor_in_getTestClassValue = new Field(new FieldName("in_getTestClassValue"));
            this.fieldFor_in_wrappedIObject = new Field(new FieldName("in_wrappedIObject"));
            this.fieldFor_out_wrappedIObject = new Field(new FieldName("out_wrappedIObject"));
        } catch (Exception e) {
            throw new InvalidArgumentException("", e);
        }

    }

    public void init(IObject environments)  {
        this.env = environments;

    }

    public IObject getEnvironmentIObject(IFieldName fieldName) throws InvalidArgumentException  {
        try {
            return (IObject) this.env.getValue(fieldName);
        } catch (Throwable e) {
            throw new InvalidArgumentException("Could not get IObject from environments.", e);
        }

    }

    public void setTestClassValue(info.smart_tools.smartactors.core.wrapper_generator.TestClass value) throws ChangeValueException  {
        try {
            this.fieldFor_out_setTestClassValue.out(this.env, value);
        } catch (Throwable e) {
            throw new ChangeValueException("Could not set value from iobject.", e);
        }

    }

    public java.util.List<info.smart_tools.smartactors.core.wrapper_generator.TestClass> getListOfTestClasses() throws ReadValueException  {
        try {
            return fieldFor_in_getListOfTestClasses.in(this.env);
        } catch(Throwable e) {
            throw new ReadValueException("Could not get value from iobject.", e);
        }

    }

    public void setListOfTestClasses(java.util.List<info.smart_tools.smartactors.core.wrapper_generator.TestClass> value) throws ChangeValueException  {
        try {
            this.fieldFor_out_setListOfTestClasses.out(this.env, value);
        } catch (Throwable e) {
            throw new ChangeValueException("Could not set value from iobject.", e);
        }

    }

    public java.lang.Integer getIntValue() throws ReadValueException  {
        try {
            return fieldFor_in_getIntValue.in(this.env);
        } catch(Throwable e) {
            throw new ReadValueException("Could not get value from iobject.", e);
        }

    }

    public void setIntValue(int value) throws ChangeValueException  {
        try {
            this.fieldFor_out_setIntValue.out(this.env, value);
        } catch (Throwable e) {
            throw new ChangeValueException("Could not set value from iobject.", e);
        }

    }

    public void transform(java.lang.Integer value) throws ChangeValueException  {
        try {
            this.fieldFor_out_transform.out(this.env, value);
        } catch (Throwable e) {
            throw new ChangeValueException("Could not set value from iobject.", e);
        }

    }

    public info.smart_tools.smartactors.core.wrapper_generator.TestClass getTestClassValue() throws ReadValueException  {
        try {
            return fieldFor_in_getTestClassValue.in(this.env);
        } catch(Throwable e) {
            throw new ReadValueException("Could not get value from iobject.", e);
        }

    }

    public info.smart_tools.smartactors.core.wrapper_generator.IInnerWrapper wrappedIObject() throws ReadValueException  {
        try {
            return fieldFor_in_wrappedIObject.in(this.env);
        } catch(Throwable e) {
            throw new ReadValueException("Could not get value from iobject.", e);
        }

    }

    public void wrappedIObject(info.smart_tools.smartactors.core.wrapper_generator.IInnerWrapper value) throws ChangeValueException  {
        try {
            this.fieldFor_out_wrappedIObject.out(this.env, value);
        } catch (Throwable e) {
            throw new ChangeValueException("Could not set value from iobject.", e);
        }

    }

}