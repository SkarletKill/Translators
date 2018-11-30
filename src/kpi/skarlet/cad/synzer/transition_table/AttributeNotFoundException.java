package kpi.skarlet.cad.synzer.transition_table;

import java.io.IOException;

public class AttributeNotFoundException extends IOException {
    String tag;
    String attribute;
    String message;

    public AttributeNotFoundException(String tag, String attribute) {
        this.tag = tag;
        this.attribute = attribute;
        this.message = "Tag <" + tag + "> does not have attribute '" + attribute + "'";
    }

    @Override
    public String getMessage() {
        return message;
    }
}