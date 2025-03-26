import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.util.Scanner;

public class VoiceMessageApp {
    private static final String RECORD_FILE_PATH = "recorded_voice.wav"; // Save recorded file
    private static final String UPLOAD_DIR = "uploaded_voices/"; // Folder for uploaded voices

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Record a Voice Message");
            System.out.println("2. Upload a Voice Message (Only Female Voice Allowed)");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    recordVoice();
                    break;
                case 2:
                    uploadVoice();
                    break;
                case 3:
                    System.out.println("Exiting program.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Method to record voice
    public static void recordVoice() {
        try {
            File file = new File(RECORD_FILE_PATH);
            AudioFormat format = new AudioFormat(16000, 8, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Recording not supported!");
                return;
            }

            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            System.out.println("Recording... Press ENTER to stop.");

            Thread stopper = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(line)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            stopper.start();
            new Scanner(System.in).nextLine(); // Wait for Enter key
            line.stop();
            line.close();

            System.out.println("Recording saved to: " + file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to upload voice file (only female voice allowed)
    public static void uploadVoice() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Upload a Female Voice File");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName().toLowerCase();

            if (!fileName.contains("female")) { // Basic validation (Modify for AI-based check)
                System.out.println("Only female voice files can be uploaded!");
                return;
            }

            File uploadFolder = new File(UPLOAD_DIR);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            File destinationFile = new File(UPLOAD_DIR + selectedFile.getName());
            try (InputStream in = new FileInputStream(selectedFile);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                System.out.println("File uploaded successfully: " + destinationFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("File upload failed!");
            }
        }
    }
}
