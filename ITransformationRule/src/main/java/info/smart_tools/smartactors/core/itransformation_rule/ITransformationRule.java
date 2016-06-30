package info.smart_tools.smartactors.core.itransformation_rule;

import info.smart_tools.smartactors.core.itransformation_rule.exception.TransformException;

/**
 * Interface for transformation rules
 */
public interface ITransformationRule {
    /**
     * transformation for iobject wrapper
     * @param source the source field
     * @return object
     * @throws TransformException
     */
    Object transform(Object source) throws TransformException;
}
