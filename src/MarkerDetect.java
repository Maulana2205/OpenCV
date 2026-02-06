import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.ArucoDetector;
import org.opencv.objdetect.DetectorParameters;
import org.opencv.objdetect.Dictionary;
import org.opencv.objdetect.Objdetect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MarkerDetect {

    static {
        try {
            System.load("C:\\Users\\USER\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Gagal memuat library OpenCV.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            // 1. Load Gambar
            String inputImageName = "aruco_marker_42.png";
            Mat originalImage = Imgcodecs.imread(inputImageName);

            if (originalImage.empty()) {
                System.err.println("Gambar tidak ditemukan!");
                return;
            }

            //Ubah ke Grayscale (Hitam Putih) agar lebih mudah dibaca
            Mat grayImage = new Mat();
            if (originalImage.channels() > 1) {
                Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_BGR2GRAY);
            } else {
                grayImage = originalImage.clone();
            }

            //Tambahkan Margin Putih (Padding)
            Mat paddedImage = new Mat();
            int marginSize = 50; // Menambah 50 pixel putih di setiap sisi
            Core.copyMakeBorder(grayImage, paddedImage, marginSize, marginSize, marginSize, marginSize,
                    Core.BORDER_CONSTANT, new Scalar(255, 255, 255)); // 255 = Putih

            // ------------------------

            // 2. Setup Detektor
            Dictionary dictionary = Objdetect.getPredefinedDictionary(Objdetect.DICT_6X6_250);
            DetectorParameters parameters = new DetectorParameters();

            // Opsi tambahan: Meningkatkan sensitivitas (opsional)
            parameters.set_adaptiveThreshWinSizeMin(3);
            parameters.set_adaptiveThreshWinSizeMax(23);

            ArucoDetector detector = new ArucoDetector(dictionary, parameters);

            // 3. Deteksi
            List<Mat> corners = new ArrayList<>();
            Mat ids = new Mat();
            detector.detectMarkers(paddedImage, corners, ids);

            // 4. Hasil
            if (ids.total() > 0) {
                System.out.println("SUKSES! Ditemukan " + ids.total() + " marker.");
                System.out.println("ID yang terdeteksi: " + ids.dump()); // Dump mencetak isi matriks

                // Visualisasi pada gambar yang sudah di-padding
                // convert balik ke BGR agar garis kotak bisa berwarna hijau
                Mat outputImage = new Mat();
                Imgproc.cvtColor(paddedImage, outputImage, Imgproc.COLOR_GRAY2BGR);

                Objdetect.drawDetectedMarkers(outputImage, corners, ids);
                Imgcodecs.imwrite("detected_AR.png", outputImage);
                System.out.println("Lihat hasil visualisasi di: detected_fixed.png");
            } else {
                System.out.println("GAGAL: Masih belum terdeteksi.");
                System.out.println("Coba cek: Apakah Dictionary saat generate benar-benar DICT_6X6_250?");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}