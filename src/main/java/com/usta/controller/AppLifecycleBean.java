package com.usta.controller;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.awt.Desktop;
import java.net.URI;

@ApplicationScoped
public class AppLifecycleBean {

    void onStart(@Observes StartupEvent ev) {
        try {
            // Esto le dice a Windows que abra tu navegador predeterminado en esta URL
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:8081/products"));
            }
        } catch (Exception e) {
            System.out.println("No se pudo abrir el navegador automáticamente.");
        }
    }
}