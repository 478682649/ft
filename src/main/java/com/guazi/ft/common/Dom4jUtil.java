package com.guazi.ft.common;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;

/**
 * Dom4jUtil
 *
 * @author shichunyang
 */
public class Dom4jUtil {

	public static Document getDocument(InputStream in) {
		SAXReader saxReader = new SAXReader();

		try {
			return saxReader.read(in);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void xmlWrite(OutputStream out, Document document) {
		OutputFormat format = OutputFormat.createPrettyPrint();

		XMLWriter writer = null;

		try {
			writer = new XMLWriter(new OutputStreamWriter(out, "UTF-8"), format);
			writer.write(document);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
