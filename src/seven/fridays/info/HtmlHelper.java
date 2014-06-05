package seven.fridays.info;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import android.util.Log;

public class HtmlHelper {
	  TagNode rootNode;

	  //Конструктор
	  public HtmlHelper(URL htmlPage) throws IOException
	  {
	    //Создаём объект HtmlCleaner
	    HtmlCleaner cleaner = new HtmlCleaner();
	    //Загружаем html код сайта
	    rootNode = cleaner.clean(htmlPage);
	  }

	  List<TagNode> getLinksByClass(String CSSClassname)
	  {
	    List<TagNode> linkList = new ArrayList<TagNode>();

	    //Выбираем все ссылки
	    TagNode linkElements[] = rootNode.getElementsByName("a", true);
	    for (int i = 0; linkElements != null && i < linkElements.length; i++)
	    {
	      //получаем атрибут по имени
	      String classType = linkElements[i].getAttributeByName("class");
	      //если атрибут есть и он эквивалентен искомому, то добавляем в список
	      if (classType != null && classType.equals(CSSClassname))
	      {	    	  
	        linkList.add(linkElements[i]);	        
	      }
	    }

	    return linkList;
	  }
	  
	  List<TagNode> getFullPageLinks()
	  {
	    List<TagNode> linkList = new ArrayList<TagNode>();

	   /* //Выбираем все ссылки
	    TagNode linkElements[] = rootNode.getElementsByName("img", true);
	    for (int i = 0; linkElements != null && i < linkElements.length; i++)
	    {
	    	//linkList.add(linkElements[i]);
	      //получаем атрибут по имени
	      String classType = linkElements[i].getAttributeByName("src");
	      //если атрибут есть и он эквивалентен искомому, то добавляем в список
	      
	      if (classType != null)
	      {	    	  
	        linkList.add(linkElements[i]);	
	        Log.d("MyLogs", "scr="+classType);
	      }
	    }
*/
	    
	    try {
	    	//Object[] foundList = root.evaluateXPath("//div/p[@class='name']/a");
			Object[] foundList = rootNode.evaluateXPath("//div[@class='image']/img");
			if (!(null == foundList || foundList.length < 1)) {
				for (Object tekFoundList : foundList) {
					linkList.add((TagNode) tekFoundList);
					String classType = ((TagNode) tekFoundList).getAttributeByName("src");					
				}
			}
			
			 foundList = rootNode.evaluateXPath("//div[@class='info-box']");
			 if (!(null == foundList || foundList.length < 1)) {
					for (Object tekFoundList : foundList) {
						linkList.add((TagNode) tekFoundList);						
					}
				}
			 
			 if (linkList.size()!=2) {linkList.clear();}
			 
			 
			 
		} catch (XPatherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	    return linkList;
	  }
	}