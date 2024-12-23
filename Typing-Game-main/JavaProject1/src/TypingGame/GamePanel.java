package TypingGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private final List<Word> words = new ArrayList<>(); // Ekranda belirecek kelimelerin listesini muhafaza eder
    private final List<Word> pendingWords = new ArrayList<>(); // Doğru zamanda ortaya çıkacak kelimelerin geçici deposu
    private final List<Explosion> explosions = new ArrayList<>(); // Patlama efektlerini elinde tutar
    private final Random random = new Random(); // Muhtelif rastgelelik icra etmek için kullanılır
    private boolean running; // Oyun döngüsünü idame ettirmek için kullanılan bayrak
    private String inputText = ""; // Oyuncunun hâlihazırda girdiği metni kaydeder
    private int currentWave = 1; // Mevcut dalgayı tayin eder
    private int waveWordCount = 1; // Bu dalgadaki kelime miktarı
    private int playerX;
    private int playerY;
    private int playerLives = 3; // Oyuncunun canları
    private int score = 0; // Oyuncunun puanı
    private int combo = 0; // O anki kombonun değeri
    private boolean showWaveInfo = true; // Dalga bilgisi göstermek için kullanılır
    private long waveInfoStartTime; // Dalga bilgisinin ekranda kalacağı zamanı başlatır
    private long gameStartTime; // Oyunun başlama zamanını tutar
    private boolean allowEnemySpawning = false; // Dalga bilgisi gösterilirken düşman çıkmasını engellemek için
    private static final int TARGET_FPS = 60;
    private static final long OPTIMAL_TIME = 1000 / TARGET_FPS; // Her kare için hedeflenmiş milisaniye süresi
    private static final int WAVE_DURATION = 8000; // Her dalganın milisaniye cinsinden müddeti

    public GamePanel() {
        initializePanel(); // Panel hazırlıklarını yapar
        generateWordsForWave(); // İlk dalga için kelimeleri oluşturur
        gameStartTime = System.currentTimeMillis(); // Oyunun başlangıç anını tayin eder
        waveInfoStartTime = System.currentTimeMillis(); // Dalga bilgisi için başlangıç zamanını tayin eder
    }

    private void initializePanel() {
        setBackground(Color.BLACK); // Arkaplan rengini siyah tayin eder
        setFocusable(true); // Panelin klavye girdilerini almasını sağlar
        addKeyListener(this); // Klavye olaylarını dinlemek için ekleme yapar
    }

    private void generateWordsForWave() {
        words.clear(); // Mevcut kelimeleri boşaltır
        pendingWords.clear(); // Bekleyen kelimeleri de boşaltır

        List<String> wordList = readWordsFromFile("words.txt"); // Dosyadan kelimeleri okur
        for (int i = 0; i < waveWordCount; i++) {
            String word = wordList.get(random.nextInt(wordList.size())); // Rastgele bir kelime seçer
            Word newWord = createWord(word); // Yeni bir kelime nesnesi üretir
            pendingWords.add(newWord); // Bekleyen kelimeler listesine ekler
            newWord.spawnTime = System.currentTimeMillis() + random.nextInt(WAVE_DURATION); // Kelimenin çıkış anını ayarlar
        }
    }

    private Word createWord(String wordText) {
        int x = random.nextInt(900); // Rastgele x konumu
        int y = 0; // Ekranın tepesinden başlar
        Word newWord = new Word(wordText, x, y);

        // Kelimenin hız ihtimalini belirler
        int probability = random.nextInt(10); // 0 ile 9 arası değer üretir
        if (probability < 6) {
            newWord.speed = 1; // %60 sürat 1
        } else if (probability < 9) {
            newWord.speed = 2; // %30 sürat 2
        } else {
            newWord.speed = 3; // %10 sürat 3
        }

        return newWord;
    }

    private List<String> readWordsFromFile(String filename) {
        // İlgili kelime dosyasını okur ve listeye atar
        List<String> wordList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                wordList.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Her daim görüntülenmesi gereken sabit öğeleri çizer
        drawWords(g2d); // Kelimeleri resmeder
        drawExplosions(g2d); // Patlamaları çizer
        drawUserInput(g2d); // Oyuncunun girdiği metni gösterir
        drawPlayerCharacter(g2d); // Oyuncu karakterini çizer
        drawPlayerLives(g2d); // Oyuncunun can adedini gösterir
        drawWaveNumber(g2d); // Mevcut dalga sayısını ekrana koyar
        drawGameTimer(g2d); // Oyun zamanını gösterir
        drawScore(g2d); // Puanı gösterir
        drawCombo(g2d); // Kombo değerini gösterir

        // Dalga bilgisi gösterilmesi gerekiyorsa onu da çizer
        if (showWaveInfo) {
            drawWaveInfo(g2d); // Dalga başlama bilgisini ekrana nakleder
        }
    }

    private void drawWaveInfo(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String waveText = "Wave " + currentWave; // Dalgaya dair metni oluşturur
        int textWidth = g.getFontMetrics().stringWidth(waveText);
        g.drawString(waveText, (getWidth() - textWidth) / 2, getHeight() / 2 - 20);

        // Sayaç kısmını yazar
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        long elapsed = (System.currentTimeMillis() - waveInfoStartTime) / 1000; // Dalga bilgisinin yayınlanma süresi
        int countdown = 3 - (int) elapsed;
        if (countdown >= 0) {
            String countdownText = "Starting in " + countdown;
            int countdownWidth = g.getFontMetrics().stringWidth(countdownText);
            g.drawString(countdownText, (getWidth() - countdownWidth) / 2, getHeight() / 2 + 20);
        } else {
            allowEnemySpawning = true; // Dalga başlamışsa düşman doğumuna izin ver
        }
    }

    private void drawWaveNumber(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String waveText = "Wave: " + currentWave; // Dalga sayısını metin hâline getirir
        g.drawString(waveText, getWidth() - 120, 30);
    }

    private void drawGameTimer(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000; // Oyun başladığından beri geçen saniye
        String timerText = "Time: " + elapsed + "s";
        g.drawString(timerText, getWidth() - 120, 60);
    }

    private void drawScore(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        String scoreText = "Score: " + score; // Puan yazısı
        g.drawString(scoreText, 10, 60);
    }

    private void drawCombo(Graphics2D g) {
        if (combo > 0) {
            g.setColor(Color.MAGENTA);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String comboText = "Combo: " + combo + "x"; // Kombo bilgisini metinleştirir
            int textWidth = g.getFontMetrics().stringWidth(comboText);
            g.drawString(comboText, (getWidth() - textWidth) / 2, 40);
        }
    }

    private void drawWords(Graphics2D g) {
        for (Word word : words) {
            // Kelimenin metnini ekrana yazar
            g.setColor(Color.WHITE);
            g.drawString(word.text, word.x, word.y);

            // Kelimenin altında, hızı temsil eden düşmanı çizer
            if (word.speed == 1) {
                g.setColor(Color.BLUE);
                g.fillRect(word.x, word.y + 5, 60, 60); // Yavaş düşman boyutları
            } else if (word.speed == 2) {
                g.setColor(Color.YELLOW);
                g.fillRect(word.x, word.y + 5, 40, 40); // Orta hız düşman boyutları
            } else if (word.speed == 3) {
                g.setColor(Color.RED);
                g.fillRect(word.x, word.y + 5, 20, 20); // Hızlı düşman boyutları
            }
        }
    }

    private void drawExplosions(Graphics2D g) {
        explosions.removeIf(Explosion::isExpired); // Süresi dolan patlamaları listeden siler
        for (Explosion explosion : explosions) {
            explosion.update(); // Patlamayı günceller
            explosion.draw(g); // Patlamayı çizer
        }
    }

    private void drawUserInput(Graphics2D g) {
        g.setColor(Color.GREEN);
        FontMetrics fm = g.getFontMetrics();
        int inputWidth = fm.stringWidth(inputText);
        int centeredX = playerX + 25 - inputWidth / 2; // Oyuncu karakterinin üzerinde ortalamak için hesaplama
        g.drawString(inputText, centeredX, playerY - 20);
    }

    private void drawPlayerCharacter(Graphics2D g) {
        // Oyuncu karakterini meydana getirir
        g.setColor(Color.WHITE);
        playerX = getWidth() / 2 - 25; // Ekranın ortası
        playerY = getHeight() - 100;  // Ekranın altına yakın
        g.fillOval(playerX, playerY, 50, 50); // Oyuncunun karakter silüeti
    }

    private void drawPlayerLives(Graphics2D g) {
        g.setColor(Color.RED);
        g.drawString("Lives: " + playerLives, 10, 20); // Oyuncunun can adedini metin olarak sunar
    }

    public void start() {
        running = true; // Oyun döngüsünü aktif hale getirir
        new Thread(this).start(); // Yeni bir iş parçacığı üzerinden oyunu başlatır
    }

    @Override
    public void run() {
        long lastTime = System.currentTimeMillis(); // Döngünün son kaydedilen zamanını tutar

        while (running) {
            long now = System.currentTimeMillis(); // Mevcut zaman
            long updateLength = now - lastTime; // Bir önceki kareden bu kareye kadar geçen süre
            lastTime = now; // Zamanı günceller

            // Dalga bilgisinin 3 saniyeden fazla gösterilmeyeceğini kararlaştırır
            if (showWaveInfo && (System.currentTimeMillis() - waveInfoStartTime >= 3000)) {
                showWaveInfo = false; 
                allowEnemySpawning = true; 
            }

            // Dalga bilgisi gösterilmiyorsa ve düşman doğumuna izin varsa
            if (!showWaveInfo && allowEnemySpawning) {
                spawnPendingWords(now); // Doğma vakti gelmiş kelimeleri sahneye atar
                updateWordPositions();  // Kelimelerin konumunu yeniler
                checkUserInput();       // Oyuncunun girdiği metni kontrol eder
                checkCollisions();      // Oyuncu ile kelimeler çarpışmış mı diye bakar
            }

            repaint(); // Ekranı tazeler

            // Oyuncunun canı bitmişse oyun döngüsünü durdurur
            if (playerLives <= 0) {
                running = false;
                System.out.println("Game Over"); // Oyun bitti mesajı
            }

            // Ekranda kelime kalmamış ve bekleyen de yoksa yeni dalgaya geç
            if (words.isEmpty() && pendingWords.isEmpty() && !showWaveInfo) {
                advanceToNextWave(); // Dalga sayısını artırarak yeni dalga başlatır
                allowEnemySpawning = false; 
            }

            // Hedeflenen FPS'ye göre bir sonraki kareye kadar uyutma
            long sleepTime = OPTIMAL_TIME - (System.currentTimeMillis() - now);
            if (sleepTime > 0) {
                sleep(sleepTime);
            }
        }
    }

    private void spawnPendingWords(long currentTime) {
        // Doğma vakti gelen kelimeleri sahneye atar
        List<Word> toSpawn = new ArrayList<>();
        for (Word word : pendingWords) {
            if (currentTime >= word.spawnTime) {
                toSpawn.add(word);
            }
        }
        words.addAll(toSpawn);       // Sahnedeki kelimelere ekler
        pendingWords.removeAll(toSpawn); // Bekleyenlerden çıkarır
    }

    private void updateWordPositions() {
        // Her kelimenin dikey konumunu hızlarına göre değiştirir
        words.forEach(word -> {
            word.y += word.speed; 
            // Ekran dışında kalırlarsa tekrar yukarıdan başlatır
            if (word.y > getHeight()) {
                word.y = 0;
                word.x = random.nextInt(600);
            }
        });
    }

    private void checkUserInput() {
        // Oyuncunun girdiği metinle sahnedeki kelimeleri eşleştirir
        List<Word> matchedWords = new ArrayList<>();
        for (Word word : words) {
            if (word.text.equalsIgnoreCase(inputText)) {
                matchedWords.add(word); 
                explosions.add(new Explosion(word.x, word.y)); // Patlama ekler
                score += 1 + combo; // Skoru komboya göre artırır
                combo++; // Komboyu yükseltir
            }
        }
        words.removeAll(matchedWords);
        if (!matchedWords.isEmpty()) {
            inputText = ""; // Eşleşme varsa girişi sıfırlar
        }
    }

    private void checkCollisions() {
        // Oyuncu ile kelimelerin alanları çakışıyor mu diye bakar
        words.removeIf(word -> {
            Rectangle playerBounds = new Rectangle(playerX, playerY, 50, 50);
            Rectangle wordBounds = new Rectangle(word.x, word.y, 50, 50);
            if (playerBounds.intersects(wordBounds)) {
                playerLives--; // Çarpışma varsa oyuncu can kaybeder
                return true; // Kelime sahneden silinir
            }
            return false;
        });
    }

    private void advanceToNextWave() {
        // Bir sonraki dalgaya geçiş yapar, dalga sayılarını ve kelime miktarını günceller
        currentWave++;
        waveWordCount = currentWave; // Dalgadaki kelime miktarını dalga sayısına eşit kılar
        showWaveInfo = true; 
        waveInfoStartTime = System.currentTimeMillis(); 
        generateWordsForWave(); 
    }

    private void sleep(long milliseconds) {
        // Verilen süre kadar iş parçacığını uyutur
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Klavyeden tuş basımıyla ilgili metni yakalar
        char keyChar = e.getKeyChar();
        if (Character.isLetterOrDigit(keyChar)) {
            inputText += keyChar; // Harf veya rakamsa girdi metnine ekler
        } else if (keyChar == '\b' && inputText.length() > 0) {
            // Geri tuşu basılmışsa ve metin boş değilse son karakteri siler
            inputText = inputText.substring(0, inputText.length() - 1);
            combo = 0; // Geri tuşu basıldığı anda kombo sıfırlanır
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Tuş basıldığı an; şimdilik bir eylem yapmıyoruz
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Tuş bırakıldığı an; şimdilik bir eylem yapmıyoruz
    }
}
