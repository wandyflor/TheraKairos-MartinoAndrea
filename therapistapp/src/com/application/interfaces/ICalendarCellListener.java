package com.application.interfaces;

import java.awt.event.MouseEvent;

public interface ICalendarCellListener {

    public void cellSelected(MouseEvent evet, int index);

    public void scrollChanged();
}
