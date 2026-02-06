import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.objdetect.Dictionary;
import org.opencv.objdetect.Objdetect;
import org.opencv.imgcodecs.Imgcodecs;

public class markerGenerate {

    static {
        // Memuat library native OpenCV
        try {
            System.load("C:\\Users\\USER\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Gagal memuat library OpenCV");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            // 1. Konfigurasi Marker
            int markerId = 42;       // ID marker
            int borderBits = 1;      // Lebar border
            int imageSize = 400;     // Ukuran gambar (400x400)

            String fileName = "aruco_marker_" + markerId + ".png";

            // 2. Memilih Dictionary

            Dictionary dictionary = Objdetect.getPredefinedDictionary(Objdetect.DICT_6X6_250);

            // 3. Generate Marker
            Mat markerImage = new Mat();

            Objdetect.generateImageMarker(dictionary, markerId, imageSize, markerImage, borderBits);

            // 4. Simpan ke file
            Imgcodecs.imwrite(fileName, markerImage);

            System.out.println("Berhasil membuat ArUco Marker!");
            System.out.println("File tersimpan sebagai: " + fileName);
            System.out.println("ID: " + markerId);
            System.out.println("Dictionary: 6x6_250");

        } catch (Exception e) {
            System.err.println("Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}