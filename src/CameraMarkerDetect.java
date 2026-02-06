import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture; // Import untuk Kamera
import org.opencv.highgui.HighGui;       // Import untuk menampilkan Window GUI
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.ArucoDetector;
import org.opencv.objdetect.DetectorParameters;
import org.opencv.objdetect.Dictionary;
import org.opencv.objdetect.Objdetect;

import java.util.ArrayList;
import java.util.List;

public class CameraMarkerDetect {

    static {
        try {
            System.load("C:\\Users\\USER\\Downloads\\opencv\\build\\java\\x64\\opencv_java4120.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Gagal memuat library OpenCV: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // 1. Inisialisasi Kamera 0:kamera device, 1/2: kamera eksternal
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.err.println("Error: Kamera tidak terdeteksi!");
            return;
        }

        // 2. Setup Detektor
        Dictionary dictionary = Objdetect.getPredefinedDictionary(Objdetect.DICT_6X6_250);
        DetectorParameters parameters = new DetectorParameters();

        // Tuning parameter agar lebih responsif di video
        parameters.set_adaptiveThreshWinSizeMin(3);
        parameters.set_adaptiveThreshWinSizeMax(23);
        parameters.set_adaptiveThreshWinSizeStep(10);

        ArucoDetector detector = new ArucoDetector(dictionary, parameters);

        Mat frame = new Mat();
        List<Mat> corners = new ArrayList<>();
        Mat ids = new Mat();

        System.out.println("Tekan 'Esc' atau tutup jendela untuk berhenti.");

        // 3. Loop Real-time
        while (true) {
            // Baca frame dari kamera
            if (!camera.read(frame)) {
                System.out.println("Gagal membaca frame kamera.");
                break;
            }

            // Bersihkan list corners dari frame sebelumnya agar tidak menumpuk
            corners.clear();
            ids = new Mat(); // Reset Mat ID

            // Deteksi Marker
            detector.detectMarkers(frame, corners, ids);

            // 4. Visualisasi
            if (ids.total() > 0) {
                // Gambar kotak hijau di sekeliling marker
                Objdetect.drawDetectedMarkers(frame, corners, ids, new Scalar(0, 255, 0));
                System.out.println("Terdeteksi: " + ids.dump());
            }

            // Tampilkan hasil di jendela popup
            HighGui.imshow("ArUco Detection Real-Time", frame);

            // Tunggu input keyboard selama 30ms (untuk memberi waktu refresh window)
            // Jika tombol Esc (kode 27) ditekan, keluar loop
            if (HighGui.waitKey(30) == 27) {
                break;
            }
        }

        // 5. Cleanup
        camera.release();
        HighGui.destroyAllWindows();
        System.out.println("Program selesai.");
    }
}