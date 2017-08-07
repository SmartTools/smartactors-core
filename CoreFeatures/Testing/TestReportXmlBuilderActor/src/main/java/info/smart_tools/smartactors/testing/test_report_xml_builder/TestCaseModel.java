package info.smart_tools.smartactors.testing.test_report_xml_builder;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "testcase")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
final class TestCaseModel {
    private String name;
    private String className;
    private Long time;
    private TestFailureModel failure;

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    @XmlAttribute(name = "time")
    public Long getTime() {
        return time;
    }

    @XmlAttribute(name = "classname")
    public String getClassName() {
        return className;
    }

    @XmlElement(name = "failure")
    public TestFailureModel getFailure() {
        return failure;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public void setTime(final Long time) {
        this.time = time;
    }

    public void setFailure(final TestFailureModel failure) {
        this.failure = failure;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
