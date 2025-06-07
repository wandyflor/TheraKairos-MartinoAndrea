package com.application.view.panels.renderers;

import com.application.interfaces.IDynamicCellRender;
import com.application.view.panels.consultation.calendar.DynamicCell;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class CalendarCellRender<E> implements IDynamicCellRender<E> {

    public Point getMouse() {
        return mouse;
    }

    public void setMouse(Point mouse) {
        this.mouse = mouse;
    }

    public CalendarCellRender(Point mouse) {
        this.mouse = mouse;
    }

    private Point mouse;

    @Override
    public void paintBackground(Graphics2D g2, DynamicCell<E> dynamicCell, Rectangle rectangle) {
//        if (mouse != null && dynamicCell.getAlpha() == 1) {
//            Insets inset = dynamicCell.getInsets();
//            BufferedImage img = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_ARGB);
//            Graphics2D g = img.createGraphics();
//            int column = dynamicCell.getColumn();
//            int row = dynamicCell.getRow();
//            double startY = dynamicCell.getStartLocation();
//            double cellWidth = rectangle.getWidth() / column;
//            double cellHeight = rectangle.getHeight() / row;
//            double x = 0;
//            double y = startY;
//            float s1 = UIScale.scale(1f);
//            float s2 = UIScale.scale(3f);
//            for (int i = 0; i < dynamicCell.getModels().size(); i++) {
//                Area area = new Area(new Rectangle2D.Double(x + s1, y + s1, cellWidth - s1 * 2, cellHeight - s1 * 2));
//                area.subtract(new Area(new Rectangle2D.Double(x + s2, y + s2, cellWidth - s2 * 2, cellHeight - s2 * 2)));
//                g.fill(area);
//                if ((i + 1) % column == 0) {
//                    x = 0;
//                    y += cellHeight;
//                } else {
//                    x += cellWidth;
//                }
//            }
//            Color color = FlatUIUtils.getUIColor("Calendar.effectColor", "Label.foreground");
//            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, dynamicCell.getAlpha()));
//            float radius = Math.min(rectangle.width, rectangle.height) * 0.3f;
//            float[] dist = {0f, 0.6f, 1f};
//            Color[] colors = {convertColor(color, 0.7f), convertColor(color, 0.3f), convertColor(color, 0.03f)};
//            g.setPaint(new RadialGradientPaint(new Point(mouse.x - inset.left, mouse.y - inset.top), radius, dist, colors));
//            g.fill(new Rectangle2D.Double(0, 0, rectangle.width, rectangle.height));
//            g.dispose();
//            g2.drawImage(img, 0, 0, null);
//        }
    }

    @Override
    public void paint(Graphics2D g2, Rectangle rectangle) {
    }

    public Color convertColor(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * alpha));
    }
}
