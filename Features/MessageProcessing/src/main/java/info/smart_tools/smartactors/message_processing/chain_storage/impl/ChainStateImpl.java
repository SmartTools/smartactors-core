package info.smart_tools.smartactors.message_processing.chain_storage.impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_storage.interfaces.IChainState;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainModificationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class ChainStateImpl implements IChainState {
    /**
     *
     */
    private class Modification {
        private final IObject description;
        private final IReceiverChain result;

        Modification(final IObject description, final IReceiverChain result) {
            this.description = description;
            this.result = result;
        }

        IObject getDescription() {
            return description;
        }

        IReceiverChain getResult() {
            return result;
        }
    }

    private IReceiverChain current;
    private LinkedHashMap<Object, Modification> modifications = new LinkedHashMap<>();

    private final IFieldName modificationFN;

    /**
     * The constructor.
     *
     * @param initial    the initial chain
     * @throws ResolutionException if error occurs resolving any dependencies
     * @throws InvalidArgumentException if initial chain is null
     */
    public ChainStateImpl(final IReceiverChain initial) throws ResolutionException, InvalidArgumentException {
        if (null == initial) {
            throw new InvalidArgumentException("Initial chain should not be null.");
        }

        this.current = initial;
        this.modifications.put(initial, new Modification(null, initial));

        modificationFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "modification");
    }

    private IReceiverChain applyModification(final IReceiverChain chain, final IObject modification)
            throws ResolutionException, ReadValueException, InvalidArgumentException {
        return IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), modification.getValue(modificationFN)),
                chain, modification
        );
    }

    @Override
    public Object update(final IObject modification) throws ChainModificationException {
        try {
            Object modId = new Object();

            IReceiverChain modified = applyModification(current, modification);

            modifications.put(modId, new Modification(modification, modified));
            current = modified;

            return modId;
        } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
            throw new ChainModificationException(e);
        }
    }

    @Override
    public void rollback(final Object modId) throws ChainModificationException {
        try {
            Modification rbMod = modifications.get(modId);
            Modification prevMod = null;
            LinkedHashMap<Object, Modification> newModifications = new LinkedHashMap<>();

            if (null == rbMod) {
                throw new ChainModificationException("Invalid modification id.");
            }

            Iterator<Map.Entry<Object, Modification>> modIter = modifications.entrySet().iterator();

            while (modIter.hasNext()) {
                Map.Entry<Object, Modification> modEntry = modIter.next();

                if (modEntry.getValue() == rbMod) {
                    break;
                }

                prevMod = modEntry.getValue();

                newModifications.put(modEntry.getKey(), modEntry.getValue());
            }

            while (modIter.hasNext()) {
                Map.Entry<Object, Modification> modEntry = modIter.next();

                prevMod = new Modification(
                        modEntry.getValue().getDescription(),
                        applyModification(prevMod.getResult(), modEntry.getValue().getDescription())
                );

                newModifications.put(modEntry.getKey(), prevMod);
            }

            current = prevMod.getResult();
            modifications = newModifications;
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new ChainModificationException(e);
        }
    }

    @Override
    public IReceiverChain getCurrent() {
        return current;
    }
}
