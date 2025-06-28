package com.application.interfaces;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface ICalendarEventCellRender<E> {

    public void paint(Graphics2D g2, Rectangle2D rectangle2D, boolean isSelected, E value);
}
