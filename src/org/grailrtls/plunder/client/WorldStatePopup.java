package org.grailrtls.plunder.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class WorldStatePopup extends PopupPanel {

  
  public WorldStatePopup(){
    super(true);
    
    
  }
  
  public void setState(WorldState state){
    Tree tree = new Tree();
    
    TreeItem root = new TreeItem(state.getUri());
    
    
    TreeItem attribsItem = new TreeItem("Attributes");
    for(Attribute attr : state.getAttributes()){
      TreeItem aItem = new TreeItem(attr.getName());
      aItem.addItem("Origin: " + attr.getOrigin());
      aItem.addItem("Created: " + attr.getCreated());
      aItem.addItem("Expires: " + attr.getExpires());
      aItem.addItem("Data: " + attr.getData());
      
      attribsItem.addItem(aItem);
    }
    
    tree.addItem(root);
    tree.addItem(attribsItem);
    
    this.setWidget(tree);
  }
  
}
