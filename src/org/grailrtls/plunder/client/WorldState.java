package org.grailrtls.plunder.client;



public class WorldState{
  private String identifier;
  private Attribute[] attributes;
  
  protected WorldState(){
    
  }
  
  public final void setId(final String identifier)  {this.identifier = identifier;}
  
  public final String getId() {return this.identifier;}
  
  public final Attribute[] getAttributes() { return this.attributes; }
  
  public final void setAttributes(final Attribute[] attributes) { this.attributes = attributes; }

}
