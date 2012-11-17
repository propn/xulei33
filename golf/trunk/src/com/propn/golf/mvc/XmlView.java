package com.propn.golf.mvc;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The purpose of this factory is to take the values from XML documents and put them into plain old Java objects using
 * SAX, Reflection, and annotations. This factory can also convert plain old java objects to their XML representations.
 * 
 * @author
 */
public class XmlView {

    /** a map of all of this object's fields by their name */
    private LinkedHashMap<String, Field> _fields;
    /** a map of all of this object's list based object by their name */
    private LinkedHashMap<String, List<Object>> _listMap;
    /** a map of node names to field names for this class and associated XML */
    private LinkedHashMap<String, String> _nodeNameToFieldNameMap;
    /** a map of field names to node names for this class and associated XML */
    private LinkedHashMap<String, String> _fieldNameToNodeNameMap;
    /** a map of attribute names to field names for this class and associated XML */
    private LinkedHashMap<String, String> _attributeNameToFieldNameMap;
    /** represents the name of this class as an XML node */
    private String xmlRootElementName;

    /**
     * Represents a list of warnings encountered during processing of this instance and all instance that are
     * recursively created by this instance
     */
    private static List<String> warnings = new ArrayList<String>();

    /**
     * This can only be called when a factory is needed by this factory so it is considered dependent, and all warnings
     * should be stored so they are not cleared.
     */
    private XmlView() {

    }

    /**
     * Creates a new instance of the factory in order to do some processing, and clears all warnings
     * 
     * @return
     */
    public static XmlView newInstance() {
        warnings.clear();
        return new XmlView();
    }

    /**
     * Returns all warnings encountered while processing using this factory.
     * 
     * @return
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Adds the given text as a warning
     * 
     * @param text
     */
    private static void addWarning(String text) {
        System.err.println(text);
        warnings.add(text);
    }

    /**
     * Uses the given XML String to populate the given object
     * 
     * @param object
     * @param xml
     * @throws SAXParseException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void bind(Object object, String xml) throws SAXParseException, IOException, SAXException,
            ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        this.bind(object, doc);
    }

    /**
     * Uses the given XML file to populate the given object
     * 
     * @param object
     * @param file
     * @throws SAXParseException
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public void bind(Object object, File file) throws SAXParseException, IOException, SAXException,
            ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(file);
        this.bind(object, doc);
    }

    /**
     * Loads the given XML document and uses it to populate this object
     * 
     * @param doc
     */
    public void bind(Object object, Document doc) {
        Node node = doc.getFirstChild();
        this.bind(object, node);
    }

    /**
     * Loads the given XML node and uses it to populate this object
     * 
     * @param node
     */
    public void bind(Object object, Node node) {

        configure(object);

        handleAttributes(object, node);

        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);

