/**
 * Implementation of {@link info.smart_tools.smartactors.core.iscope.IScope}
 *
 * <pre>
 * This implementation has follow specific features:
 * - kay-value storage;
 * - pointer to parent scope;
 * - recursive call to getValue method of parent scope if current scope hasn't contain value by given key.
 * </pre>
 */
package info.smart_tools.smartactors.core.recursive_scope;