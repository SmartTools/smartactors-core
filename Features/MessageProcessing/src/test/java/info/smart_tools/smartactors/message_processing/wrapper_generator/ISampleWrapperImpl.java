//package info.smart_tools.smartactors.message_processing.wrapper_generator;
//
//import info.smart_tools.smartactors.iobject.ifield.IField;
//import info.smart_tools.smartactors.ioc.ioc.IOC;
//import info.smart_tools.smartactors.ioc.key_tools.Keys;
//import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
//import info.smart_tools.smartactors.message_processing.wrapper_generator.ISampleWrapper;
//import info.smart_tools.smartactors.iobject.iobject.IObject;
//import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
//import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
//import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
//import info.smart_tools.smartactors.ioc.exception.ResolutionException;
//import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
//import info.smart_tools.smartactors.iobject.iobject.exception.DeleteValueException;
//import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.HashMap;
//import java.lang.Double;
//
//public class ISampleWrapperImpl implements IObjectWrapper, IObject, ISampleWrapper {
//    private IField fieldFor_in_getDoubleValue;
//    private IField fieldFor_out_setDoubleValue;
//    private Map<IFieldName, IField> fields;
//    private IObject env;
//
//    public ISampleWrapperImpl() throws InvalidArgumentException  {
//        try {
//            this.fieldFor_in_getDoubleValue = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "in_getDoubleValue");
//            this.fieldFor_out_setDoubleValue = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), "out_setDoubleValue");
//            this.fields = new HashMap<>();
//        } catch (Exception e) {
//            throw new InvalidArgumentException("", e);
//        }
//
//    }
//
//    public void init(IObject environments)  {
//        this.env = environments;
//
//    }
//
//    public IObject getEnvironmentIObject(IFieldName fieldName) throws InvalidArgumentException  {
//        try {
//            if (IObjectWrapper.class.isAssignableFrom(this.env.getClass())) {
//                return ((IObjectWrapper) this.env).getEnvironmentIObject(fieldName);
//            }
//            return (IObject) this.env.getValue(fieldName);
//        } catch (Throwable e) {
//            throw new InvalidArgumentException("Could not get IObject from environments.", e);
//        }
//
//    }
//
//    public java.lang.Double getDoubleValue() throws ReadValueException  {
//        try {
//            return fieldFor_in_getDoubleValue.in(this.env, Double.class);
//        } catch(Throwable e) {
//            throw new ReadValueException("Could not get value from iobject.", e);
//        }
//
//    }
//
//    public void setDoubleValue(java.lang.Double value) throws ChangeValueException  {
//        try {
//            this.fieldFor_out_setDoubleValue.out(this.env, value);
//        } catch (Throwable e) {
//            throw new ChangeValueException("Could not set value from iobject.", e);
//        }
//
//    }
//
//    public Object getValue(IFieldName name) throws ReadValueException, InvalidArgumentException  {
//        IField field = fields.get(name);
//        if (null == field) {
//            try {
//                field = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), name);
//                fields.put(name, field);
//            } catch(ResolutionException e) {
//                throw new RuntimeException("Could not resolve dependency for IField");
//            }
//        }
//        try {
//            return ((IField) IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), name)).in(this.env);
//        } catch(ResolutionException e) {
//            throw new RuntimeException("Could not resolve dependency for IField");
//        }
//
//    }
//
//    public void setValue(IFieldName name, Object value) throws ChangeValueException, InvalidArgumentException  {
//        IField field = fields.get(name);
//        if (null == field) {
//            try {
//                field = IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), name);
//                fields.put(name, field);
//            } catch(ResolutionException e) {
//                throw new RuntimeException("Could not resolve dependency for IField");
//            }
//        }
//        try {
//            ((IField) IOC.resolve(Keys.getKeyByName(IField.class.getCanonicalName()), name)).out(env, value);
//        } catch(ResolutionException e) {
//            throw new RuntimeException("Could not resolve dependency for IField");
//        }
//
//    }
//
//    public void deleteField(IFieldName name) throws DeleteValueException, InvalidArgumentException  {
//        throw new DeleteValueException("Method not implemented.");
//
//    }
//
//    public <T> T serialize() throws SerializeException  {
//        throw new SerializeException("Method not implemented.");
//
//    }
//
//    public Iterator<Map.Entry<IFieldName, Object>> iterator()  {
//        return null;
//
//    }
//
//}