            this.processChildNode(object, child);
        }

    }

    /**
     * Configures all of the information about the class fields and annotations
     */
    private void configure(Object object) {
        Class<?> clazz = object.getClass();

        _listMap = new LinkedHashMap<String, List<Object>>();
        _fields = new LinkedHashMap<String, Field>();
        _nodeNameToFieldNameMap = new LinkedHashMap<String, String>();
        _fieldNameToNodeNameMap = new LinkedHashMap<String, String>();
        _attributeNameToFieldNameMap = new LinkedHashMap<String, String>();
        // get class annotations...
        String classXmlNodeName = this.getClassXmlNodeNameFromAnnotation(object);
        if (classXmlNodeName != null) {
            this.setClassXmlNodeName(classXmlNodeName);
        }
        // map all fields...
        Field fields[] = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            _fields.put(name, field);
            Annotation annotations[] = field.getAnnotations();
            for (Annotation ann : annotations) {
                // if the field has an XmlNodeName annotation...
                if (ann instanceof XmlElement) {
                    XmlElement annObject = (XmlElement) ann;
                    _nodeNameToFieldNameMap.put(annObject.name(), name);
                    _fieldNameToNodeNameMap.put(name, annObject.name());
                } else if (ann instanceof XmlAttribute) {
                    XmlAttribute annObject = (XmlAttribute) ann;
                    _attributeNameToFieldNameMap.put(annObject.name(), name);
                }
            }// end for each annotation
        }// end for each field
    }

    /**
     * Returns the class name of the given object.
     * 
     * @param object
     * @return
     */
    private String getClassName(Object object) {
        return object.getClass().getCanonicalName();
    }

    /**
     * Processes the given child node and uses it to populate this object
     * 
     * @param object
     * @param node
     */
    private void processChildNode(Object object, Node node) {

        // only handle element nodes
        if (node.getNodeType() != Node.ELEMENT_NODE)
            return;

        // only mess with the node is it doesn't start with an underscore
        String nodeName = node.getNodeName();

        if (nodeName.startsWith("_"))
            return;

        // if the node has attributes of children it is an object
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            // an element node is an indication that this is an object...
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                this.processObjectChildNode(object, node);
                return;
            }

        }

        // if this node has attributes it must be treated like an object
        if (node.hasAttributes()) {
            this.processObjectChildNode(object, node);
            return;
        }

        // determine the name of the field and if it is a list...
        FieldType type = determineFieldType(node, nodeName);
        String fieldName = type.getFieldName();
        boolean isList = type.isList();

        // if this node is a list of single value objects...
        if (isList) {

            // get the field that is a list
            Field field = _fields.get(fieldName);
            if (field == null) {
                addWarning("The field " + fieldName + " (from XML node " + nodeName + ") could not be found in class "
                        + getClassName(object) + " [processChildNode]");
                return;
            }

            // make sure the field is a list
            if (!isFieldList(field)) {
                addWarning("The field " + fieldName + " in class " + getClassName(object) + " must be a List in order "
                        + "to store multiple values [processChildNode]");
                return;
            }

            // attepmt to get the list from the map
            List<Object> list = getInstantiatedList(object, fieldName, field);
            // get the type of the list
            Class<?> clazz = this.getGenericClass(field);
            String className = clazz.getCanonicalName();
            String value = node.getTextContent();
            if (clazz.equals(String.class))
                list.add(value);
            else if (clazz.equals(Integer.class))
                list.add(Integer.parseInt(value));
            else if (clazz.equals(Short.class))
                list.add(Short.parseShort(value));
            else if (clazz.equals(Float.class))
                list.add(Float.parseFloat(value));
            else if (clazz.equals(Double.class))
                list.add(Double.parseDouble(value));
            else if (clazz.equals(Long.class))
                list.add(Long.parseLong(value));
            else {
                addWarning("" + className + " is an unsupported type for field " + fieldName + " in class "
                        + getClassName(object) + " [processChildNode]");
            }
        } else {
            // find this node using reflection
            Field field = _fields.get(fieldName);
            // set the value
            this.setFieldValueFromNode(object, field, node);
        }
    }

    /**
     * Processes the given child node as an object containing its own attributes and values
     * 
     * @param node
     */
    private void processObjectChildNode(Object object, Node node) {

        String nodeName = node.getNodeName();

        // determine the name of the field and if it is a list...
        FieldType type = determineFieldType(node, nodeName);
        String fieldName = type.getFieldName();
        boolean isList = type.isList();

        // find this node using reflection
        Field field = _fields.get(fieldName);
        if (field == null) {
            addWarning("The field " + fieldName + " from XML node " + nodeName + " could not be found in class "
                    + getClassName(object) + " [processObjectChildNode]");
            return;
        }

        // if this field is a list...
        if (isList) {
            // make sure the field is a list
            if (!isFieldList(field)) {
                addWarning("The field " + fieldName + " in class " + getClassName(object)
                        + " must be a List in order to store multiple values [processObjectNode]");
                return;
            }

            // attempt to get the list from the map
            List<Object> list = getInstantiatedList(object, fieldName, field);

            // get the class that goes inside the list
            Class<?> clazz = this.getGenericClass(field);

            // call the load method within the current node to populate the class
            try {

                Object instance = clazz.newInstance();
                new XmlView().bind(instance, node);

                // add this class to the list
                list.add(instance);

            } catch (Exception e) {
                addWarning(e.getMessage());
            }

        } else { // this object is not a list...

            // just call the load method with the current node
            try {
                Class<?> clazz = field.getType();
                Object instance = clazz.newInstance();
                new XmlView().bind(instance, node);

                // set this object
                this.setObject(object, field, instance);

            } catch (Exception e) {
                addWarning("The class " + field.getType().getCanonicalName() + " doesn't exist.");
            }

        }

    }

    /**
     * Determine the field name to the given corresponding node, and if that field is a list.
     * 
     * @param node
     * @param nodeName
     * @return
     */
    private FieldType determineFieldType(Node node, String nodeName) {
        boolean isList = this.doesNodeRepeat(node);
        String fieldName = this.determineFieldName(nodeName, isList);

        // if this field does not exist...
        if (!_fields.containsKey(fieldName)) {
            // attempt to make it plural and locate it
            String pluralFieldName = this.determineFieldName(nodeName, true);

            // if the the field map contains the plural field name...
            if (_fields.containsKey(pluralFieldName)) {
                // this is a list of single elements
                isList = true;
                fieldName = pluralFieldName;
            }
        }

        if (_fields.get(fieldName) != null && _fields.get(fieldName).getType().equals(List.class)) {
            isList = true;
        }

        FieldType type = new FieldType();
        type.setFieldName(fieldName);
        type.setIsList(isList);

        return type;
    }

    /**
     * Returns class for the generic of the given field
     * 
     * @param field
     * @return
     */
    private Class<?> getGenericClass(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * Returns true if the given node occurs more then once it that node's parent
     * 
     * @param node
     * @return
     */
    private boolean doesNodeRepeat(Node node) {
        Node parentNode = node.getParentNode();
        NodeList parentNodes = parentNode.getChildNodes();
        boolean isList = false;
        int nodeCount = 0;
        for (int i = 0; i < parentNodes.getLength(); i++) {
            Node childNode = parentNodes.item(i);
            if (childNode.getNodeName().equals(node.getNodeName())) {
                nodeCount++;
                if (nodeCount > 1) {
                    isList = true;
                    break;
                }
            }
        }
        return isList;
    }

    /**
     * Returns true if the given field is the java.util.List type
     * 
     * @param field
     * @return
     */
    private boolean isFieldList(Field field) {
        if (field.getType().getCanonicalName().equals(List.class.getCanonicalName()))
            return true;
        else
            return false;
    }

    /**
     * Returns the plural version of the given name
     * 
     * @param name
     * @return
     */
    private String getPluralName(String name) {
        if (name.endsWith("s"))
            name += "es";
        else
            name += "s";

        return name;
    }

    /**
     * Returns the singular version of the given plural name
     * 
     * @param pluralName
     * @return
     */
    private String getSingularName(String pluralName) {
        if (pluralName.endsWith("es"))
            return pluralName.substring(0, pluralName.length() - 2);
        else
            return pluralName.substring(0, pluralName.length() - 1);
    }

    /**
     * Gets the given field as a list, and if it doesn't exist creates it and sets it as the instance value for the
     * given field.
     * 
     * @param fieldName
     * @param field
     * @return
     */
    private List<Object> getInstantiatedList(Object object, String fieldName, Field field) {
        // attempt to get the list from the map
        List<Object> list = _listMap.get(fieldName);

        // if the list doesn't exist...
        if (list == null) {
            // create it and add it
            list = new ArrayList<Object>();
            _listMap.put(fieldName, list);
        }

        // set this list as the value for this field
        try {
            this.setList(object, field, list);
        } catch (IllegalAccessException il) {
            addWarning(il.getMessage());
        }

        return list;
    }

    /**
     * Handles populating field values in this class according to the attributes of the given node
     * 
     * @param node
     */
    private void handleAttributes(Object object, Node node) {
        // if this node has attributes...
        if (node.hasAttributes()) {

            NamedNodeMap map = node.getAttributes();
            for (int i = 0; i < map.getLength(); i++) {

                Node child = map.item(i);

                // find this node using reflection
                String childNodeName = child.getNodeName();
                String fieldName = "";

                if (_attributeNameToFieldNameMap.containsKey(childNodeName))
                    fieldName = _attributeNameToFieldNameMap.get(childNodeName);
                else
                    fieldName = childNodeName;

                Field field = _fields.get(fieldName);

                // set the value
                this.setFieldValueFromNode(object, field, child);

            }

        }
    }

    /**
     * Determine the field name of he java object using the node name from the XML document.
     * 
     * @param nodeName
     * @param isList
     * @return
     */
    private String determineFieldName(String nodeName, boolean isList) {
        String fieldName = "";

        // if the node is inside the node to field map...
        // use the corresponding value for the field name
        if (_nodeNameToFieldNameMap.containsKey(nodeName))
            fieldName = _nodeNameToFieldNameMap.get(nodeName);
        else if (isList)
            fieldName = this.getPluralName(nodeName);
        else
            // otherwise the field name is the node name...
            fieldName = nodeName;

        return fieldName;
    }

    /**
     * Sets the given field to the given value
     * 
     * @param field
     * @param node
     */
    private void setFieldValueFromNode(Object object, Field field, Node node) {
        // if ths node cannot be found...
        if (field == null) {
            addWarning("The field " + node.getNodeName() + " could not be found in class " + getClassName(object)
                    + " [setFieldValueFromNode]");
            return;
        }

        // get the type of this field, it should be a single value object at this point
        Class<?> type = field.getType();
        String typeName = type.getCanonicalName();

        // get a string representation of the node value
        String value = node.getTextContent();

        try {
            if (typeName.equals("java.lang.String"))
                this.setString(object, field, value);
            else if (typeName.equals("int"))
                this.setInt(object, field, value);
            else if (typeName.equals("short"))
                this.setShort(object, field, value);
            else if (typeName.equals("boolean"))
                this.setBoolean(object, field, value);
            else if (typeName.equals("char"))
                this.setChar(object, field, value);
            else if (typeName.equals("float"))
                this.setFloat(object, field, value);
            else if (typeName.equals("double"))
                this.setDouble(object, field, value);
            else if (typeName.equals("long"))
                this.setLong(object, field, value);
            else
                addWarning("" + typeName + " is not a supported class type for field " + field.getName() + " in class "
                        + getClassName(object) + " from node " + node.getNodeName() + " [setFieldValueFromNode]");
        } catch (IllegalAccessException il) {
            il.printStackTrace();
        }
    }

    /**
     * Removes all formatting characters and additional spaces from the given string.
     * 
     * @param value
     * @return
     */
    private String removeFormatting(String value) {
        StringTokenizer t = new StringTokenizer(value);
        int tokens = t.countTokens();

        String newValue = "";
        for (int i = 0; i < tokens; i++)
            newValue += t.nextToken() + " ";

        if (tokens == 1)
            newValue = value;

        newValue = newValue.trim();

        return newValue;
    }

    private void setString(Object object, Field field, String value) throws IllegalAccessException {
        String newValue = this.removeFormatting(value);
        field.set(object, newValue);
    }

    private void setList(Object object, Field field, List<Object> list) throws IllegalAccessException {
        field.set(object, list);
    }

    private void setObject(Object objectInstance, Field field, Object object) throws IllegalAccessException {
        field.set(objectInstance, object);
    }

    private void setInt(Object object, Field field, String value) throws IllegalAccessException {
        try {
            int intValue = Integer.parseInt(value);
            field.setInt(object, intValue);
        } catch (NumberFormatException nf) {
            addWarning(value + " is not a valid integer value for field " + field.getName() + " in class "
                    + getClassName(object));
        }
    }

    private void setShort(Object object, Field field, String value) throws IllegalAccessException {
        try {
            short shortValue = Short.parseShort(value);
            field.setShort(object, shortValue);
        } catch (NumberFormatException nf) {
            addWarning(value + " is not a valid short value for field " + field.getName() + " in class "
                    + getClassName(object));
        }
    }

    private void setBoolean(Object object, Field field, String value) throws IllegalAccessException {
        String boolLowercase = value.toLowerCase();
        boolean boolValue = false;
        if (boolLowercase.equals("true"))
            boolValue = true;
        field.setBoolean(object, boolValue);
    }

    private void setChar(Object object, Field field, String value) throws IllegalAccessException {
        char charValue = ' ';

        if (value.length() == 1)
            charValue = value.charAt(0);
        else
            addWarning(value + " is not a valid char value for field " + field.getName() + " in class "
                    + getClassName(object));

        field.setChar(object, charValue);
    }

    private void setFloat(Object object, Field field, String value) throws IllegalAccessException {
        try {
            float calValue = Float.parseFloat(value);
            field.setFloat(object, calValue);
        } catch (NumberFormatException nf) {
            addWarning(value + " is not a valid float value for field " + field.getName() + " in class "
                    + getClassName(object));
        }
    }

    private void setDouble(Object object, Field field, String value) throws IllegalAccessException {
        try {
            double calValue = Double.parseDouble(value);
            field.setDouble(object, calValue);
        } catch (NumberFormatException nf) {
            addWarning(value + " is not a valid double value for field " + field.getName() + " in class "
                    + getClassName(object));
        }
    }

    private void setLong(Object object, Field field, String value) throws IllegalAccessException {
        try {
            long calValue = Long.parseLong(value);
            field.setLong(object, calValue);
        } catch (NumberFormatException nf) {
            addWarning(value + " is not a valid long value for field " + field.getName() + " in class"
                    + getClassName(object));
        }
    }

    /**
     * Sets the name of this class's xml node
     * 
     * @param name
     */
    protected void setClassXmlNodeName(String name) {
        this.xmlRootElementName = name;
    }

    /**
     * Returns the name of this class's xml node
     * 
     * @return
     */
    protected String getRootElementName() {
        return this.xmlRootElementName;
    }

    /**
     * Converts this object to its XML representation
     * 
     * @return
     */
    public String toXML(Object instance) {
        String xml = "";
        this.configure(instance);
        if (this.getRootElementName() == null) {
            addWarning("The class " + getClassName(instance) + " doesn't have the ClassXmlNodeName annotation so it "
                    + "cannot be convert to XML.");
            return "";
        }
        // create the opening xml tag
        xml += "<" + this.getRootElementName() + " ";
        // convert any attributes to XML
        xml += this.convertAttributesToXML(instance);
        // close the opening xml tag
        xml += ">\n";
        // convert any fields to XML
        xml += this.convertFieldsToXML(instance);
        // close the root tag
        xml += "</" + this.getRootElementName() + ">\n";
        return xml;
    }

    /**
     * Casts the given object to an XmlBasedObject and returns the result of its toXML() method.
     * 
     * @param object
     * @return
     */
    private String toXmlBasedObjectXML(Object object) {
        String xml = "";
        xml += new XmlView().toXML(object);
        return xml;
    }

    private String convertFieldToXml(Object object, Field field) {
        String xml = "";
        try {
            Object instance = field.get(object);
            xml += new XmlView().toXML(instance);
        } catch (IllegalAccessException il) {
            addWarning(il.getMessage());
        }
        return xml;
    }

    /**
     * Returns the name of the class as specified in the ClassXmlNodeName annotation in the given object. Null is
     * returned if none exists
     * 
     * @param object
     * @return
     */
    private String getClassXmlNodeNameFromAnnotation(Object object) {
        Class<?> clazz = object.getClass();
        return this.getClassXmlNodeNameFromAnnotation(clazz);
    }

    /**
     * Returns the name of the class as specified in the ClassXmlNodeName annotation in the given object. Null is
     * returned if none exists
     * 
     * @param field
     * @return
     */
    private String getClassXmlNodeNameFromAnnotation(Field field) {
        Class<?> clazz = field.getType();
        return this.getClassXmlNodeNameFromAnnotation(clazz);
    }

    /**
     * Gets the value of the ClassXmlNodeName annotation for the given class if that annotation exists.
     * 
     * @param clazz
     * @return
     */
    private String getClassXmlNodeNameFromAnnotation(Class<?> clazz) {
        String result = null;
        Annotation anns[] = clazz.getAnnotations();
        for (Annotation ann : anns) {
            if (ann instanceof XmlRootElement) {
                XmlRootElement instance = (XmlRootElement) ann;
                result = instance.name();
                break;
            }
        }
        return result;
    }

    /**
     * Converts all attributes of this class to their XML representation
     * 
     * @return
     */
    private String convertAttributesToXML(Object instance) {
        String xml = "";
        Set<String> set = _attributeNameToFieldNameMap.keySet();
        Iterator<String> i = set.iterator();
        while (i.hasNext()) {
            String attributeName = i.next();
            String fieldName = _attributeNameToFieldNameMap.get(attributeName);

            try {
                Field field = instance.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                if (field == null) {
                    addWarning("Field " + fieldName + " doesn't exist in " + getClassName(instance));
                    continue;
                }

                if (field.get(instance) == null)
                    continue;

                String value = field.get(instance).toString();
                xml += fieldName + "=\"" + value + "\" ";
            } catch (NoSuchFieldException nsf) {
                addWarning("The field " + fieldName + " doesn't exist in class" + getClassName(instance));
            } catch (IllegalAccessException il) {
                addWarning(il.getMessage());
            }

        }
        return xml;
    }

    /**
     * Converts all field in this object to their XML representation
     * 
     * @return
     */
    private String convertFieldsToXML(Object instance) {

        String xml = "";

        // for each field...
        Set<String> set = _fields.keySet();
        Iterator<String> i = set.iterator();
        while (i.hasNext()) {
            String fieldName = i.next();

            // if this field is an attribute ignore it, it was already handled...
            if (_attributeNameToFieldNameMap.containsValue(fieldName))
                continue;

            // get the node name of this field...
            String nodeName = fieldName;
            boolean nodeNameFromMap = false;

            // if this field name is in the map...
            if (_fieldNameToNodeNameMap.containsKey(fieldName)) {
                nodeName = _fieldNameToNodeNameMap.get(fieldName);
                nodeNameFromMap = true;
            }

            // get the field...
            Field field = _fields.get(fieldName);
            if (field == null) {
                addWarning("No field named" + fieldName + " exists in " + instance.getClass().getCanonicalName());
                continue;
            }

            if (this.getClassXmlNodeNameFromAnnotation(field) != null) {
                xml += convertFieldToXml(instance, field);
                continue;
            }

            if (field.getType().equals(List.class)) {
                try {
                    List<Object> list = (List) field.get(instance);

                    if (list == null)
                        continue;

                    for (Object object : list) {

                        String classXmlNodeName = this.getClassXmlNodeNameFromAnnotation(object);

                        if (classXmlNodeName != null)
                            xml += toXmlBasedObjectXML(object);
                        else {
                            // if the node name didn't come from the map
                            String singularName = nodeName;
                            if (!nodeNameFromMap) {
                                // un-pluralize the node name
                                singularName = this.getSingularName(nodeName);
                            }
                            String value = object.toString();
                            xml += "<" + singularName + ">" + value + "</" + singularName + ">\n";
                        }
                    }

                } catch (IllegalAccessException il) {
                    addWarning(il.getMessage());
                }
                continue;
            }

            try {

                if (field.get(instance) == null)
                    continue;
                /*
                 * if (field.get(instance) == null) { addWarning("The field "+fieldName+" doesn't exist in class "
                 * +getClassName(instance)+" [convertFieldsToXML]"); continue;
                 * 
                 * }
                 */

                String value = field.get(instance).toString();
                xml += "<" + nodeName + ">" + value + "</" + nodeName + ">\n";
            } catch (IllegalAccessException il) {
                addWarning(il.getMessage());
            }

        }

        return xml;
    }
}

/**/
class FieldType {
    private boolean isList = false;
    private String fieldName;

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String value) {
        this.fieldName = value;
    }

    public boolean isList() {
        return this.isList;
    }

    public void setIsList(boolean value) {
        this.isList = value;
    }
}
