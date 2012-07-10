package org.grailrtls.plunder.client;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class WorldStatePopup extends PopupPanel {

  
  public WorldStatePopup(){
    super(true);
    
    
  }
  
  public void setState(WorldState state){
    Tree tree = new Tree();
    
    TreeItem root = new TreeItem(SafeHtmlUtils.fromString(state.getId()));
    
    
    TreeItem attribsItem = new TreeItem(SafeHtmlUtils.fromSafeConstant("Attributes"));
    for(Attribute attr : state.getAttributes()){
      TreeItem aItem = new TreeItem(SafeHtmlUtils.fromString(attr.getName()));
      aItem.addItem(SafeHtmlUtils.fromString("Origin: " + attr.getOrigin()));
      aItem.addItem(SafeHtmlUtils.fromString("Created: " + attr.getCreated()));
      aItem.addItem(SafeHtmlUtils.fromString("Expires: " + attr.getExpires()));
      aItem.addItem(SafeHtmlUtils.fromString("Data: " + attr.getData()));
      
      attribsItem.addItem(aItem);
    }
    
    tree.addItem(root);
    tree.addItem(attribsItem);
    
    this.setWidget(tree);
  }
  
}
