/*
 * Owl Platform
 * Copyright (C) 2014 Robert Moore and the Owl Platform
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.grailrtls.plunder.client.drawable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * @author Robert Moore
 */
public class PassiveTiles extends DrawableObject {

  private static final Logger log = Logger.getLogger(PassiveTiles.class
      .getName());

  private static class Tile {
    public final float x1, x2, y1, y2, score;

    public Tile(float x1, float y1, float x2, float y2, float score) {
      this.x1 = x1;
      this.x2 = x2;
      this.y1 = y1;
      this.y2 = y2;
      this.score = score;
    }

    @Override
    public boolean equals(Object o){
      if(o instanceof Tile){
        return this.equals((Tile)o);
      }
      return super.equals(o);
    }
    
    public boolean equals(Tile t) {
      if (Math.abs(this.x1 - t.x1) > 0.001) {
        return false;
      }
      if (Math.abs(this.x2 - t.x2) > 0.001) {
        return false;
      }
      if (Math.abs(this.y1 - t.y1) > 0.001) {
        return false;
      }
      if (Math.abs(this.y2 - t.y2) > 0.001) {
        return false;
      }
      return Math.abs(this.score - t.score) < 0.001;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("T");
      NumberFormat nf = NumberFormat.getFormat("#.00");
      sb.append(" {(").append(nf.format(this.x1)).append(",")
          .append(nf.format(this.y1)).append(") (").append(nf.format(this.x2))
          .append(",").append(nf.format(this.y2)).append("): ")
          .append(nf.format(this.score)).append("}");
      return sb.toString();
    }
  }

  private List<Tile> tiles = new ArrayList<PassiveTiles.Tile>();
  private final long timestamp = System.currentTimeMillis();

  /**
   * Creates a new set of passive tiles without any data.
   * 
   * @param id
   *          the Identifier for the region in which the tile exists
   */
  public PassiveTiles(String id) {
    super(id);
    

  }

  /**
   * Set the tiles (with scores) for this object.
   * 
   * @param newTiles
   */
  public void setTiles(List<Float[]> newTiles) {

    this.tiles.clear();
    if (newTiles != null && !newTiles.isEmpty()) {
      for (Float[] tFloat : newTiles) {
        if (tFloat.length != 5) {
          log.warning("Passive result does not have 5 elements, has "
              + tFloat.length);
          continue;
        }
        this.tiles.add(new Tile(tFloat[0].floatValue(), tFloat[1].floatValue(),
            tFloat[2].floatValue(), tFloat[3].floatValue(), tFloat[4]
                .floatValue()));
      }
    }
  }

  @Override
  public boolean equals(DrawableObject o) {
    if (o instanceof PassiveTiles) {
      return this.equals((PassiveTiles) o);
    }
    return super.equals(o);
  }

  public boolean equals(PassiveTiles t) {
    if (!this.getUri().equals(t.getUri())) {
      return false;
    }
    if (this.tiles.size() != t.tiles.size()) {
      return false;
    }
    for (Tile i : this.tiles) {
      if (!t.tiles.contains(i)) {
        
        return false;
      }
    }
    return true;
  }

  /**
   * Renders this objects tiles (if any) to the DrawableContext.
   * Since there are no images, uses drawing primitives to render
   * 2-d colored boxes.
   */
  @Override
  public void draw(Context2d context) {
    // FIXME: Proper solution for "out of date" data
    if (System.currentTimeMillis() - this.timestamp > 5000) {
      return;
    }

    
    context.save();
    context.setFillStyle("rgba(40,40,230,0.5)");

    for (Tile t : this.tiles) {
      float x1 = this.toCanvasX(t.x1);
      float x2 = this.toCanvasX(t.x2);
      float y1 = this.toCanvasY(t.y1);
      float y2 = this.toCanvasY(t.y2);
      
      context.fillRect(x1, y1, x2 - x1, y2 - y1);

    }

    context.restore();

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Tiles [").append(this.tiles.size()).append("]");

    if (!this.tiles.isEmpty()) {
      sb.append(": ");
      for (Tile t : this.tiles) {
        sb.append(" ").append(t.toString());
      }
    }
    return sb.toString();
  }
}
