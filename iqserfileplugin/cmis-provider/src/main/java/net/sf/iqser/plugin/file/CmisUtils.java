package net.sf.iqser.plugin.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
