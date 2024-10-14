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
    private int x;
    private int y;
    private int width;
    private int height;

    private Frame(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

    // Getters
    private int getX() { return x; }
    private int getY() { return y; }
    private int getWidth() { return width; }
    private int getHeight() { return height; }
  }

  // Color property
  private Color color;

  // Enum for alignment
  private enum Alignment {
    LEFT,
    RIGHT,
    CENTERED
  }

  private String rawText;
  private Alignment alignment;
  private Frame frame;

  private Content(Frame frame, String hexColor, Alignment alignment, String text) {
    this.frame = frame;
    this.color = Color.decode(hexColor);
    this.alignment = alignment;
    this.rawText = text;
  }

  public static final Content UMICO_LOAN_BANNER_1_EN = new Content(new Frame(50, 50, 700, 190), "#25282B", Alignment.LEFT, "ENglisht покупки на %d ₼, которые мы выделили для вас");
  public static final Content UMICO_LOAN_BANNER_1_RU = new Content(new Frame(50, 50, 700, 190), "#25282B", Alignment.LEFT, "Совершайте покупки на %d ₼, которые мы выделили для вас");
  public static final Content UMICO_LOAN_BANNER_1_AZ = new Content(new Frame(50, 50, 700, 190), "#25282B", Alignment.LEFT, "Azerbaijani покупки на %d ₼, которые мы выделили для вас");

  public String drawText(int amount) {
    String filename = UUID.randomUUID().toString();
    try {
      // Load the image
      BufferedImage image = ImageIO.read(new File("input.png"));

      // Create a Graphics2D object
      Graphics2D g2d = image.createGraphics();

      String text = String.format(rawText, amount);

      // Set the font and color
      g2d.setFont(new Font("Serif", Font.PLAIN, 40));
      g2d.setColor(new Color(37, 40, 43));

      int frameX = this.frame.getX(), frameY = this.frame.getY(), frameWidth = this.frame.getWidth(), frameHeight = this.frame.getHeight();
      // uncomment to see the draw frame for debugging
      // g2d.drawRect(frameX, frameY, frameWidth, frameHeight);

      // Wrap text into lines
      FontMetrics fm = g2d.getFontMetrics();
      List<String> lines = wrapText(text, fm, frameWidth);

      // Calculate starting y position
      int lineHeight = fm.getHeight();
      int startY = frameY + (frameHeight - lines.size() * lineHeight) / 2 + fm.getAscent();

      // Draw each line of text within the frame
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
        // Move to the next line
        currentY += lineHeight; 
      }

      // Dispose the Graphics2D object
      g2d.dispose();

      // Save the modified image
      ImageIO.write(image, "png", new File("output/", filename + ".png"));
      System.out.println("Output file" + filename + ".png");
    } catch (IOException e) {
      e.printStackTrace();
    }

    return filename;
  }

  // Method to wrap text into lines based on the maximum width
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
