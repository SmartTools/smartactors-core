package info.smart_tools.smartactors.testing.test_report_xml_builder;

import javax.xml.bind.annotation.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "testsuite")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
final class TestSuiteModel {
    private String name;
    private Integer tests;
    private Integer skipped;
    private Integer errors;
    private Integer failures;
    private Long timestamp;
    private Long time;
    private List<TestCaseModel> testCaseModels = new ArrayList<>();

    private final DateFormat format = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    @XmlAttribute(name = "tests")
    public Integer getTests() {
        return tests;
    }

    @XmlAttribute(name = "skipped")
    public Integer getSkipped() {
        return skipped;
    }

    @XmlAttribute(name = "errors")
    public Integer getErrors() {
        return errors;
    }

    @XmlAttribute(name = "failures")
    public Integer getFailures() {
        return failures;
    }

    @XmlAttribute(name = "timestamp")
    public String getTimestamp() {
        if (timestamp == null) return null;
        return format.format(new Date(timestamp));
    }

    @XmlAttribute(name = "time")
    public Long getTime() {
        return time;
    }

    @XmlElement(name = "testcase")
    public List<TestCaseModel> getTestCaseModels() {
        return testCaseModels;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setTests(final Integer tests) {
        this.tests = tests;
    }

    public void setSkipped(final Integer skipped) {
        this.skipped = skipped;
    }

    public void setErrors(final Integer errors) {
        this.errors = errors;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTime(final Long time) {
        this.time = time;
    }

    public void setTestCaseModels(final List<TestCaseModel> testCaseModels) {
        this.testCaseModels = testCaseModels;
    }

    public void setFailures(final Integer failures) {
        this.failures = failures;
    }
}
