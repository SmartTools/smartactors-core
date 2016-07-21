package info.smart_tools.smartactors.core.examples;

/**
 *  Just a sample class with internal ID provided in constructor and correct equals() and hashCode() methods.
 */
public class SampleClass {

    private final String id;

    /**
     * Creates the class with specified id.
     * @param id id for this instance of the class
     */
    public SampleClass(final String id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SampleClass that = (SampleClass) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MyClass{" +
                "id='" + id + '\'' +
                '}';
    }

}
