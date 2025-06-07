package com.application.view.panels.consultation.calendar;

import com.application.view.panels.renderers.CalendarCellRender;
import com.application.interfaces.IDynamicCellListener;
import com.application.interfaces.ICalendarCellListener;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.SwingUtilities;

public class PanelYear extends DynamicCell<Integer> {

    public void setYear(int year) {
        init(year - 4);
    }

    private final Point mouse = new Point();
    private ICalendarCellListener calendarCellListener;

    public PanelYear() {
        init();
    }

    private void init() {
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background:if($Calendar.background,$Calendar.background,$Panel.background)");
        setColumn(4);
        setRow(5);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.setLocation(e.getPoint());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouse.setLocation(e.getPoint());
                repaint();
            }
        };
        IDynamicCellListener dynamicCellListener = new IDynamicCellListener() {
            @Override
            public void scrollChanged(boolean scrollNext) {
                calendarCellListener.scrollChanged();
            }

            @Override
            public void mouseSelected(MouseEvent mouse) {
                if (SwingUtilities.isLeftMouseButton(mouse)) {
                    calendarCellListener.cellSelected(mouse, getSelectedIndex());
                }
            }
        };
        addEventDynamicCellListenter(dynamicCellListener);
        addMouseMotionListener(mouseAdapter);
        setDynamicCellRender(new CalendarCellRender<Integer>(mouse) {
            @Override
            public void paintCell(Graphics2D g2, Rectangle2D rectangle, Integer e) {
                FontMetrics fm = g2.getFontMetrics();
                String text = e + "";
                Rectangle2D fr = fm.getStringBounds(text, g2);
                float x = (float) ((rectangle.getWidth() - fr.getWidth()) / 2f);
                float y = (float) (((rectangle.getHeight() - fr.getHeight()) / 2) + fm.getAscent());
                g2.setColor(getForeground());
                g2.drawString(text, x, y);
            }

            @Override
            public Integer next(Integer last) {
                return last + 1;
            }

            @Override
            public Integer previous(Integer first) {
                return first - 1;
            }
        });
    }

    public ICalendarCellListener getCalendarCellListener() {
        return calendarCellListener;
    }

    public void setCalendarCellListener(ICalendarCellListener calendarCellListener) {
        this.calendarCellListener = calendarCellListener;
    }

    public String getYear() {
        int start = getModels().get(0);
        int end = getModels().get(getModels().size() - 1);
        return start + " - " + end;
    }
}
