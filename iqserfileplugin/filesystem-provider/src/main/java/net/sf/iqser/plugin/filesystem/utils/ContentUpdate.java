package net.sf.iqser.plugin.filesystem.utils;

import java.util.Collection;
import java.util.Map;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

public class ContentUpdate {

	public void updateKeyAttributes(Content content, Collection keyAttributesList) {

		Collection attributes = content.getAttributes();

		for (Object attribute : attributes) {

			String name = ((Attribute)attribute).getName();

			boolean contains = keyAttributesList.contains(name);
			if (contains) {
				((Attribute)attribute).setKey(true);
			} else
				((Attribute)attribute).setKey(false);

		}

	}
	
	public  void updateAttributes(Content content, Map attributeMappings) {

		Collection attributes = content.getAttributes();

		for (Object attribute : attributes) {
			String name = ((Attribute)attribute).getName();
			if (attributeMappings.containsKey(name)) {
				name = (String) attributeMappings.get(name);
				((Attribute) attribute).setName(name);
			}

		}

	}
}
