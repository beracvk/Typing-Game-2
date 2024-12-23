package TypingGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Patlama efektlerini yönetmek için sınıf
public class Explosion {
    int x; // Patlamanın x koordinatı
    int y; // Patlamanın y koordinatı
    int size; // Patlamanın boyutu
    long startTime; // Patlama başlangıç zamanı
    private static final int DURATION = 1000; // Patlama süresi (milisaniye)
    private List<Particle> particles; // Patlamadan çıkan parçacıklar
    private Color color; // Patlamanın rengi

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 30 + new Random().nextInt(20); // Boyut 30 ile 50 arasında değişir
        this.startTime = System.currentTimeMillis(); // Başlangıç zamanını alır
        this.color = generateRandomColor(); // Rastgele bir renk oluşturur
        this.particles = generateParticles(); // Parçacık listesini oluşturur
    }

    private List<Particle> generateParticles() {
        List<Particle> particleList = new ArrayList<>(); // Parçacık listesi oluşturulur
        Random random = new Random();
        int particleCount = 20 + random.nextInt(30); // 20 ile 50 arasında parçacık oluşturur

        for (int i = 0; i < particleCount; i++) {
            double angle = random.nextDouble() * 2 * Math.PI; // Rastgele bir açı oluşturur
            double speed = 2 + random.nextDouble() * 3; // Hız 2 ile 5 arasında değişir
            particleList.add(new Particle(x, y, angle, speed, generateRandomColor()));
        }
        return particleList;
    }

    private Color generateRandomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)); // Rastgele renk üretir
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > DURATION; // Patlama süresi doldu mu kontrol eder
    }

    public void update() {
        for (Particle particle : particles) {
            particle.update(); // Her parçacığın hareketini günceller
        }
    }

    public void draw(Graphics2D g) {
        if (isExpired()) return; // Süresi dolmuşsa çizim yapmaz

        // Patlamayı çizer ve titreşim efekti verir
        long elapsed = System.currentTimeMillis() - startTime;
        int currentSize = (int) (size + Math.sin(elapsed / 50.0) * 10);
        g.setColor(color);
        g.fillOval(x - currentSize / 2, y - currentSize / 2, currentSize, currentSize);

        // Parçacıkları çizer
        for (Particle particle : particles) {
            particle.draw(g);
        }
    }

    private class Particle {
        double x; // Parçacığın x koordinatı
        double y; // Parçacığın y koordinatı
        double angle; // Hareket yönü açısı
        double speed; // Hareket hızı
        Color color; // Parçacığın rengi

        Particle(double x, double y, double angle, double speed, Color color) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.speed = speed;
            this.color = color;
        }

        void update() {
            x += Math.cos(angle) * speed; // X eksenindeki hareket
            y += Math.sin(angle) * speed; // Y eksenindeki hareket
            speed *= 0.95; // Yavaşlama efekti
        }

        void draw(Graphics2D g) {
            g.setColor(color);
            g.fillOval((int) x - 2, (int) y - 2, 4, 4); // Parçacık boyutları
        }
    }
}
