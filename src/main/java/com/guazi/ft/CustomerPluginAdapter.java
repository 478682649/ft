package com.guazi.ft;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * CustomerPluginAdapter
 *
 * @author shichunyang
 */
public class CustomerPluginAdapter extends PluginAdapter {
	@Override
	public boolean validate(List<String> list) {
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Component"));
		interfaze.addAnnotation("@Component");
		interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
		interfaze.addAnnotation("@Mapper");
		return true;
	}

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Data"));
		topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Date"));
		topLevelClass.addAnnotation("@Data");

		Field createdAt = new Field();
		createdAt.setName("createdAt");
		createdAt.setType(new FullyQualifiedJavaType("Date"));
		createdAt.setVisibility(JavaVisibility.PRIVATE);

		Field updatedAt = new Field();
		updatedAt.setName("updatedAt");
		updatedAt.setType(new FullyQualifiedJavaType("Date"));
		updatedAt.setVisibility(JavaVisibility.PRIVATE);

		topLevelClass.addField(createdAt);
		topLevelClass.addField(updatedAt);
		return true;
	}

	@Override
	public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
		List<String> annotations = method.getAnnotations();
		annotations.clear();
		annotations.add("@Select({");
		annotations.add("\"select \",");
		annotations.add("\"* \",");
		annotations.add("\"from " + introspectedTable.getFullyQualifiedTable() + " \",");
		annotations.add("\"where id = #{id}\"");
		annotations.add("})");
		return true;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return false;
	}

	@Override
	public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		return false;
	}

	@Override
	public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
		return false;
	}
}
