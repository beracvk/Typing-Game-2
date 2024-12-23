package TypingGame;

public class Word {
    String text; // Kelimenin metni
    int x; // Kelimenin x koordinatı
    int y; // Kelimenin y koordinatı
    int speed; // Kelimenin hareket hızı
    long spawnTime; // Kelimenin ortaya çıkış zamanı

    Word(String text, int x, int y) {
        this.text = text; // Metni başlangıçta belirleme
        this.x = x; // Başlangıç x koordinatı
        this.y = y; // Başlangıç y koordinatı
    }
}