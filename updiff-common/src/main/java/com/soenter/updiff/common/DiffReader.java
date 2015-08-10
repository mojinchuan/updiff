/**
 * Copyright : http://www.sandpay.com.cn/ , 2007-2015
 * Project : updiff
 * $$Id$$
 * $$Revision$$
 * Last Changed by sun.mt at 2015/8/10 15:47
 * $$URL$$
 * <p/>
 * Change Log
 * Author      Change Date    Comments
 * -------------------------------------------------------------
 * sun.mt@sand.com.cn         2015/8/10        Initailized
 */
package com.soenter.updiff.common;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.XMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName ：com.soenter.updiff.common.DiffReader
 * @Description : 
 * @author : sun.mt@sand.com.cn
 * @Date : 2015/8/10 15:47
 * @version 1.0.0
 *
 */
public class DiffReader {

	private XMLReader reader;

	private Document document;

	private Element rootElement;


	public DiffReader (File diffFile) throws DocumentException {

		SAXReader saxReader = new SAXReader();
		document = saxReader.read(diffFile);
		rootElement = document.getRootElement();
	}

	public List<DiffElement> readAll(){

		List<DiffElement> allEl = new ArrayList<DiffElement>();

		List<Element> group = rootElement.elements();

		for (Element g: group){
			List<Element> files = g.elements();
			String gName = g.attributeValue("name");
			String gOldName = g.attributeValue("oldGroup");
			for (Element f: files){
				allEl.add(new DiffElement(gName, gOldName, f.attributeValue("change"), f.attributeValue("path"), f.attributeValue("oldPath")));
			}
		}

		return allEl;
	}

}
