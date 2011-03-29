package net.sf.iqser.plugin.filesystem.utils;

import java.util.Collection;
import java.util.Map;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;

/**
 * updates content attributes and keys attr.
 * @author alexandru.galos
 *
 */
public class ContentUpdate {


	/**
	 * updates the key attrs of the content.
	 * @param content the content that is updated
	 * @param keyAttributesList the new key attributes
	 */
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
	
	/**
	 * update the content attribute names.
	 * @param content the content that is updated
	 * @param attributeMappings a map of the old names and the new names
	 */
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
