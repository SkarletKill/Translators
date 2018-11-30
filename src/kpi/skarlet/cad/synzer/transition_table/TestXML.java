package kpi.skarlet.cad.synzer.transition_table;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestXML {
    private final String dataPath = "res/transition_table.xml";
    private Map<Integer, State> states;

    private final String tagState = "state";
    private final String tagTransitions = "transitions";
    private final String tagTransition = "transition";
    private final String tagStack = "stack";
    private final String tagGoto = "goto";
    private final String tagComparability = "comparability";
    private final String tagIncomparability = "incompatibility";

    public static void main(String[] args) {
        TestXML testXML = new TestXML();
        testXML.transitionTable();
    }

    private void transitionTable() {
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(dataPath);

            // Получаем корневой элемент
            Node root = document.getDocumentElement();

            show(root.getNodeName(), "", 0);
            System.out.println();
            NodeList stateList = root.getChildNodes();
            // -> states
            for (int i = 0; i < stateList.getLength(); i++) {
                Node state = stateList.item(i);
                // -> state
                if (state.getNodeName().equals(tagState)) {
                    handleState(state);
                }
            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

    }

    private void handleState(Node state) throws AttributeNotFoundException {
        // проверка атрибута 'name'
        checkForAttribute(state, "name", 0);

        NodeList stateProps = state.getChildNodes();
        // -> подтеги state
        for (int j = 0; j < stateProps.getLength(); j++) {
            Node stateProp = stateProps.item(j);
            // -> transitions
            if (stateProp.getNodeName().equals(tagTransitions)) {
                show(stateProp.getNodeName(), "", 1);
                NodeList transitions = stateProp.getChildNodes();
                for (int k = 0; k < transitions.getLength(); k++) {
                    Node transition = transitions.item(k);
                    // -> transition
                    if (transition.getNodeName().equals(tagTransition)) {
                        handleTransition(transition);
                    } // end transition
                }
            } // end transitions
            nodeCheckPrint(stateProp, tagIncomparability, 1);
        }
        System.out.println("===========>>>>");
    }

    private void handleTransition(Node transition) throws AttributeNotFoundException {
        // проверка атрибута 'label'
        checkForAttribute(transition, "label", 2);

        NodeList transProps = transition.getChildNodes();
        for (int l = 0; l < transProps.getLength(); l++) {
            Node transProp = transProps.item(l);
            // -> stack
            nodeCheckPrint(transProp, tagStack, 3);
            // -> goto
            nodeCheckPrint(transProp, tagGoto, 3);
            // -> comparability
            nodeCheckPrint(transProp, tagComparability, 3);
        } // end transition props
    }

    private void checkForAttribute(Node tagNode, String attr, int indent) throws AttributeNotFoundException {
        Node attribute = tagNode.getAttributes().getNamedItem(attr);
        if (attribute != null) {
            show(tagNode.getNodeName(), null, indent, attribute.getNodeName(), attribute.getTextContent());
        } else {
            throw new AttributeNotFoundException(tagNode.getNodeName(), attr);
        }
    }

    private void nodeCheckPrint(Node node, String field, int indent) {
        if (node.getNodeName().equals(field)) {
            Node text = node.getChildNodes().item(0);
            show(node.getNodeName(), (text != null) ? text.getTextContent() : "null", indent);
        }
    }

    private void show(String tag, String text, int layer) {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < layer; i++) space.append("\t");
        System.out.println(space + tag + ": " + text);
    }

    private void show(String tag, String text, int layer, String attrName, String attrValue) {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < layer; i++) space.append("\t");
        space.append(tag)
                .append("[")
                .append(attrName)
                .append("=\"")
                .append(attrValue)
                .append("\"]")
                .append(": ");
        if (text != null)
            space.append(text);
        System.out.println(space);
    }

}