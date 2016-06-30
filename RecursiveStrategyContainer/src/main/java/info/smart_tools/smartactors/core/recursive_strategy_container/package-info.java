/**
 * Implementation of {@link info.smart_tools.smartactors.core.istrategy_container.IStrategyContainer}
 *
 * <p>
 * This implementation has these specific features:
 * <ul>
 *     <li>key-{@link info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy} storage</li>
 *     <li>uses {@link info.smart_tools.smartactors.core.iscope.IScope}</li>
 *     <li>stores the link to the parent container to make the recursive resolving
 *          when the strategy doesn't exist in the current container</li>
 * </ul>
 * </p>
 */
package info.smart_tools.smartactors.core.recursive_strategy_container;