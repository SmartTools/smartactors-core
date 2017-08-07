package info.smart_tools.smartactors.testing.test_report_xml_builder;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "failure")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
final class TestFailureModel {
    private String message;
    private String type;
    private String body;

    @XmlAttribute(name = "message")
    public String getMessage() {
        return message;
    }

    @XmlAttribute(name = "type")
    public String getType() {
        return type;
    }

    @XmlValue
    public String getBody() {
        return body;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setBody(final String body) {
        this.body = body;
    }
}
