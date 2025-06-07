package com.application.interfaces;

import com.application.view.panels.consultation.calendar.DynamicCell;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public interface IDynamicCellRender<E> {

    public void paintBackground(Graphics2D g2, DynamicCell<E> dynamicCell, Rectangle rectangle);

    public void paintCell(Graphics2D g2, Rectangle2D rectangle, E e);

    public void paint(Graphics2D g2, Rectangle rectangle);

    public E next(E last);

    public E previous(E first);
}
