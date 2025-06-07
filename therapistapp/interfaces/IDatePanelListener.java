package com.application.interfaces;

import com.application.view.panels.consultation.calendar.ModelDate;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface IDatePanelListener {

    public boolean cellPaint(Graphics2D g2, Rectangle2D rectangle, ModelDate e);
}
