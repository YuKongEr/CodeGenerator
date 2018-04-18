package com.tony.util;

import com.tony.config.ConfigInfo;
import com.tony.config.JdbcConfig;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangwj20966 2018/4/18
 */
public class ConfigParser {
    private static ConfigParser INSTANCE;
    private Element root;
    private Document document;
    private static Logger logger = LoggerFactory.getLogger(ConfigParser.class);

    public static ConfigParser getInstance() {
        if (INSTANCE == null) {
            synchronized (ConfigParser.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConfigParser();
                }
            }
        }
        return INSTANCE;
    }

    private ConfigParser() {
        InputStream configInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("xml.xml");
        SAXReader saxReader = new SAXReader();
        try {
            document = saxReader.read(configInputStream);
        } catch (DocumentException e) {
            logger.error("初始化读取XML配置信息失败");
        }
        if (document != null) {
            root = document.getRootElement();
        }

    }

    public List<ConfigInfo> getConfigs() {
        Logger logger = LoggerFactory.getLogger(ConfigParser.class);

        try {
            List<ConfigInfo> configInfos = new ArrayList<>();
            Element child;
            ConfigInfo config;
            for (Object e : root.elements("config")) {
                child = (Element) e;
                config = new ConfigInfo();
                config.setPackageName(getValueByTagName("packageName", child));
                config.setClassName(getValueByTagName("className", child));
                config.setDesc(getValueByTagName("desc", child));
                config.setTableName(getValueByTagName("tableName", child));
                configInfos.add(config);
            }
            return configInfos;
        } catch (Exception e) {
            logger.error("读取配置信息失败", e);
        }
        return null;
    }

    public JdbcConfig getJdbcConfig() {
        JdbcConfig config = new JdbcConfig();
        Element jdbcConfig = root.element("jdbcConfig");

        config.setHost(getValueByTagName("host", jdbcConfig));
        config.setPort(getValueByTagName("port", jdbcConfig));
        config.setUserName(getValueByTagName("userName", jdbcConfig));
        config.setPassword(getValueByTagName("password", jdbcConfig));

        return config;
    }

    public String getFilePath() {
        return getValueByTagName("filePath", root);
    }

    private static String getValueByTagName(String tagName, Element element) {
        Element child = element.element(tagName);
        if (child != null) {
            return child.getStringValue();
        }
        return null;
    }
}