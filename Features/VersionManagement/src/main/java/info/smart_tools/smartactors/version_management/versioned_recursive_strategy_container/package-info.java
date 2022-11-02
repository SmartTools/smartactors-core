/**
 * Implementation of {@link info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer}
 *
 * This implementation has these specific features:
 * <ul>
 *     <li>key-{@link info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy} storage</li>
 *     <li>uses Scope</li>
 *     <li>stores the link to the parent container to make the recursive resolving
 *          when the strategy doesn't exist in the current container</li>
 * </ul>
 */
package info.smart_tools.smartactors.version_management.versioned_recursive_strategy_container;