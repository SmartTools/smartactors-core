package info.smart_tools.smartactors.core.examples;

/**
 *  Just a sample class with internal ID provided in constructor and correct equals() and hashCode() methods.
 */
public class MyClass {

    private final String id;

    public MyClass(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyClass that = (MyClass) o;
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
