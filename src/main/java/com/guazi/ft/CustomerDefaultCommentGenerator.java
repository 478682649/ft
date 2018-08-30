package com.guazi.ft;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;

/**
 * CustomerDefaultCommentGenerator
 *
 * @author shichunyang
 */
public class CustomerDefaultCommentGenerator extends DefaultCommentGenerator {
	@Override
	public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
		method.addJavaDocLine("/**");
		method.addJavaDocLine(method.getName());
		String idName = "id";
		String paramName = method.getParameters().get(0).getName();
		String paramComment;
		if (paramName.equals(idName)) {
			paramComment = "主键";
		} else {
			paramComment = "DO";
		}
		method.addJavaDocLine("@param " + paramName + " " + paramComment);
		method.addJavaDocLine("*/");
	}

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
		String fieldComment = "(" + introspectedColumn.getActualColumnName() + ")";

		if (introspectedColumn.getRemarks() != null && introspectedColumn.getRemarks().length() > 0) {
			field.addJavaDocLine("/** " + introspectedColumn.getRemarks() + fieldComment + " */");
		} else {
			field.addJavaDocLine("/** " + fieldComment + " */");
		}
	}

	@Override
	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		topLevelClass.addJavaDocLine("/**");

		topLevelClass.addJavaDocLine(introspectedTable.getFullyQualifiedTable() + "");

		topLevelClass.addJavaDocLine("*/");
	}

	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
	}

	@Override
	public void addComment(XmlElement xmlElement) {
	}

	@Override
	public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
	}

	@Override
	public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
	}
}
