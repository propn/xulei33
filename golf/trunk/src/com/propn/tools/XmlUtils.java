package com.propn.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.SAXException;

/**
 * 
 * <pre>
 * 功能描述：XML工具类
 * 1.
 * </pre>
 * 
 * @author Thunder.Hsu
 * @Create Date : 2012-10-11
 * @Version : 1.0
 */
public class XmlUtils {

    private static final boolean VALIDATE = true;
    private static final String ENCODING = "UTF-8";

    /**
     * 将字符串转为XML
     * 
     * @param inXML
     * @return Document
     * @throws Exception
     */
    public static Document parse(String xmlStr) throws Exception {
        Document doc = DocumentHelper.parseText(xmlStr);
        return doc;
    }

    public static Document load(InputStream in) throws Exception {
        SAXReader xmlReader = new SAXReader();
        Document doc = xmlReader.read(in);
        return doc;
    }

    /**
     * XML头编码和文件编码要一致
     * 
     * @param xmlFilePath
     * @return
     * @throws Exception
     */
    public static Document load(String xmlFilePath) throws Exception {
        SAXReader xmlReader = new SAXReader();
        Document doc = xmlReader.read(new File(xmlFilePath));
        return doc;
    }

    /**
     * 将document中的内容写入文件中
     * 
     * @param doc
     * @param filename
     * @return
     */
    public static boolean save(Document doc, String filename) {
        boolean flag = true;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(ENCODING);// 设置XML文档的编码类型
            format.setIndent(true);// 设置是否缩进
            format.setIndent(" ");// 以空格方式实现缩进
            format.setNewlines(true);// 设置是否换行
            XMLWriter writer = new XMLWriter(new FileWriter(new File(filename)), format);
            writer.write(doc);
            writer.close();
        } catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    /**
     * 
     * @param doc
     * @param encode UTF-8
     * @return
     * @throws Exception
     */
    public static String doc2String(Document doc) throws Exception {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(ENCODING);// 设置XML文档的编码类型
        format.setIndent(true);// 设置是否缩进
        format.setIndent(" ");// 以空格方式实现缩进
        format.setNewlines(true);// 设置是否换行
        StringWriter sw = new StringWriter();
        XMLWriter xw = new XMLWriter(sw, format);
        xw.write(doc);
        return sw.toString();
    }

    /**
     * 
     * @param xmlStr
     * @return
     * @throws Exception
     */
    public static Map doc2Map(Document doc) throws Exception {
        Element root = doc.getRootElement();
        Map rst = new HashMap();
        element2Map(root, rst);
        return rst;
    }

    public Document createDoc() {
        Document doc = DocumentHelper.createDocument();
        return doc;
    }

    /**
     * 通过XSD（XML Schema）校验XML
     * 
     * @param xmlDocument
     * @param schemaSource schemaSource=this.getClass().getResource("xml.xsd").toString()
     * @throws ParserConfigurationException
     * @throws Exception
     */
    public static void validateXMLByXSD(Document xmlDocument, String schemaSource) throws ParserConfigurationException,
            Exception {
        // 获取基于 SAX 的解析器的实例
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // 解析器在解析时验证 XML内容
        factory.setValidating(true);
        // 指定由此代码生成的解析器将提供对 XML名称空间的支持。
        factory.setNamespaceAware(true);
        // 使用当前配置的工厂参数创建 SAXParser 的一个新实例。
        SAXParser parser = factory.newSAXParser();
        // [url]http://sax.sourceforge.net/?selected=get-set[/url] 中找到。
        parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaSource);
        // 创建一个SAXValidator校验工具，并设置校验工具的属性
        SAXValidator validator = new SAXValidator(parser.getXMLReader());
        // 设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。
        XMLErrorHandler errorHandler = new XMLErrorHandler();// 创建默认的XML错误处理器
        validator.setErrorHandler(errorHandler);
        // 校验
        validator.validate(xmlDocument);
        // 如果错误信息不为空，说明校验失败，打印错误信息
        if (errorHandler.getErrors().hasContent()) {
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, OutputFormat.createPrettyPrint());
            writer.write(errorHandler.getErrors());
            throw new Exception("XML通过XSD文件校验失败:" + sw.toString());
        }
    }

    public static Element getElementByXpath(Document doc, String xpath) {
        Element e = (Element) doc.selectSingleNode(xpath);
        return e;
    }

    public static List<Element> getElementsByXpath(Document doc, String xpath) {
        List<Element> elements = (List<Element>) doc.selectNodes(xpath);
        return elements;
    }

    public static int getElementsCount(Document doc, String xpath) {
        int count = 0;
        String countPath = "count(" + xpath + ")";
        count = doc.numberValueOf(countPath).intValue();
        return count;
    }

    public static void modifyByXpath(Document doc, String xpath, String value) {
        Element e = (Element) doc.selectSingleNode(xpath);
        e.setText(value);
    }

    /**
     * 
     * @param element
     * @return
     */
    private static void element2Map(Element element, Map rstMap) {
        if (null == element) {
            return;
        }
        if (element.elements().isEmpty() && element.attributes().isEmpty()) {
            put(rstMap, element.getName(), (element.getStringValue() != null) ? element.getStringValue().trim() : "");
        } else {// 非叶子节点 带属性叶子节点
            Map map = new HashMap();
            // 处理节点属性
            if (!element.attributes().isEmpty()) {
                putAttribute(map, element);
            }
            if (!element.elements().isEmpty()) {
                for (Iterator i = element.elements().iterator(); i.hasNext();) {
                    element2Map((Element) i.next(), map);
                }
            }
            put(rstMap, element.getName(), map);
        }
    }

    private static void put(Map rstMap, Object key, Object value) {
        Object o = rstMap.get(key);
        if (o == null) {
            rstMap.put(key, value);
            return;
        }
        if (o instanceof List) {
            ((List) o).add(value);
            return;
        }
        // 存在相同节点,且不是List,则转换为List
        List list = new ArrayList();
        list.add(o);
        list.add(value);
        rstMap.put(key, list);
    }

    private static void putAttribute(Map rstMap, Element e) {
        List<Attribute> attrs = e.attributes();
        if (!attrs.isEmpty()) {
            for (Iterator it = attrs.iterator(); it.hasNext();) {
                Attribute attribute = (Attribute) it.next();
                String attrname = attribute.getName();
                String attrvalue = attribute.getValue();
                rstMap.put(attrname, attrvalue);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException, SAXException, Exception {
        String xmlFileName = "D:\\IBM\\wmbt70\\workspace\\MQTestJava\\com\\ztesoft\\csb\\common\\note_gb2312.xml";
        // 获取要校验xml文档实例
        SAXReader xmlReader = new SAXReader();
        try {
            XmlUtils v = new XmlUtils();
            String url = v.getClass().getResource("note.xsd").toString();
            Document doc = xmlReader.read(new File(xmlFileName));
            XmlUtils.validateXMLByXSD(doc, url);
            String xml = XmlUtils.doc2String(doc);
            System.out.println(xml);
            Document doc2 = XmlUtils.parse(xml);
            System.out.println(XmlUtils.doc2String(doc2));
        } catch (DocumentException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
    }
}
