package com.application.interfaces;

import com.application.view.panels.consultation.calendar.ModelDate;
import java.awt.event.MouseEvent;

public interface ICalendarSelectedListener {

    public void selected(MouseEvent evt, ModelDate date);
}
