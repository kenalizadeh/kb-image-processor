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

public class ImageTextAdder {
    public static void main(String[] args) {
        try {
            // Load the image
            BufferedImage image = ImageIO.read(new File("image.jpg"));

            // Create a Graphics2D object
            Graphics2D g2d = image.createGraphics();

            // Set the font and color
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.setColor(Color.RED);

            // Define the frame for the text
            int frameX = 50, frameY = 50, frameWidth = 400, frameHeight = 200;
            
            // Sample multiline text
            String text = "This is an example of multiline text that needs to be wrapped within a specific frame. Make sure it fits nicely within the boundaries of the frame.";

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
                int x = frameX + (frameWidth - textWidth) / 2; // Center text horizontally
                g2d.drawString(line, x, currentY);
                currentY += lineHeight; // Move to the next line
            }

            // Dispose the Graphics2D object
            g2d.dispose();

            // Save the modified image
            ImageIO.write(image, "jpg", new File("output_image.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
