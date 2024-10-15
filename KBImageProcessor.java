import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KBImageProcessor {
  public static void main(String[] args) {
    Content.UMICO_LOAN_BANNER_1_AZ.drawText(15000);
    Content.UMICO_LOAN_BANNER_1_RU.drawText(10000);
    Content.UMICO_LOAN_BANNER_1_EN.drawText(2000);
  }
}

class Content {
  private static class Frame {
    private int x, y, width, height;

    private Frame(int x, int y, int width, int height) {
      this.x = x; this.y = y; this.width = width; this.height = height;
    }

    // Getters
    private int getX() { return x; }
    private int getY() { return y; }
    private int getWidth() { return width; }
    private int getHeight() { return height; }
  }

  private enum Alignment {
    LEFT, RIGHT, CENTERED
  }

  private Frame frame;
  private Color color;
  private Alignment alignment;
  private String rawText;
  private Font font;
  private int lineHeight;

  private Content(Frame frame, String hexColor, Alignment alignment, String text, int fontSize, int lineHeight) {
    this.frame = frame;
    this.color = Color.decode(hexColor);
    this.alignment = alignment;
    this.rawText = text;
    this.lineHeight = lineHeight;

    try {
      Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("font/SF-Pro-Display-Medium.otf"));
      this.font = customFont.deriveFont(Font.PLAIN, fontSize);
    } catch (Exception e) {
      e.printStackTrace();
      this.font = new Font("Serif", Font.PLAIN, fontSize);
    }
  }

  public static final Content UMICO_LOAN_BANNER_1_EN = new Content(
      new Frame(50, 50, 700, 190), 
      "#25282B", 
      Alignment.LEFT, 
      "Make purchases with the %d ₼ we have allocated for you",
      40,
      20
      );
  public static final Content UMICO_LOAN_BANNER_1_RU = new Content(
      new Frame(50, 50, 700, 190), 
      "#25282B", 
      Alignment.LEFT, 
      "Совершайте покупки на %d ₼, которые мы выделили для вас",
      40,
      20
      );
  public static final Content UMICO_LOAN_BANNER_1_AZ = new Content(
      new Frame(50, 50, 700, 190), 
      "#25282B", 
      Alignment.LEFT, 
      "Məhz sizin üçün ayırdığımız %d ₼ məbləğlə alış-veriş edin",
      40,
      20
      );

  public String drawText(int amount) {
    String filename = UUID.randomUUID().toString();
    try {
      BufferedImage image = ImageIO.read(new File("input.png"));
      Graphics2D g2d = image.createGraphics();

      String text = String.format(rawText, amount);
      g2d.setFont(this.font);
      g2d.setColor(color);

      int frameX = frame.getX(), frameY = frame.getY(), frameWidth = frame.getWidth(), frameHeight = frame.getHeight();
      // Uncomment for debugging the text draw frame
      // g2d.drawRect(frameX, frameY, frameWidth, frameHeight);

      FontMetrics fm = g2d.getFontMetrics();
      List<String> lines = wrapText(text, fm, frameWidth);

      int totalTextHeight = lines.size() * fm.getHeight() + (lines.size() - 1) * lineHeight;
      int startY = frameY + (frameHeight - totalTextHeight) / 2 + fm.getAscent();

      int currentY = startY;
      for (String line : lines) {
        int textWidth = fm.stringWidth(line);
        int x = switch (alignment) {
          case LEFT -> frameX;
          case CENTERED -> frameX + (frameWidth - textWidth) / 2;
          case RIGHT -> frameX + frameWidth - textWidth;
          default -> 0;
        };

        g2d.drawString(line, x, currentY);
        currentY += fm.getHeight() + lineHeight;
      }

      g2d.dispose();
      ImageIO.write(image, "png", new File("output/", filename + ".png"));
      System.out.println("Output file: " + filename + ".png");
    } catch (IOException e) {
      e.printStackTrace();
    }

    return filename;
  }

  private static List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
    List<String> lines = new ArrayList<>();
    String[] words = text.split(" ");
    StringBuilder line = new StringBuilder();

    for (String word : words) {
      String testLine = line.toString() + (line.length() > 0 ? " " : "") + word;
      if (fm.stringWidth(testLine) > maxWidth) {
        if (line.length() > 0) {
          lines.add(line.toString());
          line = new StringBuilder();
        }
        line.append(word);
      } else {
        line.append((line.length() > 0 ? " " : "") + word);
      }
    }
    if (line.length() > 0) {
      lines.add(line.toString());
    }

    return lines;
  }
}
