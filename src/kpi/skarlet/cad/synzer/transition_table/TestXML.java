package kpi.skarlet.cad.synzer.transition_table;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TestXML {
    private final String dataPath = "res/transition_table.xml";
    private Map<Integer, State> states;

    private final String tagState = "state";
    private final String tagTransitions = "transitions";
    private final String tagTransition = "transition";
    private final String tagStack = "stack";
    private final String tagGoto = "goto";
    private final String tagComparison = "comparability";
    private final String tagIncompatibility = "incompatibility";

    public static void main(String[] args) {
        TestXML testXML = new TestXML();
//        Integer.parseInt(null);
        testXML.states = testXML.transitionTable();
        System.out.println(testXML.states);
    }

    private Map<Integer, State> transitionTable() {
        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            Document document = documentBuilder.parse(dataPath);

            // Получаем корневой элемент
            Node root = document.getDocumentElement();

            states = new HashMap<>() {
                @Override
                public String toString() {
                    return this.entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + " = " + entry.getValue())
                            .collect(Collectors.joining(",\n ", "{", "}"));
                }
            };

            NodeList stateList = root.getChildNodes();
            // -> states
            for (int i = 0; i < stateList.getLength(); i++) {
                Node state = stateList.item(i);
                // -> state
                if (state.getNodeName().equals(tagState)) {
                    handleState(state);
                }
            }

            return states;

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }

        return null;
    }

    private void handleState(Node state) throws AttributeNotFoundException {
        // проверка атрибута 'name'
        int stateNum = Integer.parseInt(checkForAttribute(state, "name", 0));
        states.put(stateNum, new State(stateNum));

        NodeList stateProps = state.getChildNodes();
        // -> подтеги state
        for (int j = 0; j < stateProps.getLength(); j++) {
            Node stateProp = stateProps.item(j);
            // -> transitions
            if (stateProp.getNodeName().equals(tagTransitions)) {
                NodeList transitions = stateProp.getChildNodes();
                for (int k = 0; k < transitions.getLength(); k++) {
                    Node transition = transitions.item(k);
                    // -> transition
                    if (transition.getNodeName().equals(tagTransition)) {
                        handleTransition(transition, stateNum);
                    } // end transition
                }
            } // end transitions
            if (nodeCheck(stateProp, tagIncompatibility))
                states.get(stateNum).setIncomparability(getNodeContext(stateProp));
        }
//        System.out.println("===========>>>>");
    }

    private void handleTransition(Node transition, int state) throws AttributeNotFoundException {
        // проверка атрибута 'label'
        String label = checkForAttribute(transition, "label", 2);
        Integer stack = null, nextState = null;
        String comparability = null;

        NodeList transProps = transition.getChildNodes();
        for (int l = 0; l < transProps.getLength(); l++) {
            Node transProp = transProps.item(l);
            // -> stack / goto / comparability
            if (nodeCheck(transProp, tagStack)) {
                String nodeContext = getNodeContext(transProp);
                stack = (nodeContext != null) ? Integer.parseInt(nodeContext) : null;
            } else if (nodeCheck(transProp, tagGoto)) {
                String nodeContext = getNodeContext(transProp);
                nextState = (nodeContext != null) ? Integer.parseInt(nodeContext) : null;
            } else if (nodeCheck(transProp, tagComparison)) {
                comparability = getNodeContext(transProp);
            }
        } // end transition props

        states.get(state).add(label, new TransitionElems(stack, nextState, comparability));
    }

    private String checkForAttribute(Node tagNode, String attr, int indent) throws AttributeNotFoundException {
        Node attribute = tagNode.getAttributes().getNamedItem(attr);
        if (attribute != null) {
            return attribute.getTextContent();
        } else {
            throw new AttributeNotFoundException(tagNode.getNodeName(), attr);
        }
    }

    private boolean nodeCheck(Node node, String field) {
        return node.getNodeName().equals(field);
    }

    private String getNodeContext(Node node) {
        Node text = node.getChildNodes().item(0);
        return (text != null) ? text.getTextContent() : null;
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