package ru.niifhm.bioinformatics.jsub.ant;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.niifhm.bioinformatics.jsub.Properties;
import ru.niifhm.bioinformatics.jsub.Skeleton;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.jsub.template.DefaultTepmlate;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * Project build.xml parser.
 * Creates alternative build.nodependencies.xml with no
 * dependencies between targets.
 * @author zeleniy
 */
public class BuildXml {


    /**
     * depends attribute name.
     */
    private final String        ATTRIBUTE_DEPENDS = "depends";
    /**
     * name attribute name.
     */
    private final String        ATTRIBUTE_NAME    = "name";
    /**
     * target tag name.
     */
    private final String        TARGET_TAG_NAME   = "target";
    /**
     * 
     */
    private Document            _document;
    private String              _filepath;
    private List<String>        _targets          = new ArrayList<String>();
    private static final Logger _LOGGER           = Logger.getLogger(BuildXml.class);


    public static BuildXml factory(String filepath) throws Exception {

        return new BuildXml(filepath);
    }


    public BuildXml(String filepath) throws Exception {

        _filepath = filepath;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        builder = builderFactory.newDocumentBuilder();
        _document = builder.parse(_filepath);

        _loadTargetsList();
    }


    /**
     * See {@link #getDependentTargets(Property)} for comments.
     * @param property
     * @return
     */
    public List<String> getDependentTargets(String property) {

        return getDependentTargets(new Property(property, StringPool.EMPTY));
    }


    /**
     * Get targets that have this property as input.
     * @param property
     * @return
     */
    public List<String> getDependentTargets(Property property) {

        List<String> targets = new ArrayList<String>();

        try {
            Set<String> searchProperties = new HashSet<String>();
            searchProperties.add(property.getClearName());

            File buildXml = new File(_filepath);
            File skeletonDir = buildXml.getParentFile();
            Skeleton skeleton = new Skeleton(skeletonDir.getName());
            Project project = AntProjectBuilder.getInstance()
                .setBuildXmlFile(buildXml)
                .create();

            for (Job job : skeleton.getJobList()) {
                Target target = AntUtil.getTargetByName(project, job.getName());
                Properties inputProperties = new Properties(AntUtil.getInputProperties(target));
                for (Task task : target.getTasks()) {
                    Property outputProperty = Property.factory(task);
                    if (! outputProperty.isProperty()) {
                        continue;
                    }

                    if (! Property.isOutput(outputProperty)) {
                        continue;
                    }

                    if (! DefaultTepmlate.isPlaceholder(outputProperty.getValue())) {
                        continue;
                    }

                    Property placeholderProperty = new Property(Property.getName(outputProperty.getValue()),
                        StringPool.EMPTY);
                    if (! searchProperties.contains(placeholderProperty.getClearName())) {
                        continue;
                    }

                    if (! inputProperties.containsInGeneral(placeholderProperty)) {
                        continue;
                    }

                    String propertyName = new Property(outputProperty.getName(), StringPool.EMPTY).getClearName();
                    searchProperties.add(propertyName);
                    if (! targets.contains(job.getName())) {
                        targets.add(job.getName());
                    }
                }
            }
        } catch (Exception e) {
            _LOGGER.error(String.format("ERROR: [$s] %s", e.getClass().getName(), e.getMessage()));
        }

        return targets;
    }


    private void _loadTargetsList() {

        NodeList childNodes = _document.getElementsByTagName(TARGET_TAG_NAME);
        for (int i = 0; i < childNodes.getLength(); i ++) {
            Node target = childNodes.item(i);
            NamedNodeMap attributes = target.getAttributes();
            for (int j = 0; j < attributes.getLength(); j ++) {
                Node attribute = attributes.item(j);
                if (attribute.getNodeName().equals(ATTRIBUTE_NAME)) {
                    _targets.add(attribute.getNodeValue());
                }
            }
        }
    }


    public Document getDocument() {

        return _document;
    }


    /**
     * Parse project build.xml file.
     * Method remove dependency attribute from targets.
     */
    public BuildXml removeDependencyAttribute() {

        NodeList childNodes = _document.getElementsByTagName(TARGET_TAG_NAME);
        for (int i = 0; i < childNodes.getLength(); i ++) {
            Node target = childNodes.item(i);
            NamedNodeMap attributes = target.getAttributes();
            for (int j = 0; j < attributes.getLength(); j ++) {
                Node attribute = attributes.item(j);
                if (attribute.getNodeName().equals(ATTRIBUTE_DEPENDS)) {
                    attributes.removeNamedItem(ATTRIBUTE_DEPENDS);
                }
            }
        }

        return this;
    }


    public BuildXml addTarget(Element target) {

        return addTarget(target, null);
    }


    public BuildXml addTarget(Element target, String dependency) {

        if (! _targets.contains(target.getAttribute("name"))) {
            if (dependency != null) {
                target.setAttribute(ATTRIBUTE_DEPENDS, dependency);
            }

            _document.getDocumentElement().appendChild(target);
        }

        return this;
    }


    /**
     * Save parsed project build.xml file.
     * @throws Exception
     */
    public void save() throws Exception {

        save(_filepath);
    }


    /**
     * Save parsed project build.xml file.
     * @throws Exception
     */
    public void save(String buildFile) throws Exception {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(_document);
        StreamResult stream = new StreamResult(buildFile);

        transformer.transform(source, stream);
    }
}