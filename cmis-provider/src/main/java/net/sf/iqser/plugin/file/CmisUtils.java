package net.sf.iqser.plugin.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

/**
 * Utility class for CmisContentProvider.
 * 
 * @author robert.baban
 *
 */
public class CmisUtils {
	
	/**
	 * Parses repository initialization parameters. 
	 * The initialization parameters are using the following pattern: 
	 * [value1][value2][value3]
	 *  
	 * @param initParam the string value of the init-param
	 * 
	 * @return a list of repository names
	 */
	public static  List<String> parseInitParam(String initParam) {
		
		List<String> repositories =  new ArrayList<String>();
		
		if (initParam != null && initParam.length() > 0){
			
			Pattern p = Pattern.compile("\\[([^]]+)");
			Matcher m = p.matcher(initParam);
	        
			while(m.find()){
				repositories.add(m.group(1).trim());
			}
		}
		
		return repositories;
	}
	
	/**
	 * Parses repository attribute mappings initialization parameter. 
	 * The initialization parameters are using the following pattern: 
	 * [oldAttrName=newAttrName][oldAttrName=newAttrName][oldAttrName=newAttrName]
	 *  
	 * @param initParam the string value of the init-param
	 * 
	 * @return a map of attribute names of repository names
	 */
	public static Map<String, String> parseAttributesMappings(String initParam) {
		
		Map<String, String> mappings = new HashMap<String, String>();
		
		if (initParam != null && initParam.length() > 0){
			
			Pattern p = Pattern.compile("\\[([^=]+)=([^]]+)]");
			Matcher m = p.matcher(initParam);
	        	        
			while(m.find()){
				String oldName = m.group(1).trim();
				String newName = m.group(2).trim();
				mappings.put(oldName, newName);
			}
		}
				
		return mappings;
	}	

	/**
	 * Extracts repository name from content url (http://cmis/repositoryName/basicType#cmisObjectId).
	 * 
	 * @param contentUrl - url 
	 * @return a string representing repository name
	 */
	public static String getRepository(String contentUrl){
		
		
		Pattern p = Pattern.compile("cmis/([^/]+)/([^#]+)#(.+)");		
		Matcher m = p.matcher(contentUrl);
		
		if(m.find()){			
			String repoName = m.group(1);
			return repoName;
		}		
		
		return null;
	}
	
	/**
	 * Extracts objectId name from content url (http://cmis/repositoryName/basicType#cmisObjectId).
	 * 
	 * @param contentUrl - url 
	 * @return a string representing objectId name
	 */
	public static String getObjectID(String contentUrl){
		
		//http://cmis/repositoryName/basicType#ID
		Pattern p = Pattern.compile("cmis/([^/]+)/([^#]+)#(.+)");		
		Matcher m = p.matcher(contentUrl);
		
		if(m.find()){			
			return m.group(3);
		}		
		
		return null;
	}
	
	/**
	 * Parse a relative path to obtain an ordered list of folders and subfolders.
	 * 
	 * @param relativePath - a relative path in the form &quot;folder/sub1/sub2&quot;
	 * @return a string representing objectId name
	 */
	public static List<String> parseRelativePath(String relativePath){
		
		if(relativePath == null || relativePath.isEmpty()) {
			return null;
		}
		List<String> foldersList = new ArrayList<String>();
		StringTokenizer folderSepTokenizer = new StringTokenizer(relativePath, "/", false);
		while(folderSepTokenizer.hasMoreTokens()) {
			foldersList.add(folderSepTokenizer.nextToken());
		}
		return foldersList;
		
	}
	
	public static Folder findOrAutocreateBaseFolder(Session session, String baseFolderPath) {
		CmisObject baseObj = session.getObjectByPath(baseFolderPath);
		Folder result = null;
		if(baseObj != null) {
			if(baseObj.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
				result = (Folder) baseObj;
			}
		}
		else {
			// Auto-create
			StringBuilder incrementalPath = new StringBuilder();
			for(String folderName : CmisUtils.parseRelativePath(baseFolderPath)) {
				Folder parent = null;
				if(incrementalPath.length() == 0) {
					parent = session.getRootFolder();
				}
				else {
					CmisObject parentObj = session.getObjectByPath(incrementalPath.toString());
					if(parentObj != null && parentObj.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
						parent = (Folder) parentObj;
					}
				}
				if(parent != null) {
					CmisObject checkObj = session.getObjectByPath(incrementalPath.toString() + "/" + folderName);
					Folder newFolder = null;
					if(checkObj == null) {
						Map<String, Object> folderParams = new HashMap<String, Object>();
						folderParams.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
						folderParams.put(PropertyIds.NAME, folderName);
					    newFolder = parent.createFolder(folderParams);
					}
					if(incrementalPath.length() == 0) {
						incrementalPath.append(folderName);
					}
					else {
						incrementalPath.append("/" + folderName);
					}
					if(incrementalPath.toString().equals(baseFolderPath)) {
						result = newFolder;
					}
				}
				else {
					// Unable to create. Stop the cycle
					break;
				}
			}
		}
		return result;
	}

}
