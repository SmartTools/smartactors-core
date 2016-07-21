package utils;

import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

import java.util.*;

import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class SearchQueryHelper {

    public static final String A_SINGLE_PARAM_NAME = "aSingleParam"; //$lt
    public static final String B_SINGLE_PARAM_NAME = "bSingleParam"; //$lte
    public static final String C_ARRAY_PARAM_NAME = "cArrayParam"; //$in
    public static final Boolean E_SINGLE_PARAM_VALUE = true; //$isNull

    public static final String expectedResult = "WHERE(" +
            "(((((document#>'{a}')<to_json(?)::jsonb))" +
            "AND((document#>'{e}') is null))" +
            "OR((((document#>'{b}')<=to_json(?)::jsonb))" +
            "AND(((document#>'{c}')in(to_json(?)::jsonb,to_json(?)::jsonb,to_json(?)::jsonb))))))";

    public static IObject createComplexCriteria() {
        IObject criteria = mock(IObject.class);

        IObject firstBlock = mock(IObject.class);
        IObject secondBlock = mock(IObject.class);

        IObject a = mock(IObject.class);
        IObject e = mock(IObject.class);
        IObject b = mock(IObject.class);
        IObject c = mock(IObject.class);

        IFieldName aFN = mock(IFieldName.class);
        IFieldName eFN = mock(IFieldName.class);
        IFieldName bFN = mock(IFieldName.class);
        IFieldName cFN = mock(IFieldName.class);

        when(aFN.toString()).thenReturn("a");
        when(eFN.toString()).thenReturn("e");
        when(bFN.toString()).thenReturn("b");
        when(cFN.toString()).thenReturn("c");

        IFieldName orFN = mock(IFieldName.class);
        IFieldName ltFN = mock(IFieldName.class);
        IFieldName nullFN = mock(IFieldName.class);
        IFieldName lteFN = mock(IFieldName.class);
        IFieldName inFN = mock(IFieldName.class);

        when(orFN.toString()).thenReturn("$or");
        when(ltFN.toString()).thenReturn("$lt");
        when(nullFN.toString()).thenReturn("$isNull");
        when(lteFN.toString()).thenReturn("$lte");
        when(inFN.toString()).thenReturn("$in");

        List<IObject> or = new ArrayList<>(2);
        or.add(firstBlock);
        or.add(secondBlock);

        Iterator fBIterator = mock(Iterator.class);
        when(fBIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(fBIterator.next()).thenReturn(new MapEntry(aFN, a)).thenReturn(new MapEntry(eFN, e));
        when(firstBlock.iterator()).thenReturn(fBIterator);

        Iterator sBIterator = mock(Iterator.class);
        when(sBIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(sBIterator.next()).thenReturn(new MapEntry(bFN, b)).thenReturn(new MapEntry(cFN, c));
        when(secondBlock.iterator()).thenReturn(sBIterator);

        Iterator aIterator = mock(Iterator.class);
        when(aIterator.next()).thenReturn(new MapEntry(ltFN, A_SINGLE_PARAM_NAME));
        when(aIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(a.iterator()).thenReturn(aIterator);

        Iterator eIterator = mock(Iterator.class);
        when(eIterator.next()).thenReturn(new MapEntry(nullFN, E_SINGLE_PARAM_VALUE));
        when(eIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(e.iterator()).thenReturn(eIterator);

        Iterator bIterator = mock(Iterator.class);
        when(bIterator.next()).thenReturn(new MapEntry(lteFN, B_SINGLE_PARAM_NAME));
        when(bIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(b.iterator()).thenReturn(bIterator);

        Iterator cIterator = mock(Iterator.class);
        when(cIterator.next()).thenReturn(new MapEntry(inFN, Arrays.asList(C_ARRAY_PARAM_NAME, 3)));
        when(cIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(c.iterator()).thenReturn(cIterator);

        Iterator criteriaBIterator = mock(Iterator.class);
        when(criteriaBIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(criteriaBIterator.next()).thenReturn(new MapEntry(orFN, or));
        when(criteria.iterator()).thenReturn(criteriaBIterator);

        return criteria;
    }

    // Map.Entry impl. for mocks.
    private static class MapEntry implements Map.Entry {
        private Object key;
        private Object value;

        public MapEntry(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(Object value) {
            return this.value = value;
        }
    }
}
