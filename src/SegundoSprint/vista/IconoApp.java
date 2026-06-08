package SegundoSprint.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**

 * <p>
 * No depende de ningún recurso externo (PNG, ICO, etc.): se construye en
 * memoria al arrancar la aplicación. Se exponen varias resoluciones porque
 * el sistema operativo elige una u otra según el contexto (barra de tareas,
 * Alt+Tab, esquina de la ventana…).
 * </p>
 *
 * <p>Uso típico desde una ventana:</p>
 * <pre>
 *   setIconImages(IconoApp.getIconImages());
 * </pre>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #4
 */
public final class IconoApp {

    /** Lista cacheada de iconos en distintas resoluciones (16/32/48/64/128 px). */
    private static List<Image> iconos;

    private IconoApp() { /* clase de utilidad estática */ }

    /**
     * Devuelve la lista de imágenes del icono en diferentes resoluciones.
     * El SO escogerá la más adecuada para cada situación.
     *
     * @return lista de imágenes del icono.
     */
    public static List<Image> getIconImages() {
        if (iconos == null) {
            iconos = new ArrayList<>();
            for (int tam : new int[]{16, 32, 48, 64, 128}) {
                iconos.add(crearTijeras(tam));
            }
        }
        return iconos;
    }

    /**
     * Aplica el icono de tijeras a cualquier ventana Swing
     * ({@link JFrame} o {@link java.awt.Dialog}).
     *
     * @param ventana ventana destino.
     */
    public static void aplicar(Window ventana) {
        if (ventana instanceof JFrame) {
            ((JFrame) ventana).setIconImages(getIconImages());
        } else {
            ventana.setIconImages(getIconImages());
        }
    }

    /**
     * Dibuja unas tijeras de sastre en una imagen cuadrada del tamaño indicado.
     *
     * @param tam ancho y alto en píxeles.
     * @return imagen con el icono.
     */
    private static BufferedImage crearTijeras(int tam) {
        BufferedImage img = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Anti-aliasing para que se vea suave en cualquier resolución
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                           RenderingHints.VALUE_STROKE_PURE);

        // Escalamos a un sistema de coordenadas 100x100 para diseñar
        // siempre con los mismos números independientemente del tamaño final.
        AffineTransform escala = AffineTransform.getScaleInstance(tam / 100.0, tam / 100.0);
        g.setTransform(escala);

        // Colores
        Color rojoUE  = new Color(0xE6, 0x30, 0x27); // rojo institucional UE
        Color metal   = new Color(0x33, 0x33, 0x33); // hojas de las tijeras (gris oscuro)
        Color metalBr = new Color(0x66, 0x66, 0x66); // brillo del metal

        // ---------- HOJAS (dos triángulos cruzados en forma de X) ----------
        // Hoja izquierda: del agujero (35,70) a la punta (95, 5)
        int[] hi_x = {30, 95,  88};
        int[] hi_y = {70,  5,  18};
        // Hoja derecha: del agujero (65,70) a la punta (5, 5)
        int[] hd_x = {70,  5,  12};
        int[] hd_y = {70,  5,  18};

        // Sombra ligera bajo las hojas para que destaquen sobre fondos claros
        g.setColor(new Color(0, 0, 0, 35));
        g.translate(1.5, 1.5);
        g.fillPolygon(hi_x, hi_y, 3);
        g.fillPolygon(hd_x, hd_y, 3);
        g.translate(-1.5, -1.5);

        // Hoja izquierda
        g.setColor(metal);
        g.fillPolygon(hi_x, hi_y, 3);
        g.setColor(metalBr);
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(35, 67, 90, 12); // línea de brillo interna

        // Hoja derecha
        g.setColor(metal);
        g.fillPolygon(hd_x, hd_y, 3);
        g.setColor(metalBr);
        g.drawLine(65, 67, 10, 12);

        // Contorno general
        g.setColor(new Color(0x1A, 0x1A, 0x1A));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(hi_x, hi_y, 3);
        g.drawPolygon(hd_x, hd_y, 3);

        // ---------- TORNILLO CENTRAL ----------
        g.setColor(metal);
        g.fillOval(46, 46, 8, 8);
        g.setColor(new Color(0xCC, 0xCC, 0xCC));
        g.fillOval(48, 48, 4, 4);

        // ---------- MANGOS (dos anillos rojos) ----------
        g.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(rojoUE);
        // Anillo izquierdo
        g.drawOval(15, 65, 30, 28);
        // Anillo derecho
        g.drawOval(55, 65, 30, 28);

        // Brillo interior de los mangos
        g.setStroke(new BasicStroke(2.0f));
        g.setColor(new Color(255, 255, 255, 90));
        g.drawArc(18, 68, 24, 22, 60, 80);
        g.drawArc(58, 68, 24, 22, 60, 80);

        g.dispose();
        return img;
    }
}
