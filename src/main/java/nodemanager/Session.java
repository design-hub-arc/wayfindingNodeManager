package nodemanager;

import nodemanager.gui.editPage.NodeDataPane;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.model.Node;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */


/**
* The Session class is a static class used 
* to store global variables used by the program.
* 
* While having global variables is generally not a good idea,
* it is significantly better than passing a Session object to every object the program creates
*/
public class Session {
    // the view
    public static MapImage map = null;
}